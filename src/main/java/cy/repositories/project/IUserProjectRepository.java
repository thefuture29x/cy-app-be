package cy.repositories.project;

import cy.entities.project.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface IUserProjectRepository extends JpaRepository<UserProjectEntity, Long> {
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2")
    List<UserProjectEntity> getByCategoryAndObjectId(String category, Long objectId);
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2 and up.type = ?3")
    List<UserProjectEntity> getByCategoryAndObjectIdAndType(String category, Long objectId,String type);

    void deleteByCategoryAndObjectId(String category, Long objectId);

    @Query(value = "DELETE FROM tbl_user_projects WHERE id = :deletingId", nativeQuery = true)
    void deleteByIdNative(Long deletingId);

    @Query("SELECT up.idUser FROM UserProjectEntity up WHERE up.category = ?1 AND up.objectId = ?2 AND up.type = ?3")
    List<Long> getIdByCategoryAndObjectIdAndType(String category, Long objectId, String type);
}
