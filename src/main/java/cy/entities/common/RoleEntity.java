package cy.entities.common;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {
    public final static String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public final static String ADMIN = "ROLE_ADMIN";
    public final static String MANAGER = "ROLE_MANAGER";
    public final static String LEADER = "ROLE_LEADER";
    public final static String EMPLOYEE = "ROLE_EMPLOYEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", unique = true)
    private String roleName;
    @ManyToMany
    private Set<UserEntity> userEntitySet;
}
