package cy.entities.project;

import cy.entities.UserEntity;
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
@Table(name = "tbl_files", uniqueConstraints = { @UniqueConstraint(columnNames =
        { "object_id", "category" }) })
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;

    @Column(name = "file_type")
    private String fileType;
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "object_id")
    private Long objectId;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_upload_id")
    private UserEntity uploadedBy;
}
