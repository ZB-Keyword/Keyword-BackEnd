package DevHeaven.keyword.domain.chat.controller;

import DevHeaven.keyword.common.exception.util.ResponseUtils;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.domain.chat.dto.response.ChatResponse;
import DevHeaven.keyword.domain.chat.dto.response.ChatRoomListResponse;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    MemberAdapter memberAdapter =
            MemberAdapter.from(MemberFixture.DOG.createMember());


    @Test
    @DisplayName("채팅방 생성 - 성공")
    @WithCustomMockUser
    void createChatRoom() throws Exception {
        //given

        Long scheduleId = 1L;

        given(chatRoomService.createChatRoom(
                memberAdapter, scheduleId))
                .willReturn(true);

        //when
        //then
        mockMvc.perform(
                        post("/chats/room/{scheduleId}", scheduleId)
                                .header("Authorization", "Bearer AccessToken")

                )
                .andDo(print())
                .andExpect(status().isOk())
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
                                .header("Authorization", "Bearer AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
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
    @DisplayName("채팅방 목록 가져오기")
    @WithCustomMockUser
    void getChatRoomList() throws Exception {
        //given

        Pageable pageable = PageRequest.of(0, 10);

        List<String> friendsName = Arrays.asList("버니", "벅스", "강아지");

        ChatRoomListResponse response1 =
                ChatRoomListResponse.builder()
                        .chatRoomId(1L)
                        .scheduleTitle("크리스마스 모임")
                        .friendsName(friendsName)
                        .build();
        ChatRoomListResponse response2 =
                ChatRoomListResponse.builder()
                        .chatRoomId(2L)
                        .scheduleTitle("생일파티")
                        .friendsName(friendsName)
                        .build();
        ChatRoomListResponse response3 =
                ChatRoomListResponse.builder()
                        .chatRoomId(3L)
                        .scheduleTitle("송별회")
                        .friendsName(friendsName)
                        .build();

        Page<ChatRoomListResponse> pageResult =
                new PageImpl<>(Arrays.asList(response1, response2, response3));

        given(chatRoomService.getChatRoomList(memberAdapter, pageable))
                .willReturn(pageResult);
        //when
        //then
        mockMvc.perform(
                        get("/chats/room")
                                .header("Authorization", "Bearer AccessToken")
                                .param("size", "10")
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].scheduleTitle").value("크리스마스 모임"))
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 목록 조회 API")
                                .description("로그인한 사용자가 포함된 채팅방 목록 조회")
                                .responseSchema(Schema.schema("ChatRoomListResponse"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestParameters(
                                parameterWithName("size").description("Description of size parameter"),
                                parameterWithName("page").description("Description of page parameter")
                        )
                        , responseFields(
                                fieldWithPath("content[].chatRoomId").description("채팅방 번호")
                                , fieldWithPath("content[].scheduleTitle").description("일정 제목")
                                , fieldWithPath("content[].friendsName").description("일정 참여 멤버")

                                , fieldWithPath("pageable").type(JsonFieldType.STRING).description("페이지 정보")
                                , fieldWithPath("last").description("마지막 페이지인지 여부")
                                , fieldWithPath("totalElements").description("테이블 총 데이터 갯수")
                                , fieldWithPath("totalPages").description("전체 페이지 갯수")
                                , fieldWithPath("size").description("한 페이지당 조회할 데이터 갯수")
                                , fieldWithPath("number").description("현재 페이지 번호")

                                , fieldWithPath("sort.empty").description("데이터 비었는지 여부")
                                , fieldWithPath("sort.sorted").description("정렬 여부")
                                , fieldWithPath("sort.unsorted").description("정렬 안 됐는지 여부")

                                , fieldWithPath("first").description("첫 번째 페이지인지 여부")
                                , fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 갯수")
                                , fieldWithPath("empty").description("데이터 비었는지 여부")

                        )


                ));
    }

    @Test
    @DisplayName("채팅방 입장 테스트")
    @WithCustomMockUser
    void enterChatRoom() throws Exception {
        //given
        ChatResponse chatResponse1 =
                ChatResponse.builder()
                        .sender("버니")
                        .imageUrl("image.png")
                        .message("안녕하세요.")
                        .build();

        ChatResponse chatResponse2 =
                ChatResponse.builder()
                        .sender("벅스")
                        .imageUrl("image.png")
                        .message("네 ~ 반갑습니다.")
                        .build();

        List<ChatResponse> chatResponseList =
                Arrays.asList(chatResponse1, chatResponse2);

        Long chatRoomId = 1L;

        given(chatRoomService.enterChatRoom(memberAdapter, chatRoomId))
                .willReturn(chatResponseList);

        //when
        //then
        mockMvc.perform(
                        get("/chats/room/{chatRoomId}", chatRoomId)
                                .header("Authorization", "Bearer AccessToken")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("chats/room/enter/success",
                        ResourceSnippetParameters.builder()
                                .tag("Chat API")
                                .summary("채팅방 입장 API")
                                .description("채팅방 입장 후 이전 메세지 확인 가능")
                                .requestHeaders(
                                        headerWithName("Authorization").description("Bearer AccessToken")
                                )
                                .pathParameters(parameterWithName("chatRoomId").description("입장할 채팅방 Id"))
                                .responseSchema(Schema.schema("ChatResponse"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , responseFields(
                                fieldWithPath("[].sender").description("송신자"),
                                fieldWithPath("[].imageUrl").description("프로필 이미지 url"),
                                fieldWithPath("[].message").description("전송 메세지")
                        )
                ));
    }
}