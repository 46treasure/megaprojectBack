package okten.megaproject.Service;

import okten.megaproject.dao.UserDao;
import okten.megaproject.models.AccountCredentials;
import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService  {
    @Autowired
    private UserDao userDao;

    public boolean compire(AccountCredentials accountCredentials){
        System.out.println("!!!!!!!!!!!!!!!");
        List<User> list = userDao.findAll();
        for (User user : list) {
            if(user.getUsername().equals(accountCredentials.getUsername()))
            return true;
        }
        return false;
    }
}
