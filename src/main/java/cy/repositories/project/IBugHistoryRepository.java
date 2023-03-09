package cy.repositories.project;

import cy.dtos.project.BugHistoryDto;
import cy.entities.project.BugHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface IBugHistoryRepository extends JpaRepository<BugHistoryEntity, Long> {
    List<BugHistoryEntity> findAllByBugId(Long bugId);

    @Query(value = "select * from tbl_bug_historys where bug_id = ?1", nativeQuery = true)
    List<BugHistoryDto> findByBugId(Long bugId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_bug_historys SET detail = ?1 WHERE id = ?2",nativeQuery = true)
    void updateDetailHistoryBug(String detail,Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_bug_historys WHERE bug_id = ?1 AND end_date IS NULL ORDER BY id DESC LIMIT 1",nativeQuery = true)
    void deleteLastBugHistoryOfBug(Long idBug);

}
