package okten.megaproject.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
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
    @ManyToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<User> user = new ArrayList<>();

    public Films(String name, String year, String aboutFilm, String country, String quality) {
        this.name = name;
        this.year = year;
        this.aboutFilm = aboutFilm;
        this.country = country;
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "Films{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", aboutFilm='" + aboutFilm + '\'' +
                ", country='" + country + '\'' +
                ", quality='" + quality + '\'' +
                ", genre=" + genre +
                ", picture='" + picture + '\'' +
                ", movie='" + movie + '\'' +
                '}';
    }

}


