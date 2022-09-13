package cy.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_pay_roll")
public class PayRollEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameStaff;

    @Column(name = "total_working_day")
    private int totalWorkingDay;

    @Column(name = "total_days_worked")
    private int totalDaysWorked;

    @Column(name = "total_paid_leave_days")
    private int totalPaidLeaveDays;

    @Column(name = "total_unpaid_leave_days")
    private int totalUnpaidLeaveDays;

    @Column(name = "total_overtime_hours_in_week")
    private Float totalOvertimeHoursInWeek;
    @Column(name = "total_overtime_hours_in_weekend")
    private Float totalOvertimeHoursInWeekend;
    @Column(name = "total_overtime_hours_in_holiday")
    private Float totalOvertimeHoursInHoliday;

    private int month;

    private int year;





}
