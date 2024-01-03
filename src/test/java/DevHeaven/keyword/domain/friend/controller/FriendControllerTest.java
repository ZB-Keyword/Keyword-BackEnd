package DevHeaven.keyword.domain.friend.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.FriendNotFoundException;
import DevHeaven.keyword.common.exception.MemberNotFoundException;
import DevHeaven.keyword.domain.friend.service.FriendService;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(FriendController.class)
class FriendControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private FriendService friendService;
  protected  ResourceSnippetParameters friendDeleteErrorResponse() {
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
  void deleteFriend_success() throws Exception {
    //given
    Long memberId=1L;

    //when
    //then
    mockMvc.perform(delete("/friends/{memberId}",memberId)
            .header("Authorization", "Bearer AccessToken")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.isFriendDelete").value(true))
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
                    .responseFields(
                        fieldWithPath("isFriendDelete").type(JsonFieldType.BOOLEAN).description("친구 삭제 성공 유무")
                    )
                    .responseSchema(Schema.schema("Friend Delete Response"))
                    .build()
            )));

    verify(friendService).deleteFriend(memberId);
  }
  @Test
  @DisplayName("친구 삭제 - 실패 : 친구를 회워 DB에서 찾을 수 없음")
  void deleteFriend_fail_not_found_member() throws Exception {
    //given
    Long memberId = 1L;

    //when
    doThrow(new MemberNotFoundException(MEMBER_NOT_FOUND)).when(friendService).deleteFriend(anyLong());

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

    verify(friendService).deleteFriend(memberId);
  }
  @Test
  @DisplayName("친구 삭제 - 실패 : 친구 목록에서 ACCEPT 인 상태를 찾을 수 없음")
  void deleteFriend_fail_not_found_accepted() throws Exception {
    //given
    Long memberId=1L;

    //when
    doThrow(new FriendNotFoundException(FRIEND_NOT_FOUND)).when(friendService).deleteFriend(anyLong());

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

    verify(friendService).deleteFriend(memberId);
  }

}