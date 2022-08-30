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

    private int totalWorkingDay;

    private int totalDaysWorked;

    private int totalPaidLeaveDays;

    private int totalUnpaidLeaveDays;

    private Float totalOvertimeHours;

    private int month;

    private int year;

    public static PayRollDto entityToDto(PayRollEntity object){
        return PayRollDto.builder()
                .id(object.getId())
                .nameStaff(object.getNameStaff())
                .totalWorkingDay(object.getTotalWorkingDay())
                .totalDaysWorked(object.getTotalDaysWorked())
                .totalPaidLeaveDays(object.getTotalPaidLeaveDays())
                .totalUnpaidLeaveDays(object.getTotalUnpaidLeaveDays())
                .totalOvertimeHours(object.getTotalOvertimeHours())
                .month(object.getMonth())
                .year(object.getYear())
                .build();
    }




}
