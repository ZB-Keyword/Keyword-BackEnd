package DevHeaven.keyword.domain.member.controller;

import static DevHeaven.keyword.domain.member.type.MemberControllerTestType.GET_MY_INFO;
import static DevHeaven.keyword.support.TestUtil.AUTHORIZATION;
import static DevHeaven.keyword.support.TestUtil.BEARER_ACCESS_TOKEN;
import static DevHeaven.keyword.support.TestUtil.ERROR_CODE;
import static DevHeaven.keyword.support.TestUtil.ERROR_MESSAGE;
import static DevHeaven.keyword.support.TestUtil.HTTP_STATUS;
import static DevHeaven.keyword.support.TestUtil.getErrorCodeBySummary;
import static DevHeaven.keyword.support.TestUtil.getErrorMessageBySummary;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDtoAndDescription;
import static DevHeaven.keyword.support.TestUtil.getHttpStatusBySummary;
import static DevHeaven.keyword.support.TestUtil.getSuccess;
import static DevHeaven.keyword.support.TestUtil.getWithPrefixAPI;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.type.DomainType.MEMBER;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberControllerTestType;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.ControllerTest;
import DevHeaven.keyword.support.fixture.MemberFixture;
import DevHeaven.keyword.type.DomainType;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class MemberGetMyInfoTest extends ControllerTest {

  private final MemberControllerTestType type = GET_MY_INFO;
  private final MemberFixture fixture = DOG;
  private final DomainType domain = MEMBER;

  // 성공 시 응답
  private ResourceSnippetParameters getMyInfoSuccessResponse(MyInfoResponse myInfoResponse) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestHeaders(
            headerWithName(AUTHORIZATION).description(BEARER_ACCESS_TOKEN)
        )
        .responseFields(
            getFieldDescriptorsByDtoAndDescription(myInfoResponse, GET_MY_INFO.getDescriptionFormatBySummary())
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }

  // 실패 시 응답
  private ResourceSnippetParameters getMyInfoErrorResponse() {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestHeaders(
            headerWithName(AUTHORIZATION).description(BEARER_ACCESS_TOKEN)
        )
        .responseFields(
            fieldWithPath(ERROR_CODE).type(JsonFieldType.STRING).description(getErrorCodeBySummary(summary)),
            fieldWithPath(ERROR_MESSAGE).type(JsonFieldType.STRING).description(getErrorMessageBySummary(summary)),
            fieldWithPath(HTTP_STATUS).type(JsonFieldType.STRING).description(getHttpStatusBySummary(summary))
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }


  // Test code

  @Test
  @DisplayName("나의 정보 조회 - success")
  @WithCustomMockUser
  void getMyInfo_success() throws Exception {

    Member fixtureMember = fixture.createMember();
    MemberAdapter fixtureMemberAdapter = MemberAdapter.from(fixtureMember);

    MyInfoResponse myInfoResponse = MyInfoResponse.from(fixtureMember, fixtureMember.getProfileImageFileName());
    when(memberService.getMyInfo(eq(fixtureMemberAdapter)))
        .thenReturn(MyInfoResponse.from(fixtureMember, fixtureMember.getProfileImageFileName()));

    mockMvc.perform(
        get(domain.getPrefixUrl())
          .header(AUTHORIZATION, BEARER_ACCESS_TOKEN))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentationWrapper.document(
              getSuccess(),
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              resource(getMyInfoSuccessResponse(myInfoResponse))
    ));

    verify(memberService).getMyInfo(fixtureMemberAdapter);
  }
}
