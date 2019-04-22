package okten.megaproject.Service;

import okten.megaproject.dao.UserDao;
import okten.megaproject.models.AccountCredentials;
import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;


}
