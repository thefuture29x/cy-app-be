package cy.repositories;

import cy.entities.RequestModifiEntity;
import cy.entities.RequestOTEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRequestOTRepository extends JpaRepository<RequestOTEntity, Long>, JpaSpecificationExecutor<RequestOTEntity> {
    @Query(value = "SELECT * FROM `tbl_request_ot` \n " +
            "WHERE user_id = ?1 \n " +
            "and created_date between ?2 and ?3", nativeQuery = true)
    List<RequestOTEntity> getAllRequestSendMe(Long id, String startTime, String endTime);
}
