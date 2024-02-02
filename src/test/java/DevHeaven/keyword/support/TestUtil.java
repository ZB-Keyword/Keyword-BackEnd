package DevHeaven.keyword.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class TestUtil {

  public static String AUTHORIZATION = "Authorization";
  public static String BEARER_ACCESS_TOKEN = "Bearer AccessToken";

  public static String ERROR_CODE = "errorCode";
  public static String ERROR_MESSAGE = "errorMessage";
  public static String HTTP_STATUS = "httpStatus";

  private static String ERROR_RESPONSE_PREFIX = " 실패 시 ";
  private static String PREFIX_API = " API";
  private static String SUCCESS = "success";
  private static String FAIL = "fail";

  public static String getWithPrefixAPI(String string) {
    return string + PREFIX_API;
  }

  public static String getErrorCodeBySummary(String summary) {
    return summary + ERROR_RESPONSE_PREFIX + ERROR_CODE;
  }

  public static String getErrorMessageBySummary(String summary) {
    return summary + ERROR_RESPONSE_PREFIX + ERROR_MESSAGE;
  }

  public static String getHttpStatusBySummary(String summary) {
    return summary + HTTP_STATUS + HTTP_STATUS;
  }

  public static String getSuccess() {
    return SUCCESS;
  }

  public static String getFailByErrorCase(String errorCase) {
    return FAIL + " : " + errorCase;
  }

  public static List<FieldDescriptor> getFieldDescriptorsByDtoAndDescription(Object object,
      String description) {

    return Arrays.stream(object.getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(field -> PayloadDocumentation
            .fieldWithPath(field.getName())
            .type(getJsonFiendTypeByFieldType(field.getType()))   //.type(field.getType())
            .description(description + field.getName()))
        .collect(Collectors.toList());
  }

  private static JsonFieldType getJsonFiendTypeByFieldType(Class<?> type) {

    if (type.equals(null)) {
      return JsonFieldType.NULL;
    } else if (type.equals(String.class)) {
      return JsonFieldType.STRING;
    } else if (type.equals(long.class) || type.equals(int.class) || type.equals(double.class)
        || type.equals(Long.class) || type.equals(Integer.class) || type.equals(Double.class)) {
      return JsonFieldType.NUMBER;
    } else if (type.equals(List.class) || type.equals(Arrays.class)) {
      return JsonFieldType.ARRAY;
    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return JsonFieldType.BOOLEAN;
    } else {
      return JsonFieldType.OBJECT;
    }
  }
}
