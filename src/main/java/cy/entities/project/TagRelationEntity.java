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
@Table(name = "tbl_tag_relations",
        uniqueConstraints = { @UniqueConstraint(columnNames =
                { "object_id", "tag_id", "category" }) })
public class TagRelationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "object_id")
    private Long objectId;
    @Column(name = "tag_id")
    private Long idTag;
    private String category;

}
