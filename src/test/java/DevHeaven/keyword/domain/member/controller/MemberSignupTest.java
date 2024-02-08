package DevHeaven.keyword.domain.member.controller;

import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_EMAIL;
import static DevHeaven.keyword.common.exception.type.ErrorCode.ALREADY_EXISTS_PHONE;
import static DevHeaven.keyword.common.exception.type.ErrorCode.EMAIL_NOT_FOUND;
import static DevHeaven.keyword.common.exception.type.ErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION;
import static DevHeaven.keyword.domain.member.type.MemberControllerTestType.SIGNUP;
import static DevHeaven.keyword.support.TestUtil.AUTHORIZATION;
import static DevHeaven.keyword.support.TestUtil.BEARER_ACCESS_TOKEN;
import static DevHeaven.keyword.support.TestUtil.getFailByErrorCase;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndDto;
import static DevHeaven.keyword.support.TestUtil.getFieldDescriptorsByDescriptionAndErrorCode;
import static DevHeaven.keyword.support.TestUtil.getSuccess;
import static DevHeaven.keyword.support.TestUtil.getWithPrefixAPI;
import static DevHeaven.keyword.support.fixture.MemberFixture.DOG;
import static DevHeaven.keyword.support.fixture.MemberFixture.DUCK;
import static DevHeaven.keyword.type.DomainType.MEMBER;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.dto.request.SigninRequest;
import DevHeaven.keyword.domain.member.dto.request.SignupRequest;
import DevHeaven.keyword.domain.member.dto.response.MemberInfoResponse;
import DevHeaven.keyword.domain.member.dto.response.SignupResponse;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.type.MemberControllerTestType;
import DevHeaven.keyword.mock.WithCustomMockUser;
import DevHeaven.keyword.support.ControllerTest;
import DevHeaven.keyword.support.fixture.MemberFixture;
import DevHeaven.keyword.type.DomainType;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MemberSignupTest extends ControllerTest {

  private final MemberControllerTestType type = SIGNUP;
  private final MemberFixture fixture = DOG;
  private final DomainType domain = MEMBER;

  // 성공 시 응답
  private ResourceSnippetParameters getSignupSuccessResponseSnippet(
      SignupRequest requestDto,
      SignupResponse responseDto) {

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
                type.getSuccessResponseDescriptionFormatBySummary(), responseDto, "",
                new ArrayList<>())
        )
        .responseSchema(Schema.schema(schema))
        .build();
  }

  // 실패 시 응답
  private ResourceSnippetParameters getSignupErrorResponseSnippet(SignupRequest requestDto,
      ErrorCode errorCode) {

    String summary = type.getSummary();
    String schema = type.getSchema();

    return ResourceSnippetParameters.builder()
        .tag(getWithPrefixAPI(domain.name()))
        .summary(getWithPrefixAPI(summary))
        .requestFields(
            getFieldDescriptorsByDescriptionAndDto(type.getRequestDescriptionFormatBySummary(),
                requestDto, "", new ArrayList<>())
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
  @DisplayName("회원 가입 - success")
  @WithCustomMockUser
  void signup_success() throws Exception {

    Member fixtureMember = fixture.createMember();

    SignupRequest requestDto = SignupRequest.builder()
        .name(fixtureMember.getName())
        .phone(fixtureMember.getPhone())
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();
    SignupResponse responseDto = new SignupResponse(fixtureMember.getName());
    when(memberService.signup(eq(requestDto)))
        .thenReturn(responseDto);

    mockMvc.perform(
            post(type.getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(UTF_8)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                getSuccess(),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(getSignupSuccessResponseSnippet(requestDto, responseDto))
            ));

    verify(memberService).signup(requestDto);
  }

  // case - fail

  @Test
  @DisplayName("회원 가입 - fail : 이미 존재하는 이메일")
  @WithCustomMockUser
  void getMyInfo_fail_ALREADY_EXISTS_EMAIL() throws Exception {

    Member fixtureMember = fixture.createMember();
    ErrorCode errorCode = ALREADY_EXISTS_EMAIL;

    SignupRequest requestDto = SignupRequest.builder()
        .name(fixtureMember.getName())
        .phone(fixtureMember.getPhone())
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signupCommonErrorTestCode(requestDto, errorCode);
  }

  @Test
  @DisplayName("회원 가입 - fail : 이미 존재하는 핸드폰 번호")
  @WithCustomMockUser
  void getMyInfo_fail_ALREADY_EXISTS_PHONE() throws Exception {

    Member fixtureMember = fixture.createMember();
    ErrorCode errorCode = ALREADY_EXISTS_PHONE;

    SignupRequest requestDto = SignupRequest.builder()
        .name(fixtureMember.getName())
        .phone(fixtureMember.getPhone())
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signupCommonErrorTestCode(requestDto, errorCode);
  }

  @Test
  @DisplayName("회원 가입 - fail : 회원 가입 필수 정보 중 null, 혹은 정규식에 맞지 않는 값 전달")
  @WithCustomMockUser
  void getMyInfo_fail_METHOD_ARGUMENT_NOT_VALID_EXCEPTION() throws Exception {

    Member fixtureMember = DUCK.createMember();
    ErrorCode errorCode = METHOD_ARGUMENT_NOT_VALID_EXCEPTION;

    SignupRequest requestDto = SignupRequest.builder()
        .name(fixtureMember.getName())
        .phone(fixtureMember.getPhone())
        .email(fixtureMember.getEmail())
        .password(fixtureMember.getPassword())
        .build();

    signupCommonErrorTestCode(requestDto, errorCode);
  }

  // 공통 Error Test Code
  private void signupCommonErrorTestCode(SignupRequest requestDto, ErrorCode errorCode)
      throws Exception {

    doThrow(new MemberException(errorCode))
        .when(memberService)
        .signup(eq(requestDto));

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
                resource(getSignupErrorResponseSnippet(requestDto, errorCode))
            ));
  }
}