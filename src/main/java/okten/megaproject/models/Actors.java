package okten.megaproject.models;

import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Actors {
    private String name;
    private String image;

    public Actors(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
