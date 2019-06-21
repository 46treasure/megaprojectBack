package okten.megaproject.Service;

import okten.megaproject.dao.UserDao;
import okten.megaproject.models.AccountCredentials;
import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void saveAva(MultipartFile file) {
        String path = System.getProperty("user.home") + File.separator + "FilmImages" + File.separator;
        try {
            file.transferTo(new File(path + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean activateUser(String key){
        User byUserKey = userDao.findByUserKey(key);
        byUserKey.setUserKey(null);
        byUserKey.setActive(true);
        userDao.save(byUserKey);
        return true;
    }

}
