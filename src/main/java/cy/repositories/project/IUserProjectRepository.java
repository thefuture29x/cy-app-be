package cy.repositories.project;

import cy.entities.project.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserProjectRepository extends JpaRepository<UserProjectEntity, Long> {
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2")
    List<UserProjectEntity> getByCategoryAndObjectId(String category, Long objectId);
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2 and up.type = ?3")
    List<UserProjectEntity> getByCategoryAndObjectIdAndType(String category, Long objectId,String type);

    void deleteByCategoryAndObjectId(String category, Long objectId);
}
