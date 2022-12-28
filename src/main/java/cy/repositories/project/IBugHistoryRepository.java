package cy.repositories.project;

import cy.dtos.project.BugHistoryDto;
import cy.entities.project.BugHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IBugHistoryRepository extends JpaRepository<BugHistoryEntity, Long> {
    List<BugHistoryEntity> findAllByBugId(Long bugId);

    @Query(value = "select * from tbl_bug_historys where bug_id = ?1", nativeQuery = true)
    List<BugHistoryDto> findByBugId(Long bugId);
}
