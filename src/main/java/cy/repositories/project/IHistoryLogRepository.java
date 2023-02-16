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

    @Query(value = "SELECT DISTINCT * FROM `tbl_historys` \n" +
            "WHERE category = 'BUG' AND object_id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg  \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id\n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tJOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "\tWHERE pr.id = ?1\n" +
            ")\n" +
            "AND (content LIKE '%đã thêm mới bug%' OR content LIKE '%đã cập nhật bug%' OR content LIKE '%đã xóa bug%')\n",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryOfBug(Long idProject,Pageable pageable);
}
