package com.tcts.controller;

import com.tcts.common.PrettyPrintingDate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tcts.datamodel.Bank;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.Volunteer;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ExcelDownloadController implements InitializingBean {

    @Autowired
    private BankController bankController;

    @Autowired
    private AdminEditController adminEditController;

    @Autowired
    private SchoolController schoolController;

    @Autowired
    private VolunteerController volunteerController;

    @Autowired
    private TeacherController teacherController;

    @Autowired
    private HomePageController homePageController;

    @Autowired
    private EventController eventController;

    @Autowired
    private BankAdminActionsController bankAdminActionsController;


    private Map workbookSpecs;

    private final ThreadLocal<HttpSession> threadLocalSession = new ThreadLocal<HttpSession>();



    @RequestMapping(value="/excel/{locationSpecName}", method=RequestMethod.GET)
    public void excelDownload(HttpServletResponse servletResponse, HttpSession session, @PathVariable("locationSpecName") String locationSpecName) throws SQLException, IOException {
        try {
            this.threadLocalSession.set(session);
            ExcelDownloadController.WorkbookSpec workbookSpec = (ExcelDownloadController.WorkbookSpec)this.workbookSpecs.get(locationSpecName);
            ExtendedModelMap mockModel = new ExtendedModelMap();
            String pageWeWouldRender = ((ExcelDownloadController.CM_SM)workbookSpec.controllerMethod).invoke(session, mockModel);

            assert pageWeWouldRender.equals(workbookSpec.expectedJSPPage);

            this.streamWorkbook(servletResponse, workbookSpec, mockModel);
        } finally {
            this.threadLocalSession.remove();
        }

    }

   @RequestMapping(value="/excel/{locationSpecName}/{parameter}", method=RequestMethod.GET)
   public void excelDownload(HttpServletResponse servletResponse, HttpSession session, @PathVariable("locationSpecName") String locationSpecName, @PathVariable("parameter") String parameter) throws SQLException, IOException {
      ExcelDownloadController.WorkbookSpec workbookSpec = (ExcelDownloadController.WorkbookSpec)this.workbookSpecs.get(locationSpecName);
      ExtendedModelMap mockModel = new ExtendedModelMap();
      String pageWeWouldRender = ((ExcelDownloadController.CM_PSM)workbookSpec.controllerMethod).invoke(parameter, session, mockModel);

      assert pageWeWouldRender.equals(workbookSpec.expectedJSPPage);

      this.streamWorkbook(servletResponse, workbookSpec, mockModel);
   }


    private void streamWorkbook(
            HttpServletResponse servletResponse,
            ExcelDownloadController.WorkbookSpec workbookSpec,
            Model model
    ) throws IOException {
        servletResponse.setContentType("application/vnd.ms-excel");
        servletResponse.setHeader("Content-Disposition", "attachment; filename=" + workbookSpec.filename + ".xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sheet1");
        int rowNum = this.makeLabels(workbookSpec, workbook, sheet);
        List items = workbookSpec.getItems(model);
        this.populateValues(workbookSpec, sheet, items, rowNum);
        this.autoSizeColumns(workbookSpec, sheet);
        ServletOutputStream servletOutputStream = servletResponse.getOutputStream();
        workbook.write(servletOutputStream);
        servletOutputStream.close();
    }

    private static void mergeCells(Sheet sheet, int rowNum, int colNum, int rows, int cols) {
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + rows - 1, colNum, colNum + cols - 1));
    }

    private int makeLabels(ExcelDownloadController.WorkbookSpec workbookSpec, HSSFWorkbook workbook, HSSFSheet sheet) {
        byte rowNum = 0;
        HSSFFont labelFont = workbook.createFont();
        labelFont.setBoldweight((short)700);
        HSSFCellStyle labelStyle = workbook.createCellStyle();
        labelStyle.setFont(labelFont);
        boolean needsSubLabels = false;
        HSSFRow row = sheet.createRow(rowNum);
        int colNum = 0;

        Iterator var10;
        ExcelDownloadController.ColSpec colSpec;
        for(var10 = workbookSpec.colSpecs.iterator(); var10.hasNext(); colNum += colSpec.getNumColumns()) {
            colSpec = (ExcelDownloadController.ColSpec)var10.next();
            Cell multiColSpec = row.createCell(colNum);
            multiColSpec.setCellValue(colSpec.label);
            multiColSpec.setCellStyle(labelStyle);
            if(colSpec instanceof ExcelDownloadController.MultiColSpec) {
                needsSubLabels = true;
                mergeCells(sheet, rowNum, colNum, 1, colSpec.getNumColumns());
            }
        }

        int var16 = rowNum + 1;
        if(needsSubLabels) {
            row = sheet.createRow(var16);
            colNum = 0;
            var10 = workbookSpec.colSpecs.iterator();

            while(var10.hasNext()) {
                colSpec = (ExcelDownloadController.ColSpec)var10.next();
                if(colSpec instanceof ExcelDownloadController.MultiColSpec) {
                    ExcelDownloadController.MultiColSpec var17 = (ExcelDownloadController.MultiColSpec)colSpec;

                    for(Iterator var13 = var17.getInnerColSpecs().iterator(); var13.hasNext(); ++colNum) {
                        ExcelDownloadController.ColSpec innerColSpec = (ExcelDownloadController.ColSpec)var13.next();
                        Cell cell = row.createCell(colNum);
                        cell.setCellValue(innerColSpec.label);
                        cell.setCellStyle(labelStyle);
                    }
                } else {
                    mergeCells(sheet, 0, colNum, 2, 1);
                    ++colNum;
                }
            }

            ++var16;
        }

        return var16;
    }

    private void populateValues(ExcelDownloadController.WorkbookSpec workbookSpec, HSSFSheet sheet, List items, int rowNum) {
        int var16;
        for(Iterator var5 = items.iterator(); var5.hasNext(); rowNum += var16) {
            Object item = var5.next();
            HSSFRow row = sheet.createRow(rowNum);
            HashMap rowsWrittenPerColSpec = new HashMap();
            int colNum = 0;

            ExcelDownloadController.ColSpec colSpec;
            int colSpec1;
            for(Iterator mostRowsInASubtable = workbookSpec.colSpecs.iterator(); mostRowsInASubtable.hasNext(); rowsWrittenPerColSpec.put(colSpec, Integer.valueOf(colSpec1))) {
                colSpec = (ExcelDownloadController.ColSpec)mostRowsInASubtable.next();
                if(colSpec instanceof ExcelDownloadController.MultiColSpec) {
                    ExcelDownloadController.MultiColSpec rowsAlreadyWritten = (ExcelDownloadController.MultiColSpec)colSpec;
                    int[] numColumns = rowsAlreadyWritten.recordSubtableInSheet(sheet, row, rowNum, colNum, item);
                    colSpec1 = numColumns[0];
                    colNum += numColumns[1];
                } else {
                    Cell var19 = row.createCell(colNum);
                    colSpec.setCellValue(var19, item);
                    colSpec1 = 1;
                    ++colNum;
                }
            }

            var16 = ((Integer)Collections.max(rowsWrittenPerColSpec.values())).intValue();
            if(var16 > 1) {
                colNum = 0;
                Iterator var17 = workbookSpec.colSpecs.iterator();

                while(var17.hasNext()) {
                    ExcelDownloadController.ColSpec var18 = (ExcelDownloadController.ColSpec)var17.next();
                    int var20 = ((Integer)rowsWrittenPerColSpec.get(var18)).intValue();
                    int var21 = var18.getNumColumns();

                    for(int i = 0; i < var21; ++i) {
                        mergeCells(sheet, rowNum + var20 - 1, colNum, var16 - var20 + 1, 1);
                        ++colNum;
                    }
                }
            }
        }

    }

    private void autoSizeColumns(ExcelDownloadController.WorkbookSpec workbookSpec, HSSFSheet sheet) {
        int numberOfColumns = 0;

        ExcelDownloadController.ColSpec colSpec;
        for(Iterator colNum = workbookSpec.colSpecs.iterator(); colNum.hasNext(); numberOfColumns += colSpec.getNumColumns()) {
            colSpec = (ExcelDownloadController.ColSpec)colNum.next();
        }

        for(int var6 = 0; var6 < numberOfColumns; ++var6) {
            sheet.autoSizeColumn(var6);
        }

    }


    private static class ColSpec<T> {

        public interface Extractor<S,T> {
            T extract(S x) throws NullPointerException;
        }

        public final String label;
        protected final Extractor extractor;


        public ColSpec(String label, Extractor<T,Object> extractor) {
            this.label = label;
            this.extractor = extractor;
        }

        public void setCellValue(Cell cell, T item) {
            Object value;
            try {
                value = extractor.extract(item);
            } catch (NullPointerException var5) {
                return;
            }
            if (value == null) {
                return;
            }
            if(value instanceof String) {
                cell.setCellValue((String)value);
            } else if (value instanceof Number) {
                cell.setCellType(0);
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof BigDecimal) {
                cell.setCellType(0);
                cell.setCellValue(((BigDecimal) value).doubleValue());
            } else {
                throw new RuntimeException("Return type " + value.getClass().getName() + " is not supported for extractors.");
            }
        }

        public int getNumColumns() {
            return 1;
        }

    }

    public interface CM_PSM {
        String invoke(String string, HttpSession session, Model model) throws SQLException;
    }

    public interface CM_SM {
        String invoke(HttpSession session, Model model) throws SQLException;
    }

    private static class MultiColSpec extends ColSpec {

        private final String noDataPlaceholder;
        private final List innerColSpecs;
        // $FF: synthetic field
        static final boolean $assertionsDisabled = !ExcelDownloadController.class.desiredAssertionStatus();


        public MultiColSpec(
                String label,
                MultiExtractor extractor,
                String noDataPlaceholder,
                ExcelDownloadController.ColSpec ...innerColSpecs
        ) {
            super(label, extractor);
            if(!$assertionsDisabled && innerColSpecs.length < 1) {
                throw new AssertionError();
            } else {
                this.noDataPlaceholder = noDataPlaceholder;
                this.innerColSpecs = Arrays.asList(innerColSpecs);
            }
        }

        public int getNumColumns() {
            return this.innerColSpecs.size();
        }

        public List getInnerColSpecs() {
            return this.innerColSpecs;
        }

        public int[] recordSubtableInSheet(Sheet sheet, Row firstRow, int rowNum, int colNum, Object item) {
            List subtableItems;
            try {
                subtableItems = (List)this.extractor.extract(item);
            } catch (NullPointerException var15) {
                return new int[]{1, 1};
            }

            if(subtableItems.size() == 0) {
                Cell var16 = firstRow.createCell(colNum);
                var16.setCellValue(this.noDataPlaceholder);
                ExcelDownloadController.mergeCells(sheet, rowNum, colNum, 1, this.getNumColumns());
                return new int[]{1, this.getNumColumns()};
            } else {
                int innerRowNum = rowNum;

                for(Iterator var8 = subtableItems.iterator(); var8.hasNext(); ++innerRowNum) {
                    Object subtableItem = var8.next();
                    int innerColNum = colNum;
                    Row row = innerRowNum == rowNum?firstRow:sheet.createRow(innerRowNum);

                    for(Iterator var12 = this.innerColSpecs.iterator(); var12.hasNext(); ++innerColNum) {
                        ExcelDownloadController.ColSpec innerColSpec = (ExcelDownloadController.ColSpec)var12.next();
                        if(!$assertionsDisabled && innerColSpec instanceof ExcelDownloadController.MultiColSpec) {
                            throw new AssertionError();
                        }

                        Cell cell = row.createCell(innerColNum);
                        innerColSpec.setCellValue(cell, subtableItem);
                    }
                }

                return new int[]{innerRowNum - rowNum, this.getNumColumns()};
            }
        }

    }


    public interface MultiExtractor<S,T> extends ColSpec.Extractor<S,List<T>> {
        List<T> extract(S x) throws NullPointerException;
    }


    private class VolunteerEventsExtractor implements MultiExtractor<Volunteer,Event> {
        public List<Event> extract(Volunteer v) throws NullPointerException {
            HttpSession session = (HttpSession) threadLocalSession.get();
            ExtendedModelMap mockModel = new ExtendedModelMap();
            String volunteerId = v.getUserId();

            try {
                String pageToShow = bankAdminActionsController.detailCoursesForAVolunteer(session, mockModel, volunteerId);
                assert pageToShow.equals("bankAdminHomeDetail");
                return (List<Event>)mockModel.asMap().get("events");
            } catch (SQLException err) {
                return Collections.emptyList();
            }
        }
    }

    private static class WorkbookSpec<T> {

        public final String expectedJSPPage;
        public final String keyInModel;
        public final T controllerMethod;
        private final String filename;
        private final List colSpecs;


        public WorkbookSpec(
            String expectedJSPPage,
            String keyInModel,
            T controllerMethod,
            String filename,
            ColSpec ...colSpecs)
        {
            this.expectedJSPPage = expectedJSPPage;
            this.keyInModel = keyInModel;
            this.controllerMethod = controllerMethod;
            this.filename = filename;
            this.colSpecs = Arrays.asList(colSpecs);
        }

        public List getItems(Model model) {
           return (List)model.asMap().get(this.keyInModel);
        }

    }



    public void afterPropertiesSet() {
        this.workbookSpecs = new HashMap<String,WorkbookSpec>() {{

            this.put("banks", new WorkbookSpec<CM_SM>(
                    "banks",
                    "banks",
                    bankController::showBanks,
                    "Banks",
                    new ColSpec<Bank>("Bank Name", b -> b.getBankName()),
                    new ColSpec<Bank>("Bank Admin Name", b -> b.getLinkedBankAdmin().getFirstName() + " " + b.getLinkedBankAdmin().getLastName()),
                    new ColSpec<Bank>("Bank Admin Email", b -> b.getLinkedBankAdmin().getEmail()),
                    new ColSpec<Bank>("Bank Admin Phone", b -> b.getLinkedBankAdmin().getPhoneNumber())
            ));

            this.put("allowedDates", new WorkbookSpec<CM_SM>(
                    "listAllowedDates",
                    "allowedDates",
                    adminEditController::listAllowedDates,
                    "AllowedDates",
                    new ColSpec<PrettyPrintingDate>("Date", d -> d.getPretty())
            ));

            this.put("allowedTimes", new WorkbookSpec<CM_SM>(
                    "listAllowedTimes",
                    "allowedTimes",
                    adminEditController::listAllowedTimes,
                    "AllowedTimes",
                    new ColSpec<String>("Time", x -> x)
            ));

            this.put("schools", new WorkbookSpec<CM_SM>(
                    "schools",
                    "schools",
                    schoolController::viewSchools,
                    "Schools",
                    new ColSpec<School>("Name", x -> x.getName()),
                    new ColSpec<School>("Address", x -> x.getAddressLine1()),
                    new ColSpec<School>("City", x -> x.getCity()),
                    new ColSpec<School>("State", x -> x.getState()),
                    new ColSpec<School>("County", x -> x.getZip()),
                    new ColSpec<School>("District", x -> x.getSchoolDistrict()),
                    new ColSpec<School>("Phone", x -> x.getPhone()),
                    new ColSpec<School>("LMI Eligible", x -> x.getLmiEligible()),
                    new ColSpec<School>("SLC", x -> x.getSLC())
            ));

            this.put("volunteers", new WorkbookSpec<CM_SM>(
                    "volunteers",
                    "volunteers",
                    volunteerController::showVolunteersList,
                    "Volunteers",
                    new ColSpec<Volunteer>("User ID", x -> x.getUserId()),
                    new ColSpec<Volunteer>("Email", x -> x.getEmail()),
                    new ColSpec<Volunteer>("First Name", x -> x.getFirstName()),
                    new ColSpec<Volunteer>("Last Name", x -> x.getLastName()),
                    new ColSpec<Volunteer>("User Type", x -> x.getUserType().getDisplayName()),
                    new ColSpec<Volunteer>("Phone Number", x -> x.getPhoneNumber()),
                    new ColSpec<Volunteer>("Bank Name", x -> x.getLinkedBank().getBankName()),
                    new ColSpec<Volunteer>("Bank Specific Data", x -> x.getBankSpecificData()),
                    new ColSpec<Volunteer>("Suite/Floor Number", x -> x.getSuiteOrFloorNumber()),
                    new ColSpec<Volunteer>("Street Address", x -> x.getStreetAddress()),
                    new ColSpec<Volunteer>("City", x -> x.getCity()),
                    new ColSpec<Volunteer>("State", x -> x.getState()),
                    new ColSpec<Volunteer>("Zip Code", x -> x.getZip())
            ));

            this.put("bankVolunteers", new WorkbookSpec<CM_SM>(
                    "bankAdminHome",
                    "normalVolunteers",
                    homePageController::showBankAdminHomePage,
                    "Volunteers",
                    new ColSpec<Volunteer>("First Name", x -> x.getFirstName()),
                    new ColSpec<Volunteer>("Last Name", x -> x.getLastName()),
                    new ColSpec<Volunteer>("Email", x -> x.getEmail()),
                    new MultiColSpec(
                            "Classes Registered",
                            new VolunteerEventsExtractor(),
                            "Not volunteered yet.",
                            new ColSpec<Event>("Date", x -> x.getEventDate().getPretty()),
                            new ColSpec<Event>("Time", x -> x.getEventTime()),
                            new ColSpec<Event>("School", x -> x.getLinkedTeacher().getLinkedSchool().getName()),
                            new ColSpec<Event>("Grade", x -> x.getGrade()),
                            new ColSpec<Event>("Presence", x -> x.getPresenceString()),
                            new ColSpec<Event>("Students", x -> x.getNumberStudents()),
                            new ColSpec<Event>("Teacher", x -> x.getLinkedTeacher().getFirstName()+" "+x.getLinkedTeacher().getLastName()),
                            new ColSpec<Event>("Teacher Email", x -> x.getLinkedTeacher().getFirstName()+" "+x.getLinkedTeacher().getEmail())

                            )
            ));

            this.put("deletedBankVolunteers", new WorkbookSpec<CM_PSM>(
                    "deleteBank",
                    "volunteers",
                    bankController::deleteBankConfirm,
                    "Volunteers",
                    new ColSpec<Volunteer>("User ID", x -> x.getUserId()),
                    new ColSpec<Volunteer>("Email", x -> x.getEmail()),
                    new ColSpec<Volunteer>("First Name", x -> x.getFirstName()),
                    new ColSpec<Volunteer>("Last Name", x -> x.getLastName()),
                    new ColSpec<Volunteer>("User Type", x -> x.getUserType().getDisplayName()),
                    new ColSpec<Volunteer>("Phone Number", x -> x.getPhoneNumber()),
                    new ColSpec<Volunteer>("Bank Specific Data", x -> x.getBankSpecificData()),
                    new ColSpec<Volunteer>("Suite/Floor Number", x -> x.getSuiteOrFloorNumber()),
                    new ColSpec<Volunteer>("Street Address", x -> x.getStreetAddress()),
                    new ColSpec<Volunteer>("City", x -> x.getCity()),
                    new ColSpec<Volunteer>("State", x -> x.getState()),
                    new ColSpec<Volunteer>("Zip Code", x -> x.getZip())
            ));

            this.put("teachers", new WorkbookSpec<CM_SM>(
                    "teachers",
                    "teachers",
                    teacherController::showTeachersList,
                    "Teachers",
                    new ColSpec<Teacher>("User ID", x -> x.getUserId()),
                    new ColSpec<Teacher>("Email", x -> x.getEmail()),
                    new ColSpec<Teacher>("First Name", x -> x.getFirstName()),
                    new ColSpec<Teacher>("Last Name", x -> x.getLastName()),
                    new ColSpec<Teacher>("User Type", x -> x.getUserType().getDisplayName()),
                    new ColSpec<Teacher>("Phone Number", x -> x.getPhoneNumber()),
                    new ColSpec<Teacher>("SLC", x -> x.getLinkedSchool().getSLC()),
                    new ColSpec<Teacher>("School Name", x -> x.getLinkedSchool().getName()),
                    new ColSpec<Teacher>("District Name", x -> x.getLinkedSchool().getSchoolDistrict())
            ));

            this.put("deletedSchoolTeachers", new WorkbookSpec<CM_PSM>(
                    "deleteSchool",
                    "teachers",
                    schoolController::deleteSchoolConfirm,
                    "Teachers",
                    new ColSpec<Teacher>("User ID", x -> x.getUserId()),
                    new ColSpec<Teacher>("Email", x -> x.getEmail()),
                    new ColSpec<Teacher>("First Name", x -> x.getFirstName()),
                    new ColSpec<Teacher>("Last Name", x -> x.getLastName()),
                    new ColSpec<Teacher>("User Type", x -> x.getUserType().getDisplayName()),
                    new ColSpec<Teacher>("Phone Number", x -> x.getPhoneNumber())
            ));

            this.put("events", new WorkbookSpec<CM_SM>(
                    "events",
                    "events",
                    eventController::showAllEvents,
                    "Classes",
                    new ColSpec<Event>("Date", x -> x.getEventDate().getPretty()),
                    new ColSpec<Event>("Time", x -> x.getEventTime()),
                    new ColSpec<Event>("Grade", x -> x.getGrade()),
                    new ColSpec<Event>("Presence", x -> x.getPresenceString()),
                    new ColSpec<Event>("Students", x -> x.getNumberStudents()),
                    new ColSpec<Event>("Teacher", x -> x.getLinkedTeacher().getFirstName() + " " + x.getLinkedTeacher().getLastName()),
                    new ColSpec<Event>("Teacher Email", x -> x.getLinkedTeacher().getEmail()),
                    new ColSpec<Event>("School", x -> x.getLinkedTeacher().getLinkedSchool().getName()),
                    new ColSpec<Event>("Volunteer", x -> x.getLinkedVolunteer() == null ? "None yet." : x.getLinkedVolunteer().getFirstName() + " " + x.getLinkedVolunteer().getLastName()),
                    new ColSpec<Event>("Volunteer Email", x -> x.getLinkedVolunteer().getEmail()),
                    new ColSpec<Event>("Bank", x -> x.getLinkedVolunteer().getLinkedBank().getBankName())
            ));
        }};
    }

}
