package okten.megaproject.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
@Service
public class FilmService {

    public  void transferTo(MultipartFile file){
        String path = System.getProperty("user.home") + File.separator + "FilmImages" + File.separator;
        try {
            file.transferTo(new File(path + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
