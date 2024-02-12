package DevHeaven.keyword.domain.notice.service;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.NoticeException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.domain.notice.dto.NoticeEvent;
import DevHeaven.keyword.domain.notice.dto.response.NoticeResponse;
import DevHeaven.keyword.domain.notice.entity.Notice;
import DevHeaven.keyword.domain.notice.repository.NoticeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class NoticeService {

  private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  private final RedisMessageListenerContainer redisMessageListenerContainer;
  private final ObjectMapper objectMapper;
  private final RedisOperations<String, NoticeResponse> eventRedisOperations;
  private final NoticeRepository noticeRepository;
  private final MemberRepository memberRepository;

  public NoticeService(RedisMessageListenerContainer redisMessageListenerContainer,
      ObjectMapper objectMapper, RedisOperations<String, NoticeResponse> eventRedisOperations,
      NoticeRepository noticeRepository, MemberRepository memberRepository) {
    this.redisMessageListenerContainer = redisMessageListenerContainer;
    this.objectMapper = objectMapper;
    this.eventRedisOperations = eventRedisOperations;
    this.noticeRepository = noticeRepository;
    this.memberRepository = memberRepository;
  }

  private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener // 이벤트 처리를 해주는 어노테이션
  @EventListener
  public void handleNoticeEvent(final NoticeEvent noticeEvent) {
    try {
      //멤버 조회
      final Member member = memberRepository.findById(noticeEvent.getMemberId())
          .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

      // 알림 생성
      final Notice notice = createNotice(noticeEvent, member);

      // 알림 저장
      noticeRepository.save(notice);

      // 레디스 알림 보내기
      final String channelName = getChannelName(member.getEmail());
      eventRedisOperations.convertAndSend(channelName, notice);

    } catch (Exception e) {
      throw new RuntimeException("Failed to send Redis notification", e);
    }
  }

  private Notice createNotice(final NoticeEvent noticeEvent, final Member member) {
    return Notice.builder()
        .id(member.getMemberId())
        .informationId(noticeEvent.getId())
        .type(noticeEvent.getNoticeType())
        .build();
  }

  public SseEmitter subscribe(final String email) throws IOException {
    final SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    emitter.send(SseEmitter.event()
        .id(email)
        .name("sse"));
    emitters.add(emitter);
    final MessageListener messageListener = (message, pattern) -> {
      final NoticeResponse notificationResponse = serialize(message);
      sendToClient(emitter, email, notificationResponse);
    };
    this.redisMessageListenerContainer.addMessageListener(messageListener,
        ChannelTopic.of(getChannelName(email)));
    checkEmitterStatus(emitter, messageListener);
    return emitter;
  }


  private NoticeResponse serialize(final Message message) {
    try {
      final Notice notification = this.objectMapper.readValue(message.getBody(), Notice.class);
      return NoticeResponse.from(notification);
    } catch (IOException e) {
      throw new NoticeException(ErrorCode.NOTICE_ERROR);
    }
  }

  private void sendToClient(final SseEmitter emitter, final String email, final Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(email)
          .name("sse")
          .data(data));
    } catch (IOException e) {
      emitters.remove(emitter);
      log.error("SSE 연결이 올바르지 않습니다. 해당 memberID={}", email);
    }
  }

  private String getChannelName(final String email) {
    return "sample:topics:" + email;
  }

  private void checkEmitterStatus(final SseEmitter emitter, final MessageListener messageListener) {
    emitter.onCompletion(() -> {
      emitters.remove(emitter);
      this.redisMessageListenerContainer.removeMessageListener(messageListener);
    });
    emitter.onTimeout(() -> {
      emitters.remove(emitter);
      this.redisMessageListenerContainer.removeMessageListener(messageListener);
    });
  }
}
