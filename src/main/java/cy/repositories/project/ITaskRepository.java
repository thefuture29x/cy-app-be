package cy.repositories.project;

import cy.entities.project.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "update tbl_order set status = 'CANCELED' where( order_id>0 and status = 'PAYING' and (updated_date < DATE_SUB(NOW(), INTERVAL '1' HOUR)))",nativeQuery = true)
    void deleteTaskEntitiesByIsDeleted();
}
