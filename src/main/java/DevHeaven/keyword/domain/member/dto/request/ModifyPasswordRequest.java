package DevHeaven.keyword.domain.member.dto.request;

import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPasswordRequest {
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])(?!.*\\s)[A-Za-z\\d@$!%*?&]{8,16}$")
  private String password;
}
