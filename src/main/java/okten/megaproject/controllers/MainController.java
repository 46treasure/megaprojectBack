package okten.megaproject.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import okten.megaproject.Configurations.LoginFilter;
import okten.megaproject.Service.FilmService;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.dao.UserDao;
import okten.megaproject.models.AccountCredentials;
import okten.megaproject.models.Films;
import okten.megaproject.models.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

@RestController
public class MainController {
    String path = "http://127.0.0.1:8887/";

    @Autowired
    FilmsDao filmsDao;
    @Autowired
    FilmService filmService;
    @Autowired
    UserDao userDao;

    AccountCredentials credentials;

    @GetMapping("/")
    public List<Films> allFilms() {
        return filmsDao.findAll();
    }

    @PostMapping("/addfilm")

    public Films addFilm(@RequestParam("name") String name,
                         @RequestParam("year") String year,
                         @RequestParam("country") String country,
                         @RequestParam("aboutFilm") String aboutFilm,
                         @RequestParam("quality") String quality ,
                         @RequestParam("genre") String genre ,
                         @RequestParam("picture") MultipartFile picture,
                         @RequestParam("movie") MultipartFile movie){
         System.out.println(genre);
         ArrayList<String> genres = new ArrayList<>();
         for(String rev: genre.split(","))
             genres.add(rev);
        Films film = new Films(name, year, aboutFilm, country, quality);
        filmService.transferTo(picture);
        film.setGenre(genres);
        film.setPicture(path + picture.getOriginalFilename());
        filmService.transferTo(movie);
        film.setMovie(path + movie.getOriginalFilename());
        return filmsDao.save(film);
    }

      @PostMapping("/getbyid")
      @CrossOrigin(origins = "http://localhost:4200")
      public Films getById(@RequestBody Long id){
      return filmsDao.getOne(id);
    }
    @PostMapping("/findByGenre")
    public List<Films> findByGenre (@RequestBody String genre) {
        return filmService.findByGenre(genre);
    }


    @PostMapping("/delfilm")
    @CrossOrigin(origins = "http://localhost:4200")
    public List<Films> delFilm(@RequestBody Long filmId){
        filmsDao.deleteById(filmId);
        return filmsDao.findAll();
    }


    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/reg")
    @CrossOrigin(origins = "http://localhost:4200")
    public boolean reg(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userDb = userDao.findByUsername(user.getUsername());
        if (userDb != null) {
            return false;
        } else {
            ArrayList<Integer> sub = new ArrayList<>();
            user.setSubscribes(sub);
            userDao.save(user);
            return true;
        }
    }
    private User current = new User();
    @GetMapping("/get")
    public User get(){
        String authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        current = userDao.findByUsername(authentication);
        return current;
}

    @PostMapping("/adduserfilm")
    public List<Films> addUserFilm(@RequestBody Long idFilm){
        String auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User byUsername = userDao.findByUsername(auth);
        List<Films> usersFilms = byUsername.getUsersFilms();
        Films one = filmsDao.getOne(idFilm);
        usersFilms.add(one);
        List<User> users = one.getUser();
        one.setUser(users);
        byUsername.setUsersFilms(usersFilms);
        userDao.save(byUsername);
        filmsDao.save(one);
        return usersFilms;
    }
//    sgfvasdsadsda
    @GetMapping("/userpage-userfilms")
    public List<Films> getUserFilm(){
        String auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User byUsername = userDao.findByUsername(auth);
        List<Films> usersFilms = byUsername.getUsersFilms();
        System.out.println(usersFilms);
        return usersFilms;
    }
    private User thisUser = new User();
    @PostMapping("/getUserById")
    public User getUserById(@RequestBody int id){
        thisUser = userDao.getOne(id);
        return thisUser;
    }
    @PostMapping("/currentPage")
    public boolean currentPage (@RequestBody int id){
        System.out.println(current + "buserName");
        if(current.getId() == id){
            System.out.println("TRUEEE");
            return true;
        }
        else {
            System.out.println("FALSEE");
            return false;
        }
    }
    @PostMapping("/subscribe")
    public int subscribe (@RequestBody int id){
        User byId = userDao.getOne(id);
        ArrayList<Integer> subscribes = byId.getSubscribes();
        subscribes.add(current.getId());
        byId.setSubscribes(subscribes);
        userDao.save(byId);
        System.out.println(byId.getSubscribes());
        return 5;
    }

    @PostMapping("/friends")
    public  ArrayList<User> friends (@RequestBody int id){
        User one = userDao.getOne(id);
        ArrayList<Integer> subscribes = one.getSubscribes();
        System.out.println(subscribes);
        ArrayList<User> friends = new ArrayList<>();
        for (Integer subscribe : subscribes) {
            friends.add(userDao.getOne(subscribe));
        }
        return friends;
    }
}
