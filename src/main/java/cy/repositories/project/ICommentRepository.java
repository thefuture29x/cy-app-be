package cy.repositories.project;

import cy.entities.project.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ICommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_comments WHERE id = ?1 OR id_parent = ?1",nativeQuery = true)
    void deleteComment(Long id);

    void deleteAllByIdParent_Id(Long id);

    @Query(value = "SELECT is_deleted FROM `tbl_comments` WHERE id = ?1", nativeQuery = true)
    boolean checkIsDeleted(Long id);
}
