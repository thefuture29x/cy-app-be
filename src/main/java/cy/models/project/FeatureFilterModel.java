package cy.models.project;

import cy.utils.Const;
import lombok.*;

import javax.validation.constraints.NotNull;

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
}
