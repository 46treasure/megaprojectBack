package okten.megaproject.controllers;

import okten.megaproject.Service.FilmService;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.models.Films;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class MainController {
    String path = System.getProperty("user.home") + File.separator + "FilmImages" + File.separator;
    @Autowired
    FilmsDao filmsDao;
    @Autowired
    FilmService filmService;

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200")
    public List<Films> allFilms() {
        System.out.println(filmsDao.findAll());
        return filmsDao.findAll();
    }

    @PostMapping("/addfilm")
    @CrossOrigin(origins = "http://localhost:4200")
    public Films addFilm(@RequestParam("name") String name,
                         @RequestParam("year") String year,
                         @RequestParam("country") String country,
                         @RequestParam("aboutFilm") String aboutFilm,
                         @RequestParam("quality") String quality ,
                         @RequestParam("picture") MultipartFile picture){
        Films film = new Films(name,year,aboutFilm,country,quality);
        filmService.transferTo(picture);
        film.setPicture(picture.getOriginalFilename());
        return filmsDao.save(film);
    }

    @PostMapping("/delfilm")
    @CrossOrigin(origins = "http://localhost:4200")
    public List<Films> delFilm(@RequestBody Long filmId){
        filmsDao.deleteById(filmId);
        return  filmsDao.findAll();
    }
}
