package DevHeaven.keyword.support;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import DevHeaven.keyword.common.exception.dto.ErrorResponse;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.type.ParameterType;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;

public class TestUtil {

  public static String ACCESS_TOKEN = "AccessToken";
  public static String REFRESH_TOKEN = "RefreshToken";
  public static String GRANT_TYPE_BEARER = "Bearer";
  public static long ACCESS_TOKEN_EXPIRED_DATE = 1000L;

  public static String AUTHORIZATION = "Authorization";
  public static String BEARER_ACCESS_TOKEN = GRANT_TYPE_BEARER + " " + ACCESS_TOKEN;

  private static String ERROR_RESPONSE_PREFIX = " 실패 시 ";
  private static String PREFIX_API = " API";
  private static String SUCCESS = "success";
  private static String FAIL = "fail";

  public static String getWithPrefixAPI(String string) {
    return string + PREFIX_API;
  }

  public static String getSuccess() {
    return SUCCESS;
  }

  public static String getFailByErrorCase(String errorCase) {
    return FAIL + "/" + errorCase;
  }

  /*
  public static List<FieldDescriptor> getFieldDescriptorsByDescriptionAndDto(
      String description, Object object) {

    return Arrays.stream(object.getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(field -> PayloadDocumentation
            .fieldWithPath(field.getName())
            .type(getJsonFiendTypeByFieldType(field.getType()))   //.type(field.getType())
            .description(description + field.getName()))
        .collect(Collectors.toList());
  }
   */
  // 아래의 메서드로 대체

  public static List<FieldDescriptor> getFieldDescriptorsByDescriptionAndDto(
      String description, Object object, String path, List<FieldDescriptor> descriptors) {

    Field[] fields = object.getClass().getDeclaredFields();

    for(Field field : fields) {
      if(!field.isSynthetic()) {
        if(getJsonFieldTypeByFieldType(field.getType()) == JsonFieldType.OBJECT) {
          getFieldDescriptorsByDescriptionAndDto(description, field, field.getName() + ".", descriptors);
        }else {
          descriptors.add(
              PayloadDocumentation.fieldWithPath(path + field.getName())
                  .type(getJsonFieldTypeByFieldType(field.getType())).optional()
                  .description(description + path + field.getName())
          );
        }
      }
    }

    return descriptors;
  }

  public static List<FieldDescriptor> getFieldDescriptorsByDescriptionAndErrorCode(
      String description, ErrorCode errorCode) {

    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    return Arrays.stream(errorResponse.getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(field -> PayloadDocumentation
            .fieldWithPath(field.getName())
            .type(JsonFieldType.STRING)
            .description(description + field.getName()))
        .collect(Collectors.toList());
  }


  /*

  public static List<FieldDescriptor> getFieldDescriptorsByDescriptionAndErrorCode(
      String description, ErrorCode errorCode
  ) {

    ErrorResponse errorResponse = ErrorResponse.from(errorCode);

    List<FieldDescriptor> fieldDescriptors = new ArrayList<>();

    Field[] fields = errorResponse.getClass().getDeclaredFields();

    JsonFieldType jsonFieldType = null;
    for(Field field : fields) {
      field.setAccessible(true);

      if(!field.isSynthetic()) {
        System.out.println(field.getName());

        try {
          jsonFieldType = field.get(errorResponse) == null ?
              JsonFieldType.NULL : getJsonFiendTypeByFieldType(field.getType());
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }

        System.out.println(jsonFieldType + "\n");

        fieldDescriptors.add(
            PayloadDocumentation
                .fieldWithPath(field.getName())
                .type(jsonFieldType)
                .description(description + field.getName())
        );
      }
    }

    return fieldDescriptors;
  }

   */

  public static List<ParameterDescriptorWithType> getParameterDescriptorsByParameterTypesAndDescription(
      String description, ParameterType ... parameterTypes
  ) {

    return Arrays.stream(parameterTypes)
        .map(parameterType ->
             parameterWithName(parameterType.getParameterName())
            .description(description + parameterType.getDescription()))
        .map(parameterDescriptor -> ParameterDescriptorWithType.Companion.fromParameterDescriptor(parameterDescriptor))
        .collect(Collectors.toList());
  }

  private static JsonFieldType getJsonFieldTypeByFieldType(Class<?> type) {

    if (type.equals(null)) {
      return JsonFieldType.NULL;
    } else if (type.equals(String.class) || type.equals(Enum.class) || type.isEnum()) {
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
