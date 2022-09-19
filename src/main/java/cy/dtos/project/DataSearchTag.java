package cy.dtos.project;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataSearchTag {
    private Long id;
    private String name;
    private String createBy;
    private Date startDate;
    private Date endDate;
    private String status;
    private String category;


}
