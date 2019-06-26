package okten.megaproject.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;
    private int userID;
    private String username;
    private String avatar;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Films film;

}
