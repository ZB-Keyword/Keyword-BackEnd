package DevHeaven.keyword.common.service.image;

import static DevHeaven.keyword.common.exception.type.ErrorCode.FILE_NOT_EXIST;

import DevHeaven.keyword.common.exception.ImgException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.common.service.image.dto.S3ImageEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class AmazonS3FileService {

  private final AmazonS3 amazonS3;
  private final ApplicationEventPublisher publisher;

  @Value("${aws.s3.folder}")
  private String folderName;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.domain}")
  private String domainUrl;

  private static final String FILENAME_SEPARATOR = "_";
  private static final String EXTENSION_SEPARATOR = ".";

  public String saveImage(final MultipartFile imgFile){
    validateImage(imgFile);

    final String fileName = createFileName(imgFile.getOriginalFilename());
    final ObjectMetadata objectMetadata = createObjectMetadata(imgFile);
    uploadImageToS3(folderName+fileName, imgFile, objectMetadata);
    return fileName;
  }


  private void validateImage(final MultipartFile imgFile) {
    if(imgFile.isEmpty()) {
      throw new ImgException(FILE_NOT_EXIST);
    }else if (imgFile.getOriginalFilename() == null){
      throw new ImgException(ErrorCode.FILE_INCORRECT_FORMAT);
    }
  }

  private String createFileName(final String fileName) {
    final int extensionIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);
    final String extractFileName = fileName.substring(0,extensionIndex);
    final String extension = fileName.substring(extensionIndex+1);
    return System.currentTimeMillis()+FILENAME_SEPARATOR+extractFileName+EXTENSION_SEPARATOR+extension;
  }

  private ObjectMetadata createObjectMetadata(final MultipartFile imgFile) {
    final ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(imgFile.getSize());
    objectMetadata.setContentType(imgFile.getContentType());
    return objectMetadata;
  }

  private void uploadImageToS3(final String fileName ,final MultipartFile imgFile ,
      final ObjectMetadata objectMetadata) {
    try {
      amazonS3.putObject(new PutObjectRequest(bucketName, fileName, imgFile.getInputStream(), objectMetadata));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteFile(final String fileName){
    publisher.publishEvent(new S3ImageEvent(fileName));
  }

  public String createUrl(final String fileName){
    if(fileName == null || fileName.isEmpty()){
      return null;
    }
    try {
      return new URL(domainUrl+"/"+fileName).toString();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
