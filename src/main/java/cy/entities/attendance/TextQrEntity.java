package cy.entities.attendance;

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
@Table(name = "tbl_text_qr")
public class TextQrEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String image;
    private String name;
    private String email;
    private String address;
    private String company;
    private String telephone;
    private String fax;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_upload_id")
    private UserEntity uploadedBy;
}
