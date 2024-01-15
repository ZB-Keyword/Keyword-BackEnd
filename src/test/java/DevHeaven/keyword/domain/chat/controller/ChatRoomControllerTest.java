package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.common.security.JwtExceptionFilter;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static DevHeaven.keyword.domain.member.type.MemberRole.MEMBER;
import static DevHeaven.keyword.domain.member.type.MemberStatus.ACTIVE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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
    private JwtExceptionFilter jwtExceptionFilter;

    @TestConfiguration
    private static class TestConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll();
        }
    }

    private MemberAdapter memberAdapter() {
        return MemberAdapter.builder()
                .email("abc@naver.com")
                .password("qwerty")
                .status(ACTIVE)
                .role(MEMBER)
                .build();
    }

    @Test
    @DisplayName("채팅방 생성 - 성공")
    void createChatRoomSuccess() throws Exception {
        //given
          given(chatRoomService.createChatRoom(memberAdapter(), 1L))
                .willReturn(true);

        Long scheduleId = 1L;
        //when
        //then
        mockMvc.perform(
                post("/chats/room/{scheduleId}", scheduleId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room/create/success",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 생성  API")
                                .description("일정 생성 후에 바로 동작히는 API로 채팅방 생성 및 일정 멤버에게 채팅방을 할당해줌.")
                                .responseSchema(Schema.schema("testResponse"))
                                .pathParameters(parameterWithName("scheduleId").description("생성된 일정 ID"))
                        ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())

                ));
    }

    @Test
    @DisplayName("채팅방 생성 - 실패")
    void createChatRoomFail() throws Exception {
        //given
        given(chatRoomService.createChatRoom(memberAdapter(), 3L))
                .willReturn(true);

        Long scheduleId = 3L;
        //when
        //then
        mockMvc.perform(
                        post("/chats/room/{scheduleId}", scheduleId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room/create/fail",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 생성  API")
                                .description("일정 생성 후에 바로 동작히는 API로 채팅방 생성 및 일정 멤버에게 채팅방을 할당해줌.")
                                .responseSchema(Schema.schema("testResponse"))
                                .pathParameters(parameterWithName("scheduleId").description("생성된 일정 ID"))
                        ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())

                ));
    }
}