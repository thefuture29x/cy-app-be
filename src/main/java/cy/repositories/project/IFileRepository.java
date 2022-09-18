package cy.repositories.project;

import cy.entities.project.FileEntity;
import cy.entities.project.TagRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public interface IFileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {
    FileEntity findByFileNameAndObjectId(String fileName, Long objectId);

    @Query("select tr from FileEntity tr where tr.category LIKE ?1 and tr.objectId = ?2")
    List<FileEntity> getByCategoryAndObjectId(String category, Long objectId);
}
