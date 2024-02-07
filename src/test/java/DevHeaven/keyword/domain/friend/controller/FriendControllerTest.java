package DevHeaven.keyword.domain.friend.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.FRIEND_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.FRIEND_REQUEST_ALREADY;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.NOTICE_NOT_FOUND;
import static DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest.REQUEST;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.NoticeException;
import DevHeaven.keyword.domain.friend.dto.request.FriendListStatusRequest;
import DevHeaven.keyword.domain.friend.dto.response.FriendListResponse;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.ControllerTest;
import DevHeaven.keyword.support.fixture.MemberFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class FriendControllerTest extends ControllerTest {

  @Test
  @DisplayName("내 친구 리스트 가져오기-성공")
  @WithCustomMockUser
  void getFriendList_success() throws Exception {
    //given
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());
    FriendListStatusRequest statusRequest = REQUEST;
    Pageable pageable = PageRequest.of(0,10);
    FriendListResponse response1 = FriendListResponse.from(MemberFixture.CAT.createMember());
    FriendListResponse response2 = FriendListResponse.from(MemberFixture.PANDA.createMember());
    FriendListResponse response3 = FriendListResponse.from(MemberFixture.LION.createMember());

    List <FriendListResponse> responseList = Arrays.asList(response1,response2,response3);
    //when
    given(friendService.getFriendList(memberAdapter,statusRequest,1L,pageable)).willReturn(responseList);

    //then
    mockMvc.perform(get("/friends")
            .header("Authorization", "Bearer AccessToken")
            .queryParam("friend-state", statusRequest.toString())
            .queryParam("size",String.valueOf(10))
            .queryParam("page",String.valueOf(0))
            .queryParam("noticeId",String.valueOf(1))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseList)))
        .andDo(MockMvcRestDocumentationWrapper.document("friends/list/success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                friendListRequest()
            )));
  }
  @Test
  @DisplayName("내 친구 리스트 가져오기 - 실패 : member 검증 실패")
  @WithCustomMockUser
  void getFriendList_fail_not_member() throws Exception {
    //given
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());
    FriendListStatusRequest statusRequest = REQUEST;
    Pageable pageable = PageRequest.of(0,10);

    //when
    doThrow(new MemberException(MEMBER_NOT_FOUND)).when(friendService).getFriendList(memberAdapter,statusRequest,1L,pageable);

    //then
    mockMvc.perform(get("/friends")
            .header("Authorization", "Bearer AccessToken")
            .queryParam("friend-state", statusRequest.toString())
            .queryParam("size",String.valueOf(10))
            .queryParam("page",String.valueOf(0))
            .queryParam("noticeId",String.valueOf(1))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/list/fail/notMember",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                friendListRequest()
            )));
  }

  @Test
  @DisplayName("내 친구 리스트 가져오기 - 실패 : noticeid 찾을 수 없음")
  @WithCustomMockUser
  void getFriendList_fail_not_found_notice() throws Exception {
    //given
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());
    FriendListStatusRequest statusRequest = REQUEST;
    Pageable pageable = PageRequest.of(0,10);

    //when
    doThrow(new NoticeException(NOTICE_NOT_FOUND)).when(friendService).getFriendList(memberAdapter,statusRequest,1L,pageable);

    //then
    mockMvc.perform(get("/friends")
            .header("Authorization", "Bearer AccessToken")
            .queryParam("friend-state", statusRequest.toString())
            .queryParam("size",String.valueOf(10))
            .queryParam("page",String.valueOf(0))
            .queryParam("noticeId",String.valueOf(1))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/list/fail/notFoundNotice",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                friendListRequest()
            )));
  }
  private ResourceSnippetParameters friendListRequest() {
    return ResourceSnippetParameters.builder()
        .tag("Friend API")
        .summary("내 친구 리스트 API")
        .requestHeaders(
            headerWithName("Authorization").description("Bearer AccessToken")
        )
        .requestParameters(
            parameterWithName("friend-state").description("Description of friend-state parameter"),
            parameterWithName("size").description("Description of size parameter"),
            parameterWithName("page").description("Description of page parameter"),
            parameterWithName("noticeId").description("Description of noticeId parameter")
        )
        .build();
  }

  @Test
  @DisplayName("친구 요청 - 성공")
  @WithCustomMockUser
  void requestFriend_success() throws Exception {
    //given
    Long memberId = 1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    when(friendService.requestFriend(memberAdapter, memberId)).thenReturn(true);

    //then
    mockMvc.perform(post("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer AccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/request/success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Friend API")
                    .summary("친구 요청 API")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer AccessToken")
                    )
                    .pathParameters(parameterWithName("memberId").description("친구 요청할 Member Id"))
                    .build()
            )));

  }

  @Test
  @DisplayName("친구 요청 - 실패 : 회원을 찾을 수 없음")
  @WithCustomMockUser
  void requestFriend_fail_not_found_member() throws Exception {
    //given
    Long memberId = 1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    doThrow(new MemberException(MEMBER_NOT_FOUND)).when(friendService).requestFriend(memberAdapter,memberId);

    //then
    mockMvc.perform(post("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer AccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/request/fail/notFoundMember",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Friend API")
                    .summary("친구 요청 API")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer AccessToken")
                    )
                    .pathParameters(parameterWithName("memberId").description("친구 요청할 Member Id"))
                    .build()
            )));

  }

  @Test
  @DisplayName("친구 요청 - 실패 : 이미 친구임")
  @WithCustomMockUser
  void requestFriend_fail_not_found_friend() throws Exception {
    //given
    Long memberId = 1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    doThrow(new FriendException(FRIEND_REQUEST_ALREADY)).when(friendService).requestFriend(memberAdapter,memberId);

    //then
    mockMvc.perform(post("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer AccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/request/fail/alreadyFriend",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Friend API")
                    .summary("친구 요청 API")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer AccessToken")
                    )
                    .pathParameters(parameterWithName("memberId").description("친구 요청할 Member Id"))
                    .build()
            )));

  }

  @Test
  @DisplayName("친구 삭제 - 성공")
  @WithCustomMockUser
  void deleteFriend_success() throws Exception {
    //given
    Long memberId=1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    when(friendService.deleteFriend(eq(memberAdapter),eq(memberId))).thenReturn(true);
    //then
    mockMvc.perform(delete("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer AccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/delete/success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Friend API")
                    .summary("친구 삭제 API")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer AccessToken")
                    )
                    .pathParameters(parameterWithName("memberId").description("친구 삭제할 Member Id"))
                    .build()
            )));

    verify(friendService).deleteFriend(memberAdapter,memberId);
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구를 회워 DB에서 찾을 수 없음")
  @WithCustomMockUser
  void deleteFriend_fail_not_found_member() throws Exception {
    //given
    Long memberId = 1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    doThrow(new MemberException(MEMBER_NOT_FOUND)).when(friendService).deleteFriend(memberAdapter,memberId);

    //then
    mockMvc.perform(delete("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer yourAccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/delete/fail/notMember",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                friendDeleteErrorResponse()
            )));

    verify(friendService).deleteFriend(memberAdapter,memberId);
  }

  @Test
  @DisplayName("친구 삭제 - 실패 : 친구 목록에서 ACCEPT 인 상태를 찾을 수 없음")
  @WithCustomMockUser
  void deleteFriend_fail_not_found_accepted() throws Exception {
    //given
    Long memberId=1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());

    //when
    doThrow(new FriendException(FRIEND_NOT_FOUND)).when(friendService).deleteFriend(memberAdapter,memberId);

    //then
    mockMvc.perform(delete("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer yourAccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcRestDocumentationWrapper.document("friends/delete/fail/notFriend",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                friendDeleteErrorResponse()
            )));

    verify(friendService).deleteFriend(memberAdapter,memberId);
  }

  private ResourceSnippetParameters friendDeleteErrorResponse() {
    return ResourceSnippetParameters.builder()
        .tag("Friend API")
        .summary("친구 삭제 API")
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

}