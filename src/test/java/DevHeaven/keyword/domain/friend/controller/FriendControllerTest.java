package DevHeaven.keyword.domain.friend.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.FRIEND_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.ControllerTest;
import DevHeaven.keyword.support.fixture.MemberFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class FriendControllerTest extends ControllerTest {

  private   ResourceSnippetParameters friendDeleteErrorResponse() {
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

  @Test
  @DisplayName("친구 삭제 - 성공")
  @WithCustomMockUser
  void deleteFriend_success() throws Exception {
    //given
    Long memberId=1L;
    MemberAdapter memberAdapter = MemberAdapter.from(MemberFixture.DOG.createMember());
    when(friendService.deleteFriend(eq(memberAdapter),eq(memberId))).thenReturn(true);

    //when
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
}