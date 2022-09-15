package cy.repositories.project;

import cy.entities.project.TagEntity;
import cy.entities.project.TagRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITagRelationRepository extends JpaRepository<TagRelationEntity,Long> {
}
