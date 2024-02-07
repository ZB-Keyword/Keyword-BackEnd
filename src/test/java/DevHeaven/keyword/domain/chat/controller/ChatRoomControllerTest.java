package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.common.exception.util.ResponseUtils;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.fixture.MemberFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ChatRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private ResponseUtils responseUtils;

    @MockBean
    private MemberRepository memberRepository;

    private ResourceSnippetParameters friendDeleteErrorResponse() {
        return ResourceSnippetParameters.builder()
                .tag("Chat API")
                .summary("채팅방 생성 API")
                .requestHeaders(
                        headerWithName("Authorization").description("Bearer AccessToken")
                )
                .pathParameters(parameterWithName("memberId").description("친구 삭제할 Member Id"))
                .responseFields(
                        fieldWithPath("errorCode").type(JsonFieldType.STRING).description("친구 삭제 실패 code"),
                        fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("친구 삭제 실패 에러 메세지"),
                        fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("친구 삭제 실패 http status")
                )
                .responseSchema(Schema.schema("Friend Delete Response"))
                .build();
    }
    @Test
    @DisplayName("채팅방 생성 - 성공")
    @WithCustomMockUser
    void createChatRoom() throws Exception{
        //given
        MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

        Long scheduleId = 1L;

        given(chatRoomService.createChatRoom(memberAdapter, scheduleId))
                .willReturn(true);

        //when
        //then
        mockMvc.perform(
                        post("/chats/room/{scheduleId}", scheduleId)
                                .header("Authorization", "Bearer AccessToken")

                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room/create/success",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 생성  API")
                                .description("일정 생성 후에 바로 동작히는 API로 채팅방 생성 및 일정 멤버에게 채팅방을 할당해줌.")
                                .pathParameters(parameterWithName("scheduleId").description("생성된 일정 ID"))
                        ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())

                ));
    }

    @Test
    @DisplayName("채팅방 생성 - 실패")
    @WithCustomMockUser
    void createChatRoomFail() throws Exception {
        //given

        MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

        given(chatRoomService.createChatRoom(memberAdapter, 3L))
                .willReturn(false);

        Long scheduleId = 1L;
        //when
        //then
        mockMvc.perform(
                        post("/chats/room/{scheduleId}", scheduleId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"))
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room/create/fail",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 생성  API")
                                .description("일정 생성 후에 바로 동작히는 API로 채팅방 생성 및 일정 멤버에게 채팅방을 할당해줌.")
                                .pathParameters(parameterWithName("scheduleId").description("생성된 일정 ID"))
                        ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())

                ));
    }

    @Test
    void getChatRoomList() {
    }

    @Test
    void enterChatRoom() {
    }
}