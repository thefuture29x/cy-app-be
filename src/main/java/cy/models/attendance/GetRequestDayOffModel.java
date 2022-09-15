package cy.models.attendance;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class GetRequestDayOffModel {
    @Valid
    @NotNull
    private Long uid;

    @Valid
    @NotBlank
    private String dateStart;

    @Valid
    @NotBlank
    private String dateEnd;

    @Valid
    @NotNull
    private Boolean isLegit;

    @Valid
    @NotNull
    private Integer status;
}
