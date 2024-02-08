package DevHeaven.keyword.domain.member.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.BLOCKED_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.domain.member.type.MemberControllerTestType.GET_MY_INFO;
import static DevHeaven.keyword.support.TestUtil.AUTHORIZATION;
import static DevHeaven.keyword.support.TestUtil.BEARER_ACCESS_TOKEN;
import static DevHeaven.keyword.support.TestUtil.getFailByErrorCase;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndDto;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndErrorCode;
import static DevHeaven.keyword.support.TestUtil.getSuccess;
import static DevHeaven.keyword.support.TestUtil.getWithPrefixAPI;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.type.DomainType.MEMBER;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
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
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class MemberGetMyInfoTest extends ControllerTest {

  private final MemberControllerTestType type = GET_MY_INFO;
  private final MemberFixture fixture = DOG;
  private final DomainType domain = MEMBER;

  // 성공 시 응답
  private ResourceSnippetParameters getMyInfoSuccessResponseSnippet(Object responseDto) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestHeaders(
            headerWithName(AUTHORIZATION).description(BEARER_ACCESS_TOKEN)
        )
        .responseFields(
            getFieldDescriptorsByDescriptionAndDto(
                type.getSuccessResponseDescriptionFormatBySummary(), responseDto, "", new ArrayList<>())
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }

  // 실패 시 응답
  private ResourceSnippetParameters getMyInfoErrorResponseSnippet(ErrorCode errorCode) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestHeaders(
            headerWithName(AUTHORIZATION).description(BEARER_ACCESS_TOKEN)
        )
        .responseFields(
            getFieldDescriptorsByDescriptionAndErrorCode(type.getFailResponseDescriptionFormatBySummary(), errorCode)
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }


  // Test code


  // case - success

  @Test
  @DisplayName("나의 정보 조회 - success")
  @WithCustomMockUser
  void getMyInfo_success() throws Exception {

    Member fixtureMember = fixture.createMember();
    MemberAdapter fixtureMemberAdapter = MemberAdapter.from(fixtureMember);

    MyInfoResponse responseDto = MyInfoResponse.from(fixtureMember, fixtureMember.getProfileImageFileName());
    when(memberService.getMyInfo(eq(fixtureMemberAdapter)))
        .thenReturn(responseDto);

    mockMvc.perform(
        get(type.getFullUrl())
          .header(AUTHORIZATION, BEARER_ACCESS_TOKEN))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentationWrapper.document(
              getSuccess(),
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              resource(getMyInfoSuccessResponseSnippet(responseDto))
    ));

    verify(memberService).getMyInfo(fixtureMemberAdapter);
  }


  // case - fail

  @Test
  @DisplayName("나의 정보 조회 - fail : 존재하지 않는 이메일")
  @WithCustomMockUser
  void getMyInfo_fail_EMAIL_NOT_FOUND() throws Exception {

    Member fixtureMember = fixture.createMember();
    MemberAdapter fixtureMemberAdapter = MemberAdapter.from(fixtureMember);
    ErrorCode errorCode = EMAIL_NOT_FOUND;

    getMyInfoCommonErrorTestCode(fixtureMemberAdapter, errorCode);
  }

  @Test
  @DisplayName("나의 정보 조회 - fail : 회원 status 가 ACTIVE 하지 않음")
  @WithCustomMockUser
  void getMyInfo_fail_BLOCKED_MEMBER() throws Exception {

    Member fixtureMember = fixture.createMember();
    MemberAdapter fixtureMemberAdapter = MemberAdapter.from(fixtureMember);
    ErrorCode errorCode = BLOCKED_MEMBER;

    getMyInfoCommonErrorTestCode(fixtureMemberAdapter, errorCode);
  }

  // 공통 Error Test Code
  private void getMyInfoCommonErrorTestCode(MemberAdapter fixtureMemberAdapter, ErrorCode errorCode)
      throws Exception {

    doThrow(new MemberException(errorCode))
        .when(memberService)
        .getMyInfo(eq(fixtureMemberAdapter));

    mockMvc.perform(
            get(type.getFullUrl())
                .header(AUTHORIZATION, BEARER_ACCESS_TOKEN))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(errorCode.getHttpStatus().value()))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getFailByErrorCase(errorCode.name()),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getMyInfoErrorResponseSnippet(errorCode))
            ));

    verify(memberService).getMyInfo(fixtureMemberAdapter);
  }
}
