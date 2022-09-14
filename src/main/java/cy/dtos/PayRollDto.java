package cy.dtos;

import cy.entities.PayRollEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PayRollDto {
    private Long id;

    private String nameStaff;

    private String monthWorking;

    private int totalWorkingDay;

    private Float totalOvertimeHoursInWeek;

    private Float totalOvertimeHoursInWeekend;

    private Float totalOvertimeHoursInHoliday;

    private int totalDaysWorked;

    private int totalPaidLeaveDays;

    private int totalUnpaidLeaveDays;

}
