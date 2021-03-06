package okten.megaproject.Service;

import okten.megaproject.dao.FilmsDao;
import okten.megaproject.models.Films;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    @Autowired
 private FilmsDao filmsDao;
    public  void transferTo(MultipartFile file){
        String path = System.getProperty("user.home") + File.separator + "FilmImages" + File.separator;
        try {
            file.transferTo(new File(path + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Films> findByGenre (String genre){
        List<Films> all = filmsDao.findAll();
        List<Films> finded = new ArrayList<>();
        for (int i=0; i < all.size(); i++) {
            if(all.get(i).getGenre().contains(genre)) {
                finded.add(all.get(i));

            }
        }
         return finded;
    }
}
