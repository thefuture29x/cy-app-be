package cy.entities;

import cy.dtos.PayRollDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_user")
@NamedNativeQuery(
        name = "pay_roll",
        query = "SELECT us.user_id as id, us.full_name as nameStaff,\n" +
                "CONCAT(month(:timeEnd), \"/\",\n" +
                "YEAR(:timeEnd)) as monthWorking, \n" +
                "(SELECT 5 * (DATEDIFF(:timeEnd, :timeStart) DIV 7) + MID('0123444401233334012222340111123400012345001234550', 7 * WEEKDAY(:timeStart) + WEEKDAY(:timeEnd) + 1, 1) + 1)  as totalWorkingDay, \n" +
                "(SELECT sum(((TIME_TO_SEC(time_end) - TIME_TO_SEC(time_start)) / 60) / 60)\n" +
                "FROM tbl_request_ot\n" +
                "where user_id = us.user_id and status = 1 and date_ot between :timeStart and :timeEnd) as totalOvertimeHours,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_attend\n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND date_request_attend BETWEEN :timeStart AND :timeEnd) as totalDaysWorked,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_dayoff \n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND is_legit = TRUE\n" +
                "AND date_request_dayoff BETWEEN :timeStart AND :timeEnd) as totalPaidLeaveDays,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_dayoff \n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND is_legit = FALSE\n" +
                "AND date_request_dayoff BETWEEN :timeStart AND :timeEnd) as totalUnpaidLeaveDays\n" +
                "\n" +
                "FROM tbl_user us \n" +
                "LEFT JOIN tbl_user_role usrl ON usrl.user_id = us.user_id\n" +
                "LEFT JOIN tbl_role rl ON usrl.role_id = rl.role_id\n" +
                "WHERE rl.role_id != 1",
        resultSetMapping = "pay_roll_dto"
)
@NamedNativeQuery(
        name = "search_user_pay_roll",
        query = "SELECT us.user_id as id, us.full_name as nameStaff,\n" +
                "CONCAT(month(:timeEnd), \"/\",\n" +
                "YEAR(:timeEnd)) as monthWorking, \n" +
                "(SELECT 5 * (DATEDIFF(:timeEnd, :timeStart) DIV 7) + MID('0123444401233334012222340111123400012345001234550', 7 * WEEKDAY(:timeStart) + WEEKDAY(:timeEnd) + 1, 1)  + 1)  as totalWorkingDay, \n" +
                "(SELECT sum(((TIME_TO_SEC(time_end) - TIME_TO_SEC(time_start)) / 60) / 60)\n" +
                "FROM tbl_request_ot\n" +
                "where user_id = us.user_id and status = 1 and date_ot between :timeStart and :timeEnd) as totalOvertimeHours,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_attend\n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND date_request_attend BETWEEN :timeStart AND :timeEnd) as totalDaysWorked,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_dayoff \n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND is_legit = TRUE\n" +
                "AND date_request_dayoff BETWEEN :timeStart AND :timeEnd) as totalPaidLeaveDays,\n" +
                "\n" +
                "(SELECT COUNT(user_id) FROM tbl_request_dayoff \n" +
                "WHERE user_id = us.user_id\n" +
                "AND `status` = 1\n" +
                "AND is_legit = FALSE\n" +
                "AND date_request_dayoff BETWEEN :timeStart AND :timeEnd) as totalUnpaidLeaveDays\n" +
                "\n" +
                "FROM tbl_user us \n" +
                "WHERE us.full_name LIKE CONCAT('%', :nameUser, '%') ",
        resultSetMapping = "pay_roll_dto"
)
@SqlResultSetMapping(
        name = "pay_roll_dto",
        classes = @ConstructorResult(
                targetClass = PayRollDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "nameStaff", type = String.class),
                        @ColumnResult(name = "monthWorking", type = String.class),
                        @ColumnResult(name = "totalWorkingDay", type = Integer.class),
                        @ColumnResult(name = "totalOvertimeHours", type = Float.class),
                        @ColumnResult(name = "totalDaysWorked", type = Integer.class),
                        @ColumnResult(name = "totalPaidLeaveDays", type = Integer.class),
                        @ColumnResult(name = "totalUnpaidLeaveDays", type = Integer.class)
                }
        )
)

public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name", unique = true)
    private String userName;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "password")
    private String password;
    @Column(name = "phone", unique = true)
    private String phone;
    @Column(name = "sex")
    private String sex;
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "avatar")
    private String avatar;


    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private UserEntity manager;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    public static final String FOLDER = "user/";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roleEntity;

    public static String USER_NO_AVATAR = "https://team-2.s3.ap-northeast-2.amazonaws.com/user/no-avatar.png";

    public static boolean hasRole(String roleName, Set<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .anyMatch(x -> x.getRoleName()
                        .equals(roleName));
    }

    public static String getName(UserEntity userEntity) {
        return (userEntity.getFullName() == null || userEntity.getFullName().isEmpty()) ? userEntity.getUserName() : userEntity.getFullName();
    }

}
