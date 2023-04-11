package cy.entities.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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

    @HistoryLogTitle(title = "", ignore = true)
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

}
