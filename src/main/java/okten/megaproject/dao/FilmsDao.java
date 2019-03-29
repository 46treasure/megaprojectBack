package okten.megaproject.dao;

import okten.megaproject.models.Films;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmsDao extends JpaRepository<Films, Long> {
}
