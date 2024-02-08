package DevHeaven.keyword.support;

import DevHeaven.keyword.common.security.JwtAuthenticationFilter;
import DevHeaven.keyword.common.security.JwtExceptionFilter;
import DevHeaven.keyword.common.security.JwtUtils;
import DevHeaven.keyword.domain.chat.controller.ChatRoomController;
import DevHeaven.keyword.domain.chat.service.ChatRoomService;
import DevHeaven.keyword.domain.chat.service.ChatService;
import DevHeaven.keyword.domain.friend.controller.FriendController;
import DevHeaven.keyword.domain.friend.service.ElasticSearchService;
import DevHeaven.keyword.domain.friend.service.FriendService;
import DevHeaven.keyword.domain.member.controller.MemberController;
import DevHeaven.keyword.domain.member.service.MemberService;
import DevHeaven.keyword.domain.member.service.OAuth2UserService;
import DevHeaven.keyword.domain.notice.controller.NoticeController;
import DevHeaven.keyword.domain.notice.service.NoticeService;
import DevHeaven.keyword.domain.schedule.controller.ScheduleController;
import DevHeaven.keyword.domain.schedule.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(controllers = {
    MemberController.class,
    FriendController.class,
    ScheduleController.class,
    ChatRoomController.class,
    NoticeController.class
  }
  ,excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
      classes = WebSecurityConfigurerAdapter.class
    ),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
      classes = {JwtAuthenticationFilter.class,
      JwtExceptionFilter.class}
    )
  }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
public abstract class ControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected MemberService memberService;

  @MockBean
  protected FriendService friendService;

  @MockBean
  protected ChatService chatService;

  @MockBean
  protected ChatRoomService chatRoomService;

  @MockBean
  protected NoticeService noticeService;

  @MockBean
  protected OAuth2UserService oAuth2UserService;

  @MockBean
  protected ScheduleService scheduleService;

  @MockBean
  protected ElasticSearchService elasticSearchService;
}
