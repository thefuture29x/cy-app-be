package cy.repositories.common;

import cy.entities.common.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IUserProjectRepository extends JpaRepository<UserProjectEntity, Long> {
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2")
    List<UserProjectEntity> getByCategoryAndObjectId(String category, Long objectId);
    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2 and up.type = ?3")
    List<UserProjectEntity> getByCategoryAndObjectIdAndType(String category, Long objectId,String type);

    @Query("select up from UserProjectEntity up where up.category = ?1 and up.objectId = ?2 and up.type = ?3 and up.idUser = ?4 ")
    List<UserProjectEntity> getByCategoryAndObjectIdAndTypeAndIdUser(String category, Long objectId,String type, Long idUser);
    void deleteByCategoryAndObjectId(String category, Long objectId);

    @Query(value = "DELETE FROM tbl_user_projects WHERE id = :deletingId", nativeQuery = true)
    void deleteByIdNative(Long deletingId);

    @Query("SELECT up.idUser FROM UserProjectEntity up WHERE up.category = ?1 AND up.objectId = ?2 AND up.type = ?3")
    List<Long> getIdByCategoryAndObjectIdAndType(String category, Long objectId, String type);

    @Query(value = "SELECT up FROM UserProjectEntity up WHERE up.category = ?1 AND up.idUser = ?2 AND up.objectId = ?3 AND up.type = ?4")
    List<UserProjectEntity> getByAllAttrs(String category, Long userId, Long objectId, String type);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `tbl_user_projects` WHERE user_id = ?1 AND category = 'PROJECT' AND type = ?2 AND object_id = ?3", nativeQuery = true)
    void deleteByIdUserAndTypeAndObjectId(Long idUser,String type,Long objectId);

    @Query(value = "SELECT DISTINCT uspr.user_id FROM tbl_user_projects uspr \n" +
            "JOIN tbl_projects pr ON uspr.object_id = pr.id AND category = 'PROJECT' and type IN ?2 \n" +
            "WHERE uspr.object_id = (\n" +
            "\tSELECT project_id FROM tbl_features WHERE id = ?1\n" +
            ")",nativeQuery = true)
    List<Long> getAllIdDevOfProjectByFeatureIdInThisProject(Long idFeature, List<String> listType);
    @Query(value = "SELECT DISTINCT uspr.user_id FROM tbl_user_projects uspr \n" +
            "JOIN tbl_projects pr ON uspr.object_id = pr.id AND category = 'PROJECT' and type IN ?2 \n" +
            "WHERE uspr.object_id = (\n" +
            "\tSELECT project_id FROM tbl_features WHERE id = (\n" +
            "\t\tSELECT feature_id FROM tbl_tasks WHERE id = ?1\n" +
            "\t)\n" +
            ")\n",nativeQuery = true)
    List<Long> getAllIdDevOfProjectByTaskIdInThisProject(Long idTask, List<String> listType);

    @Query(value = "SELECT DISTINCT uspr.user_id FROM tbl_user_projects uspr \n" +
            "JOIN tbl_projects pr ON uspr.object_id = pr.id AND category = 'PROJECT' and type IN ?2 \n" +
            "WHERE uspr.object_id = (\n" +
            "\tSELECT project_id FROM tbl_features WHERE id = (\n" +
            "\t\tSELECT feature_id FROM tbl_tasks WHERE id = (\n" +
            "\t\t\tSELECT task_id FROM tbl_sub_tasks WHERE id = ?1\n" +
            "\t\t)\n" +
            "\t)\n" +
            ")",nativeQuery = true)
    List<Long> getAllIdDevOfProjectBySubTaskIdInThisProject(Long idSubTask, List<String> listType);

    @Query(value = "SELECT DISTINCT uspr.user_id FROM tbl_user_projects uspr \n" +
            "JOIN tbl_projects pr ON uspr.object_id = pr.id AND category = 'PROJECT' and type IN ?2 \n" +
            "WHERE uspr.object_id = (\n" +
            "\tSELECT project_id FROM tbl_features WHERE id = (\n" +
            "\t\tSELECT feature_id FROM tbl_tasks WHERE id = (\n" +
            "\t\t\tSELECT task_id FROM tbl_sub_tasks WHERE id = (SELECT sub_task_id FROM tbl_bugs WHERE id = ?1)\n" +
            "\t\t)\n" +
            "\t)\n" +
            ")\n" +
            "OR\n" +
            "uspr.object_id = (\n" +
            "\tSELECT project_id FROM tbl_features WHERE id = (\n" +
            "\t\tSELECT feature_id FROM tbl_tasks WHERE id = (SELECT task_id FROM tbl_bugs WHERE id = ?1)\n" +
            "\t)\n" +
            ")",nativeQuery = true)
    List<Long> getAllIdDevOfProjectByBugIdInThisProject(Long idBug, List<String> listType);
}
