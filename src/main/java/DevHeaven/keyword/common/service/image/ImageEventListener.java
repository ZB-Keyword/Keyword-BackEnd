package DevHeaven.keyword.common.service.image;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import DevHeaven.keyword.common.exception.ImgException;
import DevHeaven.keyword.common.service.image.dto.S3ImageEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageEventListener {

  //기본 이미지가 있다는 가정하에
  private static final String DEFAULT_IMAGE_NAME = "default-image.png";

  private final AmazonS3 amazonS3;

  @Value("${aws.s3.folder}")
  private String folderName;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Async
  @Transactional(propagation = REQUIRES_NEW)
  @TransactionalEventListener(fallbackExecution = true)
  public void deleteImageFileInS3(final S3ImageEvent event) {
    final String imageName = event.getImageName();
    if (imageName.equals(DEFAULT_IMAGE_NAME)) {
      return;
    }
    final String key = folderName+imageName;
    amazonS3.deleteObject(new DeleteObjectRequest(bucketName,  key.trim()));
  }
}
