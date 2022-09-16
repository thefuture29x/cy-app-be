package cy.repositories.project;

import cy.entities.project.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileNameAndObjectId(String fileName, Long objectId);
}
