package DevHeaven.keyword.common.exception.util;

import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class ResponseUtils {

  private final ObjectMapper objectMapper;

  @Autowired
  public ResponseUtils(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }


  public void sendErrorResponse(final HttpServletResponse response, final ErrorCode errorCode)
      throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(errorCode.getHttpStatus().value());
    response.getWriter().write(getJsonByErrorCode(errorCode));
  }

  private String getJsonByErrorCode(final ErrorCode errorCode) throws JsonProcessingException {
    return objectMapper.writeValueAsString(ErrorResponse.from(errorCode));
  }

}
