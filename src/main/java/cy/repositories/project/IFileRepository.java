package cy.repositories.project;

import cy.entities.project.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IFileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {
}
