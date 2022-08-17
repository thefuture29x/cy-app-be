package cy.repositories;

import cy.dtos.RequestAttendDto;
import cy.entities.RequestAttendEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long> {
    @Query(value = "SELECT r FROM RequestAttendEntity r WHERE r.createBy.userId = ?1 AND r.dateRequestAttend = ?2")
    RequestAttendEntity userAlreadyRequest(Long user_id, Date date);

    @Query(value = "SELECT NEW cy.dtos.RequestAttendDto(r) FROM RequestAttendEntity r WHERE r.createBy.userId = ?1")
    List<RequestAttendDto> findByUserId(Long user_id);
}
