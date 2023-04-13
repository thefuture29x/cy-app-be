package cy.repositories.mission;

import cy.entities.mission.AssignCheckListEntity;
import cy.entities.mission.AssignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IAssignCheckListRepository extends JpaRepository<AssignCheckListEntity, Long> {
    AssignCheckListEntity findByContentAndAssign_Id(String content,Long assignId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_assign_check_list WHERE content = ?1 AND assign_id = ?2",nativeQuery = true)
    void deleteByContentCheckListAndAssignId(String content,Long id);

    List<AssignCheckListEntity> findAllByAssign_Id(Long id);

}
