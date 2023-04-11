package cy.models.mission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposeModel {
    private Long id;
    private String description;
    private Boolean isDone = false; 
    private Long idAssign;
}

