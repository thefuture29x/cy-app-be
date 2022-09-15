package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private UserEntity userId;

    private String category;

    private String content;
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
//    @OneToMany
//    @JoinColumn(name = "object_id", insertable = false, updatable = false)
//    @Where(clause = "category='COMMENT'")
//    private List<FileEntity> attachFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_parent")
    private CommentEntity idParent;

    private Long ObjectId;
}
