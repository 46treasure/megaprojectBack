package okten.megaproject.controllers;

import okten.megaproject.Configurations.EmailService;
import okten.megaproject.Service.FilmService;
import okten.megaproject.Service.UserService;
import okten.megaproject.dao.CommentDAO;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.dao.UserDao;
import okten.megaproject.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class MainController {
    String path = "http://127.0.0.1:8887/";

    @Autowired
    FilmsDao filmsDao;
    @Autowired
    FilmService filmService;
    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    CommentDAO commentDAO;
    @GetMapping("/home")
    public List<Films> allFilms() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getDetails());
        return filmsDao.findAll();

    }

    @GetMapping("/topTen")
    public List<Films> topTen() {
        return filmService.topTen();
    }

    @PostMapping("/addfilm")

    public Films addFilm(@RequestParam("name") String name,
                         @RequestParam("year") String year,
                         @RequestParam("country") String country,
                         @RequestParam("aboutFilm") String aboutFilm,
                         @RequestParam("quality") String quality,
                         @RequestParam("genre") String genre,
                         @RequestParam("picture") MultipartFile picture,
                         @RequestParam("movie") MultipartFile movie) {
        System.out.println(genre);
        ArrayList<String> genres = new ArrayList<>();
        for (String rev : genre.split(","))
            genres.add(rev);
        Films film = new Films(name, year, aboutFilm, country, quality);
        ArrayList<Integer> listRating = new ArrayList<>();
        film.setRating(listRating);
        film.setScore(0);
        filmService.transferTo(picture);
        film.setGenre(genres);
        film.setPicture(path + picture.getOriginalFilename());
        filmService.transferTo(movie);
        film.setMovie(path + movie.getOriginalFilename());
        return filmsDao.save(film);
    }

    @PostMapping("/getbyid")
    @CrossOrigin(origins = "http://localhost:4200")
    public Films getById(@RequestBody Long id) {
        return filmsDao.getOne(id);
    }

    @PostMapping("/findByGenre")
    public List<Films> findByGenre(@RequestBody String genre) {
        return filmService.findByGenre(genre);
    }


    @PostMapping("/delfilm")
    public List<Films> delFilm(@RequestBody Long filmId) {
        filmsDao.deleteById(filmId);
        return filmsDao.findAll();
    }


    @Autowired
    PasswordEncoder passwordEncoder;

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

    private User current = new User();

    @GetMapping("/get")
    public User get() {
        String authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println(authentication);
        current = userDao.findByUsername(authentication);

//        current.setStatus("Online");
//        userDao.save(current);
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
        List<Films> usersFilms = byUsername.getUsersFilms();
        return usersFilms;
    }

    @PostMapping("/rating")
    public double rating(@RequestParam("idFilm") Long id, @RequestParam("rating") int rat) {
        int sum = 0;
        double res;
        Films one = filmsDao.getOne(id);
        ArrayList<Integer> rating = one.getRating();
        rating.add(rat);
        one.setRating(rating);
        for (Integer integer : rating) {
            sum = sum + integer;
        }
        res = (double) sum / (double) rating.size();
        one.setScore(res);
        filmsDao.save(one);
        return res;
        //ss
    }


    private User thisUser = new User();

    @PostMapping("/getUserById")
    public User getUserById(@RequestBody int id) {
        thisUser = userDao.getOne(id);
        return thisUser;
    }

    @PostMapping("/currentPage")
    public boolean currentPage(@RequestBody int id) {
        if (current.getId() == id) {
            return true;
        } else {
            return false;
        }
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
        current = get();
        ArrayList<Integer> following = current.getFolowing();
        return following.contains(id);

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
        userDao.setAvatar(path + avatar.getOriginalFilename(), current.getId());
    }

    @PostMapping("/close")
    public void close(@RequestBody int id) {
        System.out.println("!!" + id);
        if(id != 0) {
            User onUser = userDao.getOne(id);
            onUser.setStatus("offline");
            userDao.save(onUser);
        }
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
    @PostMapping("/getComments")
    public List<Comments> getComments (@RequestBody Long id){
        Films current = filmsDao.getOne(id);
        List<Comments> comments = current.getComments();
        return comments;

    }
}
