package cy.repositories.project;

import cy.entities.project.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserProjectRepository extends JpaRepository<UserProjectEntity, Long> {
    @Query("select up from UserProjectEntity up where up.category LIKE ?1 and up.objectId = ?2")
    List<UserProjectEntity> getByCategoryAndObjectId(String category, Long objectId);

    void deleteByCategoryAndObjectIdAndIdUser(String category, Long objectId, Long idUser);
}
