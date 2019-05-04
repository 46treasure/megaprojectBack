package okten.megaproject.controllers;

import okten.megaproject.Configurations.LoginFilter;
import okten.megaproject.Service.FilmService;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.dao.UserDao;
import okten.megaproject.models.Films;
import okten.megaproject.models.User;
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
import java.util.List;
import java.util.Optional;

@RestController
public class MainController {
    String path = System.getProperty("user.home") + File.separator + "FilmImages" + File.separator;

    @Autowired
    FilmsDao filmsDao;
    @Autowired
    FilmService filmService;
    @Autowired
    UserDao userDao;

    @GetMapping("/")
    //@CrossOrigin(origins = "http://localhost:4200")
    public List<Films> allFilms(Authentication authentication) {
         authentication = SecurityContextHolder.getContext().getAuthentication();
         //System.out.println( authentication.isAuthenticated());
        //System.out.println(authentication.getPrincipal());
        //System.out.println(authentication.getDetails());
//        System.out.println(filmsDao.findAll());

        return filmsDao.findAll();
    }

    @PostMapping("/addfilm")

    public Films addFilm(@RequestParam("name") String name,
                         @RequestParam("year") String year,
                         @RequestParam("country") String country,
                         @RequestParam("aboutFilm") String aboutFilm,
                         @RequestParam("quality") String quality ,
                         BindingResult bindingResult,
                         @RequestParam("picture") MultipartFile picture){
        if(bindingResult.hasErrors()) {
            return null;
        }
        Films film = new Films(name, year, aboutFilm, country, quality);
        filmService.transferTo(picture);
        film.setPicture(path + picture.getOriginalFilename());
        return filmsDao.save(film);
    }

      @PostMapping("/getbyid")
      @CrossOrigin(origins = "http://localhost:4200")
      public Films getById(@RequestBody Long id){
        System.out.println(filmsDao.getOne(id).toString());
      return filmsDao.getOne(id);
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
        return "get it";
    }



    @GetMapping("/logout")
    public String logout(){
        return "logouted";
    }
//    @PostMapping("/login")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public Authentication login(@RequestBody User user) {
//        User user1 = userDao.findByUsername(user.getUsername());
//        AccountCredentials creds = new AccountCredentials();
//        if (creds.getUsername().equals(user1.getUsername())) {
//            Authentication authentication = new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), Collections.emptyList());
//            return authentication;
//        }else
//            return null;
//    }
}
