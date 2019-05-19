package okten.megaproject.dao;

import okten.megaproject.models.Films;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmsDao extends JpaRepository<Films, Long> {
    Films findFilmsByName(String name);

    @Override
    Films getOne(Long aLong);
}
