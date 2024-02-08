package DevHeaven.keyword.domain.member.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.BLOCKED_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static DevHeaven.keyword.domain.member.type.MemberControllerTestType.GET_MEMBER_INFO;
import static DevHeaven.keyword.domain.member.type.ParameterType.MEMBER_ID;
import static DevHeaven.keyword.support.TestUtil.AUTHORIZATION;
import static DevHeaven.keyword.support.TestUtil.BEARER_ACCESS_TOKEN;
import static DevHeaven.keyword.support.TestUtil.getFailByErrorCase;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndDto;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndErrorCode;
import static DevHeaven.keyword.support.TestUtil.getParameterDescriptorsByParameterTypesAndDescription;
import static DevHeaven.keyword.support.TestUtil.getSuccess;
import static DevHeaven.keyword.support.TestUtil.getWithPrefixAPI;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.type.DomainType.MEMBER;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
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

public class MemberGetMemberInfoTest extends ControllerTest {

  private final MemberControllerTestType type = GET_MEMBER_INFO;
  private final MemberFixture fixture = DOG;
  private final DomainType domain = MEMBER;

  // 성공 시 응답
  private ResourceSnippetParameters getMemberInfoSuccessResponseSnippet(
      MemberInfoResponse responseDto) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestHeaders(
            headerWithName(AUTHORIZATION).description(BEARER_ACCESS_TOKEN)
        )
        .pathParameters(
            getParameterDescriptorsByParameterTypesAndDescription(type.getSuccessResponseDescriptionFormatBySummary(), MEMBER_ID)
        )
        .responseFields(
            getFieldDescriptorsByDescriptionAndDto(
                type.getSuccessResponseDescriptionFormatBySummary(), responseDto, "", new ArrayList<>())
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }

  // 실패 시 응답
  private ResourceSnippetParameters getMemberInfoErrorResponseSnippet(ErrorCode errorCode) {

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
  @DisplayName("다른 회원 정보 조회 - success")
  @WithCustomMockUser
  void getMemberInfo_success() throws Exception {

    Member fixtureMember = fixture.createMember();

    MemberInfoResponse responseDto = MemberInfoResponse.from(fixtureMember, fixtureMember.getProfileImageFileName());
    when(memberService.getMemberInfo(eq(fixtureMember.getMemberId())))
        .thenReturn(responseDto);

    mockMvc.perform(
            get(type.getFullUrl(), fixtureMember.getMemberId())
                .header(AUTHORIZATION, BEARER_ACCESS_TOKEN))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getSuccess(),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getMemberInfoSuccessResponseSnippet(responseDto))
            ));

    verify(memberService).getMemberInfo(fixtureMember.getMemberId());
  }


  // case - fail

  @Test
  @DisplayName("다른 회원 정보 조회 - fail : 존재하지 않는 id (pk)")
  @WithCustomMockUser
  void getMemberInfo_fail_MEMBER_NOT_FOUND() throws Exception {

    Member fixtureMember = fixture.createMember();
    ErrorCode errorCode = MEMBER_NOT_FOUND;

    getMemberInfoCommonErrorTestCode(fixtureMember, errorCode);
  }

  @Test
  @DisplayName("다른 회원 정보 조회 - fail : 회원 status 가 ACTIVE 하지 않음")
  @WithCustomMockUser
  void getMyInfo_fail_BLOCKED_MEMBER() throws Exception {

    Member fixtureMember = fixture.createMember();
    ErrorCode errorCode = BLOCKED_MEMBER;

    getMemberInfoCommonErrorTestCode(fixtureMember, errorCode);
  }

  // 공통 Error Test Code
  private void getMemberInfoCommonErrorTestCode(Member fixtureMember, ErrorCode errorCode)
      throws Exception {

    doThrow(new MemberException(errorCode))
        .when(memberService)
        .getMemberInfo(eq(fixtureMember.getMemberId()));

    mockMvc.perform(
            get(type.getFullUrl(), fixtureMember.getMemberId())
                .header(AUTHORIZATION, BEARER_ACCESS_TOKEN))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(errorCode.getHttpStatus().value()))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getFailByErrorCase(errorCode.name()),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getMemberInfoErrorResponseSnippet(errorCode))
            ));

    verify(memberService).getMemberInfo(fixtureMember.getMemberId());
  }
}
