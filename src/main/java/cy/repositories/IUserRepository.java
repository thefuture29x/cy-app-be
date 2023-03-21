package cy.repositories;

import cy.dtos.UserDto;
import cy.dtos.attendance.PayRollDto;
import cy.dtos.project.UserMetaDto;
import cy.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByUserName(String username);

    UserEntity findByUserName(String username);

    UserEntity findByEmail(String email);

    UserEntity findByPhone(String phone);

    @Query(value = "select u.user_id from tbl_user as u join user_role as ur on ur.user_id=u.user_id join tbl_role as r on r.role_id = ur.role_id  where r.role_name = ?1", nativeQuery = true)
    List<Long> getAllIdsByRole(String role);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String userName, String userName1);

    Optional<List<UserEntity>> findAllByUserIdIsIn(List<Long> ids);

    @Query(value = "select * from tbl_user as u join tbl_user_role as ur on ur.user_id=u.user_id join tbl_role as r on r.role_id = ur.role_id  where r.role_name = ?1", nativeQuery = true)
    List<UserEntity> findAllByRoleName(String roleName);

    @Query(value = "select DISTINCT u.* from tbl_user as u left join tbl_user_role as ur on ur.user_id=u.user_id join tbl_role as r on r.role_id = ur.role_id  where r.role_name not in ?1 and u.status = true", nativeQuery = true)
    List<UserEntity> findAllByRoleName(List<String> roles);

    @Query(value = "SELECT us.user_id FROM `tbl_user_role` usrl JOIN tbl_role rl ON usrl.role_id = rl.role_id JOIN tbl_user us ON usrl.user_id = us.user_id WHERE rl.role_id != 1 ", nativeQuery = true)
    List<Long> findAllUserWithoutRoleAdmin();
    @Query(name = "pay_roll", nativeQuery = true)
    List<PayRollDto> calculatePayRoll(@Param("timeStart")String timeStart, @Param("timeEnd")String timeEnd);
    @Query(name = "search_user_pay_roll", nativeQuery = true)
    List<PayRollDto> searchUserPayRoll(@Param("timeStart")String timeStart, @Param("timeEnd")String timeEnd, @Param("nameUser") String nameUser);
    @Query("select new cy.dtos.UserDto(u) from UserEntity u inner join UserProjectEntity up on u.userId = up.idUser where up.category = ?1 and up.type=?2 and up.objectId=?3")
    List<UserDto> getByCategoryAndTypeAndObjectid(String category, String type, Long objectId);

    @Query("select new cy.dtos.project.UserMetaDto(u) from UserEntity u inner join UserProjectEntity up on u.userId = up.idUser where up.category = ?1 and up.type=?2 and up.objectId=?3")
    List<UserMetaDto> getByCategoryAndTypeAndObjectIdUserMetaDto(String category, String type, Long objectId);

    @Query(value = "SELECT DISTINCT us.* FROM tbl_user_projects uspr \n" +
            "JOIN tbl_user us ON uspr.user_id = us.user_id\n" +
            "WHERE uspr.category = ?1 AND uspr.type = ?2 AND uspr.object_id = ?3\n",nativeQuery = true)
    List<UserEntity> getAllByCategoryAndTypeAndObjectId(String category, String type, Long objectId);

    @Query(value = "SELECT DISTINCT us.user_id FROM tbl_user_projects uspr \n" +
            "JOIN tbl_user us ON uspr.user_id = us.user_id\n" +
            "WHERE uspr.category = ?1 AND (uspr.type = ?3) AND uspr.object_id = ?2\n",nativeQuery = true)
    List<Long> getAllIdDevByTypeAndObjectId(String category, Long objectId, String type);

}
