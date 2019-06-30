package okten.megaproject.models;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Actors  implements Serializable {
    private String name;
    private String image;

    public Actors(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
