package DevHeaven.keyword.common.service.image.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class S3ImageEvent {

  private final String imageName;
}
