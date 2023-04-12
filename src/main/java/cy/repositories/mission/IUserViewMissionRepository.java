package cy.repositories.mission;

import cy.entities.mission.UserViewMissionEntity;
import cy.entities.project.UserViewProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUserViewMissionRepository extends JpaRepository<UserViewMissionEntity,Long> {
    @Query(value = "SELECT uvp.* FROM `tbl_user_view_mission` uvp \n" +
            "JOIN tbl_mission pr ON pr.id = uvp.mission_id\n" +
            "JOIN tbl_user_projects uspr ON uspr.user_id = ?1 AND uspr.object_id = pr.id AND uspr.category = 'MISSION'\n" +
            "WHERE pr.is_deleted = 0  \n" +
            "AND 0 <> (SELECT COUNT(type) FROM tbl_user_projects WHERE user_id = ?1 AND category = 'MISSION' AND object_id = pr.id)\n" +
            "AND uvp.created_date IN (\n" +
            "SELECT MAX(created_date) AS 'created_date' FROM `tbl_user_view_mission`\n" +
            "WHERE user_id = ?1\n" +
            "GROUP BY mission_id\n" +
            ")ORDER BY uvp.created_date DESC LIMIT 3",nativeQuery = true)
    List<UserViewMissionEntity> findProjectRecentlyViewed(Long id);
}
