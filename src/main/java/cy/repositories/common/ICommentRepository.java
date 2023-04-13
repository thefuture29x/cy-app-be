package cy.repositories.common;

import cy.entities.common.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Repository
public interface ICommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_comments WHERE id = ?1 OR id_parent = ?1",nativeQuery = true)
    void deleteComment(Long id);

    void deleteAllByIdParent_Id(Long id);

    List<CommentEntity> findAllByCategoryAndObjectIdAndIdParent(String category,Long objectId,Long idParent);

}
