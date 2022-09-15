package cy.models.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeviceUpdateStatusModel {
    private Long id;
    private int switchCase;
    private String reasonCancel;
}
