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
        return filmService.topTen().subList(0,6);
    }

    @GetMapping("/newFilms")
    public List<Films> newFilms(){
        return filmsDao.findAll();
    }

    @PostMapping("/addfilm")

    public Films addFilm(@RequestParam("name") String name,
                         @RequestParam("year") String year,
                         @RequestParam("country") String country,
                         @RequestParam("aboutFilm") String aboutFilm,
                         @RequestParam("quality") String quality,
                         @RequestParam("genre") String genre,
                         @RequestParam("picture") MultipartFile picture,
                         @RequestParam("movie") MultipartFile movie,
                         @RequestParam("trailer") MultipartFile trailer,
                         @RequestParam("audio1") MultipartFile audio1,
                         @RequestParam("audio2") MultipartFile audio2,
                         @RequestParam("audio3") MultipartFile audio3,
                         @RequestParam("audio4") MultipartFile audio4,
                         @RequestParam("screenShots1") MultipartFile screenShots1,
                         @RequestParam("screenShots2") MultipartFile screenShots2,
                         @RequestParam("screenShots3") MultipartFile screenShots3,
                         @RequestParam("screenShots4") MultipartFile screenShots4,
                         @RequestParam("actor1") String actor1,
                         @RequestParam("imgActor1") MultipartFile imgActor1,
                         @RequestParam("actor2") String actor2,
                         @RequestParam("imgActor2") MultipartFile imgActor2,
                         @RequestParam("actor3") String actor3,
                         @RequestParam("imgActor3") MultipartFile imgActor3,
                         @RequestParam("actor4") String actor4,
                         @RequestParam("imgActor4") MultipartFile imgActor4
                         ) {
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
        filmService.transferTo(trailer);
        film.setTrailer(path + trailer.getOriginalFilename());
        film.setScreenShots(createList(screenShots1, screenShots2, screenShots3, screenShots4));
        film.setAudio(createList(audio1, audio2, audio3, audio4));
        film.setActors(addActors(actor1, imgActor1, actor2, imgActor2, actor3, imgActor3, actor4, imgActor4));
        return filmsDao.save(film);
    }

    ArrayList<String> createList(MultipartFile file1,MultipartFile file2,MultipartFile file3,MultipartFile file4) {
        ArrayList<String> list = new ArrayList<>();
        filmService.transferTo(file1);
        filmService.transferTo(file2);
        filmService.transferTo(file3);
        filmService.transferTo(file4);
        list.add(path + file1.getOriginalFilename());
        list.add(path + file2.getOriginalFilename());
        list.add(path + file3.getOriginalFilename());
        list.add(path + file4.getOriginalFilename());
        return list;
    }
         ArrayList<Actors> addActors (String actor1, MultipartFile imgActor1,
                         String actor2, MultipartFile imgActor2,
                         String actor3, MultipartFile imgActor3,
                         String actor4, MultipartFile imgActor4) {
        ArrayList<Actors> actors = new ArrayList<>();
        filmService.transferTo(imgActor1);
        filmService.transferTo(imgActor2);
        filmService.transferTo(imgActor3);
        filmService.transferTo(imgActor4);
        actors.add(new Actors(actor1, path + imgActor1.getOriginalFilename()));
        actors.add(new Actors(actor2, path + imgActor2.getOriginalFilename()));
        actors.add(new Actors(actor3, path + imgActor3.getOriginalFilename()));
        actors.add(new Actors(actor4, path + imgActor4.getOriginalFilename()));
        return actors;
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
//a
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
