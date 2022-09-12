package cy.configs.excel;


import cy.dtos.PayRollDto;
import cy.entities.UserEntity;
import cy.utils.SecurityUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PayRollExcelExporter {
    private final int month;
    private final int year;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<PayRollDto> payRollDtoList;

    public PayRollExcelExporter(List<PayRollDto> listUsers, int month, int year) {
        this.payRollDtoList = listUsers;
        workbook = new XSSFWorkbook();
        this.month = month;
        this.year = year;
    }

    private void writeHeaderLine() {
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

        Row rowHeader = sheet.createRow(0);
        // Cell cell = rowHeader.createCell(1);
        createCell(rowHeader, 0, "Thống kê chấm công tháng " + month + " năm " + year, styleHeader);
        //Merging cells by providing cell index
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 9));

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        Row row = sheet.createRow(3);

        createCell(row, 0, "Mã NV", style);
        createCell(row, 1, "Tên nhân viên", style);
        createCell(row, 2, "Tháng làm", style);
        createCell(row, 3, "Tổng số ngày làm trong tháng", style);


        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        createCell(row, 4, "Tổng số ngày chấm công", style);
        createCell(row, 5, "Tổng số ngày nghỉ có lương", style);

        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        createCell(row, 6, "Tổng số ngày nghỉ không lương", style);
        // Setting Foreground Color
        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        createCell(row, 7, "Tổng số ngày lương", style);
        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        createCell(row, 8, "Tổng số giờ làm thêm", style);
        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(row, 9, "Tổng số giờ làm có lương", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 4;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
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
        for (PayRollDto user : payRollDtoList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            if (user.getTotalOvertimeHours() != null) {
                user.setTotalOvertimeHours(user.getTotalOvertimeHours());
            } else {
                user.setTotalOvertimeHours(Float.valueOf(0));
            }
            createCell(row, columnCount++, user.getId().toString(), style);
            createCell(row, columnCount++, user.getNameStaff(), style);
            createCell(row, columnCount++, user.getMonthWorking(), style);
            createCell(row, columnCount++, user.getTotalWorkingDay(), style);

            createCell(row, columnCount++, user.getTotalDaysWorked(), style);
            createCell(row, columnCount++, user.getTotalPaidLeaveDays(), style);
            createCell(row, columnCount++, user.getTotalUnpaidLeaveDays(), styleRed);
            createCell(row, columnCount++, user.getTotalDaysWorked() + user.getTotalPaidLeaveDays(), styleBlue);
            createCell(row, columnCount++, user.getTotalOvertimeHours() != null ? user.getTotalOvertimeHours().toString() : 0, styleYellow);
            createCell(row, columnCount++, (user.getTotalDaysWorked() + user.getTotalPaidLeaveDays()) * 8 + user.getTotalOvertimeHours(), styleGreen);

        }
        //show user export
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        Row rowHeader = sheet.createRow(rowCount);
        createCell(rowHeader, 0, "Nhân viên xuất file : "+userEntity.getFullName()+" ngày xuất: "+currentDateTime, style);
        sheet.addMergedRegion(new CellRangeAddress(rowCount,rowCount+3 , 0, 3));

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
