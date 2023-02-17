package cy.repositories.project;

import cy.entities.project.FeatureEntity;
import cy.entities.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IProjectRepository  extends JpaRepository<ProjectEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "select * from tbl_projects where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<ProjectEntity> checkProjectDelete();

    @Query(value = "SELECT is_deleted FROM `tbl_projects` WHERE id = ?1", nativeQuery = true)
    boolean checkIsDeleted(Long id);

    List<ProjectEntity> getAllByNameAndIsDeleted(String name, Boolean isDeleted);
}
