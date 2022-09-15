package cy.dtos.attendance;

import lombok.*;

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
