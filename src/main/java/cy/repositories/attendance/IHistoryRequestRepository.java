package cy.repositories.attendance;

import cy.entities.attendance.HistoryRequestEntity;
import cy.entities.attendance.RequestAttendEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistoryRequestRepository extends JpaRepository<HistoryRequestEntity, Long> {
    HistoryRequestEntity findByRequestAttend(RequestAttendEntity requestAttendEntity);

    @Query("delete from HistoryRequestEntity h where h.id in ?1")
    boolean deleteByIds(List<Long> ids);
    @Query("select h from HistoryRequestEntity h order by h.dateHistory desc")
    Page<HistoryRequestEntity> findAll(Pageable pageable);
}
