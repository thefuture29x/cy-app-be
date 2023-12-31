package cy.repositories.project;

import cy.entities.project.ProjectEntity;
import cy.entities.project.UserViewProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUserViewProjectRepository extends JpaRepository<UserViewProjectEntity,Long> {
    @Query(value = "SELECT uvp.* FROM `tbl_user_view_projects` uvp \n" +
            "JOIN tbl_projects pr ON pr.id = uvp.project_id\n" +
            "JOIN tbl_user_projects uspr ON uspr.user_id = ?1 AND uspr.object_id = pr.id AND uspr.category = 'PROJECT'\n" +
            "WHERE pr.is_deleted = 0  \n" +
            "AND 0 <> (SELECT COUNT(type) FROM tbl_user_projects WHERE user_id = ?1 AND category = 'PROJECT' AND object_id = pr.id)\n" +
            "AND uvp.created_date IN (\n" +
            "SELECT MAX(created_date) AS 'created_date' FROM `tbl_user_view_projects`\n" +
            "WHERE user_id = ?1\n" +
            "GROUP BY project_id\n" +
            ")ORDER BY uvp.created_date DESC LIMIT 3",nativeQuery = true)
    List<UserViewProjectEntity> findProjectRecentlyViewed(Long id);
}
