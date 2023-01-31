package cy.repositories.project;

import cy.entities.project.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IFileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {
    FileEntity findByFileNameAndObjectId(String fileName, Long objectId);

    @Query(value = "select * from tbl_files where category LIKE ?1 and object_id = ?2", nativeQuery = true)
    List<FileEntity> getByCategoryAndObjectId(String category, Long objectId);
    
    List<FileEntity> findByCategoryAndObjectId(String category, Long objectId);
    FileEntity findByLinkAndObjectId( String link, Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `tbl_files` WHERE category =  ?1 AND object_id = ?2 ",nativeQuery = true)
    void deleteByCategoryAndObjectId(String category,Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `tbl_files` WHERE id = :deletingId ", nativeQuery = true)
    void deleteByIdNative(Long deletingId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `tbl_files`\n" +
            "WHERE category = ?2 AND object_id = ?3 AND link NOT IN " +
            "?1",nativeQuery = true)
    void deleteFileExistInObject(List<String> listFile, String category, Long objectId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `tbl_files` WHERE category = ?1 AND object_id = ?2",nativeQuery = true)
    void deleteAllByCategoryAndObjectId(String category, Long objectId);


}
