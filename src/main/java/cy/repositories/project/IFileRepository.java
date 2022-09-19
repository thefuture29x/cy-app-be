package cy.repositories.project;

import cy.entities.project.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {
    FileEntity findByFileNameAndObjectId(String fileName, Long objectId);

    List<FileEntity> findByCategoryAndObjectId(String category, Long objectId);
}
