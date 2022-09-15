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
@Table(name = "tbl_files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;//link trên s3

    @Column(name = "file_type")
    private String fileType;//định dạng file

    @Column(name = "object_id")
    private Long objectId;//id của đối tượng

    private String category;//file thuộc đối project hay bug hay task hay subtask


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_upload_id")
    private UserEntity uploadedBy;
}
