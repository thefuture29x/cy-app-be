package cy.repositories.attendance;

import cy.entities.attendance.RequestDeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IRequestDeviceRepository extends JpaRepository<RequestDeviceEntity, Long>, JpaSpecificationExecutor<RequestDeviceEntity> {
    @Query(value = "SELECT * FROM `tbl_request_device` \n " +
            "WHERE assign_id = ?1 \n " +
            "and created_date between ?2 and ?3 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestDeviceEntity> getAllRequestSendMe(Long id, String startTime, String endTime,Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_device` \n " +
            "WHERE user_id = ?1 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestDeviceEntity> getAllRequestCreateByMe(Long id, Pageable pageable);


    @Query(value = "SELECT e FROM RequestDeviceEntity e " +
            "WHERE e.createBy.userId = ?1 AND e.type = ?2")
    Page<RequestDeviceEntity> filterByType(Long userId, String type, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_device` \n " +
            "ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestDeviceEntity> findAllByPage(Long id, Pageable pageable);
}
