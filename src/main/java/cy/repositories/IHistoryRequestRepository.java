package cy.repositories;

import cy.entities.HistoryRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IHistoryRequestRepository extends JpaRepository<HistoryRequestEntity, Long> {
    @Query("delete from HistoryRequestEntity h where h.id in ?1")
    boolean deleteByIds(List<Long> ids);
}
