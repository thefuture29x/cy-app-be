package cy.repositories;

import cy.entities.project.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITagRepository extends JpaRepository<TagEntity,Long> {
    TagEntity findByName(String name);
    Page<TagEntity> findAllByPage(Pageable pageable);
}
