package okten.megaproject.dao;

import okten.megaproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    User findByPassword(String password);

    @Query("select u from User u where u.username=:name")
    User loadByUsername(@Param("name")String name);
}
