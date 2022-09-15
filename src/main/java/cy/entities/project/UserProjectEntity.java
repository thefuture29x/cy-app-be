package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user_projects",
        uniqueConstraints = { @UniqueConstraint(columnNames =
                { "object_id", "user_id",  "type", "category" }) })
public class UserProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "object_id")
    private Long objectId;
    @Column(name = "user_id")
    private Long idUser;
    private String type;
    private String category;

}
