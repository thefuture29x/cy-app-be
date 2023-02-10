package cy.models.project;

import cy.utils.Const;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class SubTaskUpdateModel {
    @NotNull
    private Const.status newStatus;
    // If newStatus is IN_REVIEW, then reviewerIdList must be not null
    private List<Long> reviewerIdList;
}
