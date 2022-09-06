package cy.models;

import cy.entities.PayRollEntity;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PayRollModel {
    private Long id;

    private String nameStaff;

    private int totalWorkingDay;

    private int totalDaysWorked;

    private int totalPaidLeaveDays;

    private int totalUnpaidLeaveDays;

    private Float totalOvertimeHours;

    private int month;

    private int year;

}
