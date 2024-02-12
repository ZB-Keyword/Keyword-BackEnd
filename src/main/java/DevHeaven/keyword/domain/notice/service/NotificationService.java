package DevHeaven.keyword.domain.notice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final RedisTemplate<String, String> redisTemplate;
  private final ChannelTopic channelTopic;

  public NotificationService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.channelTopic = new ChannelTopic("notificationChannel"); // 채널명 설정
  }

  public void sendNotification(String message) {
    redisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }

}
