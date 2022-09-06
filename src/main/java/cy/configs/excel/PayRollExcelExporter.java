package cy.configs.excel;


import cy.dtos.PayRollDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.datatransfer.FlavorEvent;
import java.io.IOException;
import java.util.List;

public class PayRollExcelExporter {
    private Workbook workbook;
    private Sheet sheet;
   private List<PayRollDto> payRollDtoList;

    public PayRollExcelExporter(List<PayRollDto> listUsers) {
        this.payRollDtoList = listUsers;
        workbook = new HSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 16);
        style.setFont(font);

        createCell(row, 0, "Id", style);
        createCell(row, 1, "Tên nhân viên", style);
        createCell(row, 2, "Tháng làm", style);
        createCell(row, 3, "Tổng số ngày làm trong tháng", style);
        createCell(row, 4, "Tổng số giờ làm thêm", style);
        createCell(row, 5, "Tổng số ngày chấm công", style);
        createCell(row, 6, "Tổng số ngày nghỉ có lương", style);
        createCell(row, 7, "Tổng số ngày nghi không lương", style);
        createCell(row, 8, "Tổng số ngày có lương", style);
        createCell(row, 9, "Tổng số giờ làm có lương", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }
        else if (value instanceof Float) {
            cell.setCellValue(String.valueOf(value));
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short) 14);
        style.setFont(font);

      for (PayRollDto user : payRollDtoList) {
          Row row = sheet.createRow(rowCount++);
          int columnCount = 0;
if(user.getTotalOvertimeHours() != null){
    user.setTotalOvertimeHours(user.getTotalOvertimeHours());
}else {
    user.setTotalOvertimeHours(Float.valueOf(0));
}
          createCell(row, columnCount++, user.getId(), style);
          createCell(row, columnCount++, user.getNameStaff(), style);
          createCell(row, columnCount++, user.getMonthWorking(), style);
          createCell(row, columnCount++, user.getTotalWorkingDay(), style);
          createCell(row, columnCount++, user.getTotalOvertimeHours()!=null ? user.getTotalOvertimeHours().toString():0, style);
          createCell(row, columnCount++, user.getTotalDaysWorked(), style);
          createCell(row, columnCount++, user.getTotalPaidLeaveDays(), style);
          createCell(row, columnCount++, user.getTotalUnpaidLeaveDays(), style);
          createCell(row, columnCount++, user.getTotalDaysWorked()+user.getTotalPaidLeaveDays(), style);
          createCell(row, columnCount++, (user.getTotalDaysWorked()+user.getTotalPaidLeaveDays())*8+user.getTotalOvertimeHours(), style);

      }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
