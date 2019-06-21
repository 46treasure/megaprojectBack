package okten.megaproject.Service;

import okten.megaproject.dao.UserDao;
import okten.megaproject.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "xxx")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null || !user.isActive()) {
            throw new UsernameNotFoundException("User '" + username + "' not found");

        }
        return userDao.loadByUsername(username);
    }
}
