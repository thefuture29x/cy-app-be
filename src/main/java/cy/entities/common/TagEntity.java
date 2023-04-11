package cy.entities.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@HistoryLogTitle(title = "tag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_tags")
public class TagEntity {
    @HistoryLogTitle(title = "", ignore = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @HistoryLogTitle(title = "tag name")
    @Column(unique = true)
    private String name;
}
