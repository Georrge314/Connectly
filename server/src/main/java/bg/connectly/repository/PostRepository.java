package bg.connectly.repository;

import bg.connectly.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.author.email = :email ORDER BY p.createdAt DESC")
    Page<Post> findByAuthorEmail(String email, Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
