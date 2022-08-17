package cy.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptRequestModifiModel {
    private Long id;
    private int caseSwitch;
    private String reason;
}
