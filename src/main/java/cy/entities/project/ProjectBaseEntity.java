package cy.entities.project;

import cy.entities.common.HistoryLogTitle;
import cy.entities.common.UserEntity;
import cy.entities.common.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProjectBaseEntity {
    @HistoryLogTitle(title = "", ignore = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @HistoryLogTitle(title = "", ignore = true)
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @HistoryLogTitle(title = "", ignore = true)
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

    @HistoryLogTitle(title = "ngày bắt đầu", isDateType = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @HistoryLogTitle(title = "ngày kết thúc", isDateType = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;


    @HistoryLogTitle(title = "trạng thái")
    private String status;

    @HistoryLogTitle(title = "", ignore = true)
    private Boolean isDeleted = false;

    @HistoryLogTitle(title = "tên")
    private String name;

    @HistoryLogTitle(title = "mô tả")
    @Column(columnDefinition="TEXT")
    private String description;

    @HistoryLogTitle(title = "")
    private Boolean isDefault = false;

    @HistoryLogTitle(title = "từ khóa", isTagFields = true)
    @Transient
    private List<TagEntity> tagList;



}
