package cy.configs.excel;


import cy.dtos.PayRollDto;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PayRollExcelExporter {

    private Workbook workbook;
    private Sheet sheet;

    private final int month;
    private final int year;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFSheet sheet2;

    private List<PayRollDto> payRollDtoList;

    public PayRollExcelExporter(List<PayRollDto> listUsers) {
        this.payRollDtoList = listUsers;
        workbook = new HSSFWorkbook();
    }


    private void writeHeaderLine() {

        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeight(25);
        fontHeader.setFontName("Times New Roman");
        fontHeader.setColor(IndexedColors.GREEN.getIndex());
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);

        sheet = workbook.createSheet("Cham cong_" + month + "_" + year);
        sheet2 = workbook.createSheet("Gio lam them_" + month + "_" + year);
        Row rowHeader = sheet.createRow(0);
        // Cell cell = rowHeader.createCell(1);
        createCell(rowHeader, 0, "Thống kê chấm công tháng " + month + " năm " + year, styleHeader);
        //Merging cells by providing cell index
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 9));


        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 16);
        style.setFont(font);


        createCell(row, 0, "STT", style);
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
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }
        else if (value instanceof Float) {
            cell.setCellValue((Float) value);
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

//blue color
        CellStyle styleBlue = workbook.createCellStyle();
        font.setFontHeight(12);
        styleBlue.setFont(font);
        styleBlue.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        styleBlue.setFillPattern(FillPatternType.DIAMONDS);
//red color
        CellStyle styleRed = workbook.createCellStyle();
        font.setFontHeight(12);
        styleRed.setFont(font);
        styleRed.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        styleRed.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
//green color
        CellStyle styleGreen = workbook.createCellStyle();
        font.setFontHeight(12);
        styleGreen.setFont(font);
        styleGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        styleGreen.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
//yellow color
        CellStyle styleYellow = workbook.createCellStyle();
        styleYellow.setAlignment(HorizontalAlignment.RIGHT);
        font.setFontHeight(12);
        styleYellow.setFont(font);
        styleYellow.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleYellow.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
        int stt=1;
>>>>>>> Stashed changes
        for (PayRollDto user : payRollDtoList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            if (user.getTotalOvertimeHours() != null) {
                user.setTotalOvertimeHours(user.getTotalOvertimeHours());
            } else {
                user.setTotalOvertimeHours(Float.valueOf(0));
            }

            createCell(row, columnCount++, stt++, style);
            createCell(row, columnCount++, user.getNameStaff(), style);
            createCell(row, columnCount++, user.getMonthWorking(), style);
            createCell(row, columnCount++, user.getTotalWorkingDay(), style);
            createCell(row, columnCount++, user.getTotalOvertimeHours() != null ? user.getTotalOvertimeHours().toString() : 0, style);
            createCell(row, columnCount++, user.getTotalDaysWorked(), style);
            createCell(row, columnCount++, user.getTotalPaidLeaveDays(), style);
            createCell(row, columnCount++, user.getTotalUnpaidLeaveDays(), style);
            createCell(row, columnCount++, user.getTotalDaysWorked() + user.getTotalPaidLeaveDays(), style);
            createCell(row, columnCount++, (user.getTotalDaysWorked() + user.getTotalPaidLeaveDays()) * 8 + user.getTotalOvertimeHours(), style);

        }
<<<<<<< Updated upstream
=======
       /* UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        Row rowHeader = sheet.createRow(rowCount);
        createCell(rowHeader, 0, "Nhân viên xuất file : "+userEntity.getFullName()+" ngày xuất: "+currentDateTime, style);
        sheet.addMergedRegion(new CellRangeAddress(rowCount,rowCount+3 , 0, 3));*/

>>>>>>> Stashed changes
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
