package cy.services.impl;

import cy.dtos.PayRollDto;
import cy.dtos.ResponseDto;
import cy.dtos.UserDto;
import cy.entities.PayRollEntity;
import cy.entities.UserEntity;
import cy.models.PayRollModel;
import cy.models.RequestAttendByNameAndYearMonth;
import cy.repositories.IPayRollRepository;
import cy.repositories.IRequestDayOffRepository;
import cy.repositories.IUserRepository;
import cy.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import cy.entities.PayRollEntity;
import cy.models.PayRollModel;
import cy.repositories.IPayRollRepository;
import cy.services.IPayRollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PayRollServiceImpl implements IPayRollService {
    @Value("${timeKeepingDate}")
    int timeKeepingDate;
    @Autowired
    IPayRollRepository iPayRollRepository;
    @Autowired
    IRequestDayOffService iRequestDayOffService;
    @Autowired
    IRequestAttendService iRequestAttendService;
    @Autowired
    IRequestOTService iRequestOTService;
    @Autowired
    IUserRepository iUserRepository;



    @Override
    public List<PayRollDto> findAll() {
        return null;
    }

    @Override
    public Page<PayRollDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<PayRollDto> findAll(Specification<PayRollEntity> specs) {
        return null;
    }

    @Override
    public Page<PayRollDto> filter(Pageable page, Specification<PayRollEntity> specs) {
        return null;
    }

    @Override
    public PayRollDto findById(Long id) {
        return null;
    }

    @Override
    public PayRollEntity getById(Long id) {
        return null;
    }

    @Override
    public PayRollDto add(PayRollModel model) {
        return null;
    }

    @Override
    public List<PayRollDto> add(List<PayRollModel> model) {
        return null;
    }

    @Override
    public PayRollDto update(PayRollModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public Page<PayRollDto> getPayRollByMonthAndYear(int month, int year,Pageable pageable) {
//        return iPayRollRepository.getAllByMonthAndYear(month, year, pageable).map(data -> PayRollDto.entityToDto(data));
        return  null;
    }

    public HashMap<String,Integer> totalWorkingDay(){
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int endMonth = localDate.getMonthValue();
        int endYear = localDate.getYear();
        int startYear = 0;
        int startMonth = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (endMonth == 1) {
            startMonth = 12;
            startYear = endYear - 1;
        } else {
            endMonth = localDate.getMonthValue();
            startMonth = endMonth - 1;
            startYear = localDate.getYear();
        }
        String endDate = timeKeepingDate + 1 + "/" + endMonth + "/" + endYear;

        String startDate = timeKeepingDate + "/" + startMonth + "/" + startYear;
        int workingDays = 0;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            start.setTime(sdf.parse(startDate));
            end.setTime(sdf.parse(endDate));
            workingDays = 0;
            while (!start.after(end)) {
                int day = start.get(Calendar.DAY_OF_WEEK);
                if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
                    workingDays++;
                start.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.put("workingDays", workingDays);
        hashMap.put("startMonth", startMonth);
        hashMap.put("startYear", startYear);
        hashMap.put("endMonth",endMonth);
        hashMap.put("endYear",endYear);


        return hashMap;
    }


    @Override
    public HashMap<String,Object> totalWorkingDayEndWorked(RequestAttendByNameAndYearMonth requestAttendByNameAndYearMonth,Pageable pageable){
        LocalDate currentDate = LocalDate.parse(requestAttendByNameAndYearMonth.getDate().toString());
        int endMonth = currentDate.getMonthValue();
        int endYear = currentDate.getYear();

        int startMonth = 0;
        int startYear = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (endMonth ==1){
            startMonth = 12;
            startYear = endYear - 1;
        }else {
            startMonth = endMonth - 1;
            startYear = endYear;
        }

        String startDate = (timeKeepingDate + 1) + "/" + startMonth + "/" + startYear;

        String endDate = timeKeepingDate + "/" + endMonth + "/" + endYear;

        int workingDays = 0;

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            start.setTime(sdf.parse(startDate));
            end.setTime(sdf.parse(endDate));
            workingDays = 0;
            while (!start.after(end)) {
                int day = start.get(Calendar.DAY_OF_WEEK);
                if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
                    workingDays++;
                start.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalDaysWorked;
        UserEntity userEntity = iUserRepository.findByUserName(requestAttendByNameAndYearMonth.getName());
        try {
            totalDaysWorked = Math.toIntExact(iRequestAttendService.totalDayOfAttendInMonth(userEntity.getUserId(),
                    new SimpleDateFormat("dd/MM/yyyy").parse(startDate),
                    new SimpleDateFormat("dd/MM/yyyy").parse(endDate)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String startDateRequestDayOff = startYear + "/" + startMonth + "/" + timeKeepingDate + " 00:00:00";
        String endDateRequestDayOff = endYear + "/" + endMonth + "/" + (timeKeepingDate + 1) + " 00:00:00";

        int totalPaidLeaveDays = iRequestDayOffService.getTotalDayOffByMonthOfUser(startDateRequestDayOff,endDateRequestDayOff,userEntity.getUserId(),true,1,pageable).size();
        int totalUnpaidLeaveDays = iRequestDayOffService.getTotalDayOffByMonthOfUser(startDateRequestDayOff,endDateRequestDayOff,userEntity.getUserId(),false,1,pageable).size();

        Float totalOvertimeHours = iRequestOTService.totalOTHours(userEntity.getUserId(),1,startYear + "-" + startMonth + "-" + timeKeepingDate,endYear + "-" + endMonth + "-" + (timeKeepingDate + 1));

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("workingDays", workingDays);
        hashMap.put("totalDaysWorked", totalDaysWorked);
        hashMap.put("totalPaidLeaveDays", totalPaidLeaveDays);
        hashMap.put("totalUnpaidLeaveDays", totalUnpaidLeaveDays);
        hashMap.put("totalOvertimeHours", totalOvertimeHours);

        return hashMap;
    }

    @Override
    public Object calculateDate(Pageable pageable) {
        int totalWorkingDay = this.totalWorkingDay().get("workingDays");
        int endMonth = this.totalWorkingDay().get("endMonth");
        int endYear = this.totalWorkingDay().get("endYear");
        int startMonth = this.totalWorkingDay().get("startMonth");
        int startYear = this.totalWorkingDay().get("startYear");

        String startDate = timeKeepingDate + "/" + startMonth + "/" + startYear;
        String endDate = timeKeepingDate + "/" + endMonth + "/" + endYear;

//        int totalDaysWorked;
//        try {
//            totalDaysWorked = Math.toIntExact(iRequestAttendService.totalDayOfAttendInMonth(user_id,
//                    new SimpleDateFormat("dd/MM/yyyy").parse(startDate),
//                    new SimpleDateFormat("dd/MM/yyyy").parse(endDate)));
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        String startDateRequestDayOff = startYear + "/" + startMonth + "/" + timeKeepingDate + " 00:00:00";
//        String endDateRequestDayOff = endYear + "/" + endMonth + "/" + (timeKeepingDate + 1) + " 00:00:00";
//
//        int totalPaidLeaveDays = iRequestDayOffService.getTotalDayOffByMonthOfUser(startDateRequestDayOff,endDateRequestDayOff,user_id,true,1,pageable).size();
//        int totalUnpaidLeaveDays = iRequestDayOffService.getTotalDayOffByMonthOfUser(startDateRequestDayOff,endDateRequestDayOff,user_id,false,2,pageable).size();
//
//        Float totalOvertimeHours = iRequestOTService.totalOTHours(user_id,1,startYear + "-" + startMonth + "-" + timeKeepingDate,endYear + "-" + endMonth + "-" + (timeKeepingDate + 1));

        return null;
    }


}
