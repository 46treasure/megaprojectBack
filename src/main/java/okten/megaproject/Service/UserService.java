package okten.megaproject.Service;

import okten.megaproject.dao.UserDao;
import okten.megaproject.models.AccountCredentials;
import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
//fgh
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

    public List<User> searchUser(String name){
        String s = name.toLowerCase();
        List<User> all = userDao.findAll();
        List<User> finded = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            String s1 = all.get(i).getUsername().toLowerCase();
            if (s1.contains(s)) {
                finded.add(all.get(i));
            }
        }
        System.out.println(finded);
        return finded;
    }

}
