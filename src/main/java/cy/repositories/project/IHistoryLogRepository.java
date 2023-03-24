package cy.repositories.project;

import cy.dtos.project.HistoryLogDto;
import cy.entities.project.HistoryEntity;
import cy.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IHistoryLogRepository extends JpaRepository<HistoryEntity, Long>, JpaSpecificationExecutor<HistoryEntity> {

    @Query(value = "SELECT DISTINCT * FROM tbl_historys WHERE category = 'BUG' \n" +
            "AND object_id IN \n" +
            "(\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg  \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id\n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tJOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "\tWHERE pr.id = ?1 \n" +
            ")\n" +
            "OR object_id IN \n" +
            "(\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg  \n" +
            "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tJOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "\tWHERE pr.id = ?1 \n" +
            ")\n" +
            "AND (content LIKE '%đã thêm mới bug%' OR content LIKE '%đã cập nhật bug%' OR content LIKE '%đã xóa bug%')",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfBugInProject(Long idProject,Pageable pageable);


    @Query(value = "SELECT * FROM `tbl_historys` \n" +
            "WHERE object_id = ?1 and category = 'PROJECT' OR (\n" +
            "\tobject_id IN (\n" +
            "\t\tSELECT id FROM `tbl_features` WHERE project_id = ?1 \n" +
            "\t) AND category = 'FEATURE' AND (content LIKE '%đã thêm mới feature%' OR content LIKE '%đã cập nhật feature%' OR content LIKE '%đã xóa feature%')\n" +
            ")\n",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfProject(Long idProject, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_historys` \n" +
            "WHERE object_id = ?1 and category = 'FEATURE' OR (\n" +
            "\tobject_id IN (\n" +
            "\t\tSELECT id FROM `tbl_tasks` WHERE feature_id = ?1 \n" +
            "\t) AND category = 'TASK' AND (content LIKE '%đã thêm mới task%' OR content LIKE '%đã cập nhật task%' OR content LIKE '%đã xóa task%')\n" +
            ")\n",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfFeature(Long idFeature, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_historys` \n" +
            "WHERE object_id = ?1 and category = 'TASK' \n" +
            "OR (\n" +
            "\tobject_id IN (\n" +
            "\t\tSELECT id FROM `tbl_sub_tasks` WHERE task_id = ?1 \n" +
            "\t) \n" +
            "\tAND category = 'SUBTASK' AND (content LIKE '%đã thêm mới sub task%' OR content LIKE '%đã cập nhật sub task%' OR content LIKE '%đã xóa sub task%')\n" +
            ")\n" +
            "OR (\n" +
            "\tobject_id IN (\n" +
            "\t\tSELECT id FROM `tbl_bugs` WHERE task_id = ?1 \n" +
            "\t) \n" +
            "\tAND category = 'BUG' AND (content LIKE '%đã thêm mới bug%' OR content LIKE '%đã cập nhật bug%' OR content LIKE '%đã xóa bug%')\n" +
            ")",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfTask(Long idTask, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_historys` \n" +
            "WHERE object_id = ?1 and category = 'SUBTASK' AND (content LIKE '%đã thêm mới sub task%' OR content LIKE '%đã cập nhật sub task%' OR content LIKE '%đã xóa sub task%')\n" +
            "OR (\n" +
            "\tobject_id IN (\n" +
            "\t\tSELECT id FROM `tbl_bugs` WHERE sub_task_id = ?1 \n" +
            "\t) \n" +
            "\tAND category = 'BUG' AND (content LIKE '%đã thêm mới bug%' OR content LIKE '%đã cập nhật bug%' OR content LIKE '%đã xóa bug%')\n" +
            ")\n",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfSubTask(Long idSubTask, Pageable pageable);

    @Query(value = "SELECT * FROM tbl_historys\n" +
            "WHERE object_id = ?1 AND category = 'BUG'",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfBug(Long idSubTask, Pageable pageable);


}
