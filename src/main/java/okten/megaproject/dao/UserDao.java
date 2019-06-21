package okten.megaproject.dao;

import okten.megaproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

public interface UserDao extends JpaRepository<User, Integer> {


    @Override
    User getOne(Integer id);

    User findByUsername(String username);

    User findByPassword(String password);

    User findByUsersFilms(Long id);

    @Query("select u from User u where u.username=:name")
    User loadByUsername(@Param("name") String name);

    @Transactional
    @Modifying
    @Query("update User set avatar=:avatar where id=:id")
    void setAvatar(@Param("avatar") String avatar, @Param("id") int id);

    User findByUserKey(String key);
}
