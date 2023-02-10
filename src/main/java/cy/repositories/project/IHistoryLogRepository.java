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

    @Query(value = "SELECT * FROM `tbl_historys` WHERE category = ?1 AND content LIKE '%đã thêm mới project%'",nativeQuery = true)
    Page<HistoryEntity> getAllHistoryCreateObject(Const.tableName category, Pageable pageable);
}
