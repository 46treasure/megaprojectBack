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
        System.out.println(genres);
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
        System.out.println(filmsDao.getOne(id).toString());
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
            userDao.save(user);
            return true;
        }
    }
    @GetMapping("/get")
    public String get(){
        String authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (authentication.equals("anonymous")){
            return "anonymous";
        }else
        return authentication;
}

    @PostMapping("/adduserfilm")
    public List<Films> addUserFilm(@RequestBody User user, @RequestBody Films film){
        return filmService.addUserFilm(user, film);
    }

}
