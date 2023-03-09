package cy.repositories.project;

import cy.entities.project.PendingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPendingHistoryRepository extends JpaRepository<PendingHistoryEntity,Long> {
    @Query(value = "SELECT * FROM tbl_pending_historys WHERE category = ?1 AND object_id = ?2 AND end_date IS NULL ORDER BY id DESC LIMIT 1",nativeQuery = true)
    PendingHistoryEntity findByCategoryAndObjectId(String category,Long objectId);
}
