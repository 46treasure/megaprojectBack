package okten.megaproject.controllers;
import okten.megaproject.Configurations.EmailService;
import okten.megaproject.Service.FilmService;
import okten.megaproject.Service.UserService;
import okten.megaproject.dao.CommentDAO;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.dao.UserDao;
import okten.megaproject.models.Comments;
import okten.megaproject.models.Films;
import okten.megaproject.models.User;
import okten.megaproject.models.UserEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class UserController {
    private final FilmsDao filmsDao;
    private final CommentDAO commentDAO;
    private final FilmService filmService;
    private final UserDao userDao;
    private final UserService userService;
    private final EmailService emailService;
    private User current = new User();
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(FilmsDao filmsDao, CommentDAO commentDAO, FilmService filmService, UserDao userDao, UserService userService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.filmsDao = filmsDao;
        this.commentDAO = commentDAO;
        this.filmService = filmService;
        this.userDao = userDao;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reg")
    public boolean reg(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userDb = userDao.findByUsername(user.getUsername());
        if (userDb != null) {
            return false;
        } else {
            user.setAvatar("assets/ava.jpg");
            user.setRole(UserEnum.ROLE_USER);
            ArrayList<Integer> sub = new ArrayList<>();
            ArrayList<Integer> folow = new ArrayList<>();
            user.setFolowing(folow);
            user.setSubscribes(sub);
            userDao.save(user);
            emailService.send(user.getEmail(), user);
            return true;
        }
    }


    @GetMapping("/get")
    public User get() {
        String authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        current = userDao.findByUsername(authentication);
        return current;
    }

    @PostMapping("/setStatus")
    public void setStatus(@RequestBody String status) {
        current = get();
        current.setStatus(status);
        userDao.save(current);
    }

    @PostMapping("/adduserfilm")
    public List<Films> addUserFilm(@RequestBody Long idFilm) {
        String auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User byUsername = userDao.findByUsername(auth);
        List<Films> usersFilms = byUsername.getUsersFilms();
        for (int i = 0; i < usersFilms.size(); i++) {
            if (usersFilms.get(i).getId() == idFilm) {
                return usersFilms;
            }
        }
        Films one = filmsDao.getOne(idFilm);
        usersFilms.add(one);
        List<User> users = one.getUser();
        one.setUser(users);
        byUsername.setUsersFilms(usersFilms);
        userDao.save(byUsername);
        filmsDao.save(one);
        return usersFilms;
    }


    @PostMapping("/deluserfilms")
    public List<Films> delUserFilm(@RequestBody long idFilm) {
        current = get();
        List<Films> usersFilms = current.getUsersFilms();
        Iterator<Films> iterator = usersFilms.iterator();
        while (iterator.hasNext()) {
            Films next = iterator.next();
            if (next.getId() == idFilm) {
                iterator.remove();
                break;
            }
        }
        current.setUsersFilms(usersFilms);
        userDao.save(current);
        return usersFilms;
    }

    @PostMapping("/userpage-userfilms")
    public List<Films> getUserFilm(@RequestBody int id) {
        User byUsername = userDao.getOne(id);
        return byUsername.getUsersFilms();
    }

    @PostMapping("/getUserById")
    public User getUserById(@RequestBody int id) {
        return  userDao.getOne(id);
    }

    @PostMapping("/currentPage")
    public boolean currentPage(@RequestBody int id) {
        if (current != null) {
            current = get();
            return current.getId() == id;
        }
        return false;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody int id) {
        current = get();
        User byId = userDao.getOne(id);
        ArrayList<Integer> subscribes = byId.getSubscribes();
        subscribes.add(current.getId());
        byId.setSubscribes(subscribes);
        userDao.save(byId);
        ArrayList<Integer> folowing = current.getFolowing();
        folowing.add(id);
        userDao.save(current);
        System.out.println(byId.getSubscribes());
    }

    @PostMapping("/unSubscribe")
    public void unSubscribe(@RequestBody int id) {
        current = get();
        User byId = userDao.getOne(id);
        ArrayList<Integer> subscribes = byId.getSubscribes();
        Integer currentID = current.getId();
        if (subscribes.contains(currentID)) {
            subscribes.remove(currentID);
            byId.setSubscribes(subscribes);
            userDao.save(byId);
        }
        ArrayList<Integer> folowing = current.getFolowing();
        Integer i = id;
        if (folowing.contains(i)) {
            folowing.remove(i);
            current.setFolowing(folowing);
            userDao.save(current);
        }

    }

    @PostMapping("/getSubscribers")
    public ArrayList<User> friends(@RequestBody int id) {
        User one = userDao.getOne(id);
        ArrayList<Integer> subscribes = one.getSubscribes();
        ArrayList<User> friends = new ArrayList<>();
        for (Integer subscribe : subscribes) {
            friends.add(userDao.getOne(subscribe));
        }

        System.out.println(subscribes);
        return friends;
    }

    @PostMapping("/exist")
    public boolean exist(@RequestBody int id) {
        if (get() != null) {
            current = get();
            ArrayList<Integer> following = current.getFolowing();
            return following.contains(id);
        }
        return false;

    }

    @PostMapping("/getFolowing")
    public ArrayList<User> folowing(@RequestBody int id) {
        User one = userDao.getOne(id);
        ArrayList<Integer> folowing = one.getFolowing();
        ArrayList<User> folow = new ArrayList<>();
        for (Integer folower : folowing) {
            folow.add(userDao.getOne(folower));
        }
        return folow;
    }

    @PostMapping("/setAvatar")
    public void setAva(@RequestParam("avatar") MultipartFile avatar) {
        current = get();
        userService.saveAva(avatar);
        String path = "http://127.0.0.1:8887/";
        userDao.setAvatar(path + avatar.getOriginalFilename(), current.getId());
    }

    @PostMapping("/search")
    public List<Films> search(@RequestBody String name) {
        return filmService.searchFilms(name);
    }

    @GetMapping("/finishReg/{key}")
    public boolean finish(@PathVariable String key) {
        System.out.println(key);
        return userService.activateUser(key);

    }
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @PostMapping("/findSearchingUser")
    public List<User> findSearchingFilm(@RequestBody String name) {
        return userService.searchUser(name);
    }

    @PostMapping("/addComment")
    public void addComent( @RequestParam String idfilm,
                           @RequestParam String text,
                           @RequestParam String date) {

        Films currentFilm = filmsDao.getOne(Long.parseLong(idfilm));
        current = get();
        Comments comments = new Comments();
        comments.setUserID(current.getId());
        comments.setUsername(current.getUsername());
        comments.setAvatar(current.getAvatar());
        comments.setText(text);
        comments.setDate(date);
        comments.setFilm(currentFilm);
        commentDAO.save(comments);
    }
}
