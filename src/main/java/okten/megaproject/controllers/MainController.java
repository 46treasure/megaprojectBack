package okten.megaproject.controllers;
import okten.megaproject.Service.FilmService;
import okten.megaproject.dao.FilmsDao;
import okten.megaproject.dao.UserDao;
import okten.megaproject.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class MainController {
    private String path = "http://127.0.0.1:8887/";
    private final FilmsDao filmsDao;
    private final FilmService filmService;
    private final UserDao userDao;

    @Autowired
    public MainController(FilmsDao filmsDao, FilmService filmService, UserDao userDao) {
        this.filmsDao = filmsDao;
        this.filmService = filmService;
        this.userDao = userDao;
    }

    @Autowired
    @GetMapping("/home")
    @CrossOrigin("http://localhost:4200/")
    public List<Films> allFilms() {
        return filmsDao.findAll();

    }

    @GetMapping("/topTen")
    public List<Films> topTen() {
        return filmService.topTen();
    }

    @GetMapping("/newFilms")
    public List<Films> newFilms(){
        return filmService.newFilms();
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
        Collections.addAll(genres, genre.split(","));
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

    private ArrayList<String> createList(MultipartFile file1,MultipartFile file2,MultipartFile file3,MultipartFile file4) {
        ArrayList<String> list = new ArrayList<>();
        transferFour(file1,file2,file3,file4);
        list.add(path + file1.getOriginalFilename());
        list.add(path + file2.getOriginalFilename());
        list.add(path + file3.getOriginalFilename());
        list.add(path + file4.getOriginalFilename());
        return list;
    }
        private ArrayList<Actors> addActors (String actor1, MultipartFile imgActor1,
                         String actor2, MultipartFile imgActor2,
                         String actor3, MultipartFile imgActor3,
                         String actor4, MultipartFile imgActor4) {
        ArrayList<Actors> actors = new ArrayList<>();
        transferFour(imgActor1,imgActor2,imgActor3,imgActor4);
        actors.add(new Actors(actor1, path + imgActor1.getOriginalFilename()));
        actors.add(new Actors(actor2, path + imgActor2.getOriginalFilename()));
        actors.add(new Actors(actor3, path + imgActor3.getOriginalFilename()));
        actors.add(new Actors(actor4, path + imgActor4.getOriginalFilename()));
        return actors;
         }

         private void transferFour( MultipartFile file1,MultipartFile file2,MultipartFile file3,MultipartFile file4) {
             filmService.transferTo(file1);
             filmService.transferTo(file2);
             filmService.transferTo(file3);
             filmService.transferTo(file4);
         }
    @PostMapping("/getbyid")
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
    @PostMapping("/getComments")
    public List<Comments> getComments (@RequestBody Long id){
        Films current = filmsDao.getOne(id);
        return current.getComments();

    }
}
