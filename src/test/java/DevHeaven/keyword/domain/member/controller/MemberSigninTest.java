package DevHeaven.keyword.domain.member.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_EMAIL;
import static DevHeaven.keyword.common.exception.type.ErrorCode.BLOCKED_MEMBER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_OAUTH_PROVIDER;
import static DevHeaven.keyword.common.exception.type.ErrorCode.MISMATCH_PASSWORD;
import static DevHeaven.keyword.domain.member.type.MemberControllerTestType.SIGNIN;
import static DevHeaven.keyword.support.TestUtil.ACCESS_TOKEN;
import static DevHeaven.keyword.support.TestUtil.ACCESS_TOKEN_EXPIRED_DATE;
import static DevHeaven.keyword.support.TestUtil.GRANT_TYPE_BEARER;
import static DevHeaven.keyword.support.TestUtil.REFRESH_TOKEN;
import static DevHeaven.keyword.support.TestUtil.getFailByErrorCase;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndDto;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndErrorCode;
import static DevHeaven.keyword.support.TestUtil.getSuccess;
import static DevHeaven.keyword.support.TestUtil.getWithPrefixAPI;
import static DevHeaven.keyword.support.fixture.MemberFixture.BAD_DUCK;
import static DevHeaven.keyword.support.fixture.MemberFixture.CAT;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.support.fixture.MemberFixture.DUCK;
import static DevHeaven.keyword.type.DomainType.MEMBER;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.common.security.dto.TokenResponse;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MyInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.TokenAndInfoResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberControllerTestType;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.ControllerTest;
import DevHeaven.keyword.support.fixture.MemberFixture;
import DevHeaven.keyword.type.DomainType;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class MemberSigninTest extends ControllerTest {

  private final MemberControllerTestType type = SIGNIN;
  private final MemberFixture fixture = DOG;
  private final DomainType domain = MEMBER;

  // 성공 시 응답
  private ResourceSnippetParameters getSigninSuccessResponseSnippet(
      SigninRequest requestDto,
      TokenAndInfoResponse responseDto) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestFields(
            getFieldDescriptorsByDescriptionAndDto(
                type.getRequestDescriptionFormatBySummary(), requestDto, "", new ArrayList<>())
        )
        .responseFields(
            getFieldDescriptorsByDescriptionAndDto(
                type.getSuccessResponseDescriptionFormatBySummary(), responseDto,
                "", new ArrayList<FieldDescriptor>())
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }

  // 실패 시 응답
  private ResourceSnippetParameters getSigninErrorResponseSnippet(SigninRequest requestDto,
      ErrorCode errorCode) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestFields(
            getFieldDescriptorsByDescriptionAndDto(
                type.getRequestDescriptionFormatBySummary(), requestDto, "", new ArrayList<>())
        )
        .responseFields(
            getFieldDescriptorsByDescriptionAndErrorCode(
                type.getFailResponseDescriptionFormatBySummary(), errorCode)
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }


  // Test code

  // case - success

  @Test
  @DisplayName("로그인 - success")
  @WithCustomMockUser
  void signin_success() throws Exception {

    Member fixtureMember = fixture.createMember();

    SigninRequest requestDto = SigninRequest.builder()
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    TokenResponse tokenResponse = TokenResponse.builder()
        .grantType(GRANT_TYPE_BEARER)
        .accessToken(ACCESS_TOKEN)
        .refreshToken(REFRESH_TOKEN)
        .accessTokenExpiredDate(ACCESS_TOKEN_EXPIRED_DATE)
        .build();
    MyInfoResponse myInfoResponse = MyInfoResponse.from(fixtureMember, fixture.getImageUrl());

    TokenAndInfoResponse responseDto = TokenAndInfoResponse.builder()
        .tokenResponse(tokenResponse)
        .myInfoResponse(myInfoResponse)
        .build();

    when(memberService.signin(eq(requestDto)))
        .thenReturn(responseDto);

    mockMvc.perform(
            post(type.getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(UTF_8)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getSuccess(),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getSigninSuccessResponseSnippet(requestDto, responseDto))
            ));

    verify(memberService).signin(requestDto);
  }


  // case - fail

  @Test
  @DisplayName("로그인 - fail : 이메일이 존재하지 않음")
  @WithCustomMockUser
  void getMyInfo_fail_EMAIL_NOT_FOUND() throws Exception {

    Member fixtureMember = fixture.createMember();
    ErrorCode errorCode = EMAIL_NOT_FOUND;

    SigninRequest requestDto = SigninRequest.builder()
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signinCommonErrorTestCode(requestDto, errorCode);
  }

  @Test
  @DisplayName("로그인 - fail : 비밀번호 불일치")
  @WithCustomMockUser
  void getMyInfo_fail_MISMATCH_PASSWORD() throws Exception {

    Member fixtureMember = fixture.createMember().modifyPassword("wrongPassword123!");
    ErrorCode errorCode = MISMATCH_PASSWORD;

    SigninRequest requestDto = SigninRequest.builder()
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signinCommonErrorTestCode(requestDto, errorCode);
  }

  @Test
  @DisplayName("로그인 - fail : 소셜 회원이 일반 로그인으로 접근")
  @WithCustomMockUser
  void getMyInfo_fail_MISMATCH_OAUTH_PROVIDER() throws Exception {

    Member fixtureMember = CAT.createMember();
    ErrorCode errorCode = MISMATCH_OAUTH_PROVIDER;

    SigninRequest requestDto = SigninRequest.builder()
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signinCommonErrorTestCode(requestDto, errorCode);
  }

  @Test
  @DisplayName("로그인 - fail : 회원 status 가 ACTIVE 하지 않음")
  @WithCustomMockUser
  void getMyInfo_fail_BLOCKED_MEMBER() throws Exception {

    Member fixtureMember = BAD_DUCK.createMember();
    ErrorCode errorCode = BLOCKED_MEMBER;

    SigninRequest requestDto = SigninRequest.builder()
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signinCommonErrorTestCode(requestDto, errorCode);
  }

  // 공통 Error Test Code
  private void signinCommonErrorTestCode(SigninRequest requestDto, ErrorCode errorCode)
      throws Exception {

    doThrow(new MemberException(errorCode))
        .when(memberService)
        .signin(eq(requestDto));

    mockMvc.perform(
            post(type.getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(UTF_8)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(errorCode.getHttpStatus().value()))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getFailByErrorCase(errorCode.name()),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getSigninErrorResponseSnippet(requestDto, errorCode))
            ));

    verify(memberService).signin(requestDto);
  }
}
