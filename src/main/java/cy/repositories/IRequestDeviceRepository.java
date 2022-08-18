package cy.repositories;

import cy.entities.RequestDayOffEntity;
import cy.entities.RequestDeviceEntity;
import cy.entities.RequestModifiEntity;
import cy.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRequestDeviceRepository extends JpaRepository<RequestDeviceEntity, Long>, JpaSpecificationExecutor<RequestDeviceEntity> {
    @Query(value = "SELECT * FROM `tbl_request_device` \n " +
            "WHERE assign_id = ?1 \n " +
            "and created_date between ?2 and ?3", nativeQuery = true)
    Page<RequestDeviceEntity> getAllRequestSendMe(Long id, String startTime, String endTime,Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_device` \n " +
            "WHERE user_id = ?1", nativeQuery = true)
    Page<RequestDeviceEntity> getAllRequestCreateByMe(Long id, Pageable pageable);
}
