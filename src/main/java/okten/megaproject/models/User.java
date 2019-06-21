package okten.megaproject.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String status = "offline";
    @Enumerated(EnumType.STRING)
    private UserEnum role = UserEnum.ROLE_USER;
    private ArrayList<Integer> subscribes = new ArrayList<>();
    private ArrayList<Integer> folowing = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)

    private List<Films> usersFilms = new ArrayList<>();

    String userKey = UUID.randomUUID().toString();
    private boolean isActive;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    private boolean isAccountNonExpired = true;

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    private boolean isAccountNonLocked = true;

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    private boolean isCredentialsNonExpired = true;

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    private boolean isEnabled = true;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", Role=" + role +
                ", subscribes=" + subscribes +
                ", folowing=" + folowing +
                ", status='" + status + '\'' +
                ", userKey='" + userKey + '\'' +
                ", isActive=" + isActive +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                '}';
    }

}
