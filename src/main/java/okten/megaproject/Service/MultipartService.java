package okten.megaproject.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class MultipartService {
    public String streamToS3(MultipartFile file)  {
        byte[] bytes = new byte[0];
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AWSCredentials credentials  = new BasicAWSCredentials(
                "AKIAZM6O5IMQ4UKUEHVP",
                "wCrMlrEBzoHbo6c6spG7F1ejGlAovTuT6fPeyOYj");
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        String objectName = file.getOriginalFilename();
        InputStream stream = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("image/jpg");
        s3Client.putObject(new PutObjectRequest("mycinema1",
                objectName,
                stream,
                metadata).withCannedAcl(CannedAccessControlList.PublicRead));
        String s3DirectURL = "https://mycinema1.s3.us-east-2.amazonaws.com/" + objectName;
        return s3DirectURL;
    }
}
