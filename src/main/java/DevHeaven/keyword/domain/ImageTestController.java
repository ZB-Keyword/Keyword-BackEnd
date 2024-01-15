package DevHeaven.keyword.domain;

import DevHeaven.keyword.common.service.image.AmazonS3FileService;
import java.net.URL;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageTestController {

  private final AmazonS3FileService fileService;

  @PostMapping("/image")
  public ResponseEntity<?> uploadImage(@RequestBody MultipartFile file){
    fileService.saveImage(file);
    return ResponseEntity.ok("save");
  }

  @GetMapping("/image")
  public ResponseEntity<?> getImage(@RequestBody String fileName){
    System.out.println(fileName);
    URL url = fileService.createUrl(fileName);
    System.out.println(url);
    return ResponseEntity.ok(url);
  }

  @DeleteMapping("/image")
  public ResponseEntity<?> deleteImage(@RequestBody String fileName){
    fileService.deleteFile(fileName);
    return ResponseEntity.ok("delete");
  }
}
