package cy.repositories.project;

import cy.entities.project.ProjectEntity;
import cy.entities.project.UserViewProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUserViewProjectRepository extends JpaRepository<UserViewProjectEntity,Long> {
    @Query(value = "SELECT * FROM `tbl_user_view_projects`\n" +
            "WHERE created_date IN (\n" +
            "\tSELECT MAX(created_date) AS 'created_date' FROM `tbl_user_view_projects`\n" +
            "\tWHERE user_id = ?1\n" +
            "\tGROUP BY project_id\n" +
            ")\n" +
            "ORDER BY created_date DESC\n" +
            "LIMIT 3",nativeQuery = true)
    List<UserViewProjectEntity> findProjectRecentlyViewed(Long id);
}
