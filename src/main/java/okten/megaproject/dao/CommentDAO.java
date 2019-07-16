package okten.megaproject.dao;

import okten.megaproject.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDAO extends JpaRepository<Comments, Integer> {
}
