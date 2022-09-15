package cy.repositories.project;

import cy.entities.project.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITagRepository extends JpaRepository<TagEntity,Long> {
    @Query("select t from TagEntity t where t.name = ?1")
    TagEntity findByName(String name);


    @Query("select t from TagEntity t ")
    Page<TagEntity> findAllByPage(Pageable pageable);
}
