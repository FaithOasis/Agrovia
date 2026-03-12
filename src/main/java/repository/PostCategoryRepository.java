package repository;

import entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    // Find category by name
    PostCategory findByName(String name);

    // Check if category exists by name
    boolean existsByName(String name);
}