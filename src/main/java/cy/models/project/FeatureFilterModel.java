package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.utils.Const;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFilterModel {
    private String searchField;
    private String name;
    private String description;
    private Const.status status;
    private Const.priority priority;
    private String minDate;
    private String maxDate;
}
