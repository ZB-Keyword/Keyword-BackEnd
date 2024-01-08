package DevHeaven.keyword.common.service.image;

import static DevHeaven.keyword.common.exception.type.ErrorCode.FILE_NOT_EXIST;

import DevHeaven.keyword.common.exception.ImgException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ImageUploadService {

  private final AmazonS3 amazonS3;

  @Value("${aws.s3.folder}")
  private String folderName;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  private static final String FILENAME_SEPARATOR = "-";
  private static final String EXTENSION_SEPARATOR = ".";

  public String saveImage(final MultipartFile imgFile){
    validateImage(imgFile);
    final String s3Key = folderName + createFileName(imgFile.getOriginalFilename());
    final ObjectMetadata objectMetadata = createObjectMetadata(imgFile);
    uploadImageToS3(s3Key, imgFile, objectMetadata);

    return amazonS3.getUrl(bucketName, s3Key).toString();
  }

  private void uploadImageToS3(final String s3Key ,final MultipartFile imgFile ,
      final ObjectMetadata objectMetadata) {
    try {
      amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, imgFile.getInputStream(), objectMetadata));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ObjectMetadata createObjectMetadata(final MultipartFile imgFile) {
    final ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(imgFile.getSize());
    objectMetadata.setContentType(imgFile.getContentType());
    return objectMetadata;
  }

  private void validateImage(final MultipartFile imgFile) {
    if(imgFile.isEmpty()) {
      throw new ImgException(FILE_NOT_EXIST);
    }else if (imgFile.getOriginalFilename() == null){
      throw new ImgException(ErrorCode.FILE_INCORRECT_FORMAT);
    }
  }

  private String createFileName(final String fileName) {
    final String extension = fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR));
    return System.currentTimeMillis()+FILENAME_SEPARATOR+fileName+EXTENSION_SEPARATOR+extension;
  }
}
