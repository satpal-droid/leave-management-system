package com.lms.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
 
import javax.servlet.http.HttpServletResponse;

import com.lms.models.LeaveDetails;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
 
 
public class UserPDFExporter {
    private List<LeaveDetails> leaveDetails;
     
    public UserPDFExporter(List<LeaveDetails> leaveDetails) {
        this.leaveDetails = leaveDetails;
    }
 
    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);
         
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
         
        cell.setPhrase(new Phrase(" FROM DATE", font));
         
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("	TO DATE", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("NO OF DAYS", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("	LEAVE TYPE", font));
        table.addCell(cell);
         
        cell.setPhrase(new Phrase("REASON", font));
        table.addCell(cell);  

        cell.setPhrase(new Phrase("STATUS", font));
        table.addCell(cell);     
    }
     
    private void writeTableData(PdfPTable table) {
        for (LeaveDetails leaveDetails : leaveDetails) {
            table.addCell(String.valueOf(leaveDetails.getFromDate()));
            table.addCell(String.valueOf(leaveDetails.getToDate()));
            table.addCell(String.valueOf(leaveDetails.getDuration()));
            table.addCell(leaveDetails.getLeaveType());
            table.addCell(leaveDetails.getReason());   
            table.addCell(String.valueOf(leaveDetails.getStatus()));
        }
    }
     
    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
         
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
         
        Paragraph p = new Paragraph("List of Users", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
         
        document.add(p);
         
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {3.5f, 3.5f, 3.0f, 3.0f, 3.5f,3.5f});
        table.setSpacingBefore(10);
         
        writeTableHeader(table);
        writeTableData(table);
         
        document.add(table);
         
        document.close();
         
    }
}

