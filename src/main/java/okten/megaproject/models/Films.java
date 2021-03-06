package okten.megaproject.models;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Films {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String year;
    private String aboutFilm;
    private String country;
    private String quality;
    private ArrayList<String> genre;
    private String picture;
    private String movie;
    //@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private User user;
    public Films(String name, String year, String aboutFilm, String country, String quality) {
        this.name = name;
        this.year = year;
        this.aboutFilm = aboutFilm;
        this.country = country;
        this.quality = quality;
    }
}


