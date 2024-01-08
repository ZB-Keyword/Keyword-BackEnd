package DevHeaven.keyword.domain.chat.service;

import static DevHeaven.keyword.common.exception.type.ErrorCode.CHATROOM_NOT_FOUND;

import DevHeaven.keyword.common.exception.ChatException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.ScheduleException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.chat.dto.ChatDTO;
import DevHeaven.keyword.domain.chat.dto.ChatRequest;
import DevHeaven.keyword.domain.chat.entity.Chat;
import DevHeaven.keyword.domain.chat.entity.ChatRoom;
import DevHeaven.keyword.domain.chat.repository.ChatRepository;
import DevHeaven.keyword.domain.chat.repository.ChatRoomRepository;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public Chat createChat(ChatRequest message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId())
            .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));
        Member sender = memberRepository.findById(message.getSenderId())
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Chat chat = ChatRequest.toEntity(chatRoom, sender, message.getContent());
        chatRepository.save(chat);

        return chat;
    }

}
