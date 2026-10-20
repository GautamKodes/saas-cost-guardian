// package com.saasguardian.decision;

// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.pdmodel.PDPage;
// import org.apache.pdfbox.pdmodel.PDPageContentStream;
// import org.apache.pdfbox.pdmodel.common.PDRectangle;
// import org.apache.pdfbox.pdmodel.font.PDType1Font;
// import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;

// import jakarta.mail.internet.MimeMessage;

// import java.io.ByteArrayOutputStream;

// @Service
// public class EmailService {

//     private final JavaMailSender mailSender;

//     @Value("${app.alert.email.from}")
//     private String senderEmail;

//     @Value("${app.alert.email.to}")
//     private String recipientEmail;

//     public EmailService(JavaMailSender mailSender) {
//         this.mailSender = mailSender;
//     }

//     // ============================================================
//     // ANOMALY ALERT EMAIL
//     // ============================================================

//     public void sendAnomalyAlert(
//             int calls,
//             double baseline,
//             double score) {

//         SimpleMailMessage message = new SimpleMailMessage();

//         message.setFrom(senderEmail);
//         message.setTo(recipientEmail);

//         message.setSubject("⚠️ APEye - Anomaly Detected");

//         message.setText(
//                 "APEye detected unusual API usage.\n\n" +
//                 "Current API calls: " + calls + "\n" +
//                 "Previous baseline: " + baseline + "\n" +
//                 "Anomaly score: " + score + "\n\n" +
//                 "Please check your APEye dashboard for more details."
//         );

//         mailSender.send(message);
//     }


//     // ============================================================
//     // SESSION REPORT EMAIL + PDF ATTACHMENT
//     // ============================================================

//     public void sendSessionReport(
//             DecisionController.SessionReportRequest report) {

//         try {

//             // ----------------------------------------------------
//             // 1. Generate PDF
//             // ----------------------------------------------------

//             byte[] pdfBytes = generateSessionReportPdf(report);


//             // ----------------------------------------------------
//             // 2. Create email
//             // ----------------------------------------------------

//             MimeMessage message = mailSender.createMimeMessage();

//             MimeMessageHelper helper =
//                     new MimeMessageHelper(
//                             message,
//                             true,
//                             "UTF-8"
//                     );

//             helper.setFrom(senderEmail);
//             helper.setTo(recipientEmail);

//             helper.setSubject(
//                     "APEye - Session Summary Report"
//             );


//             // ----------------------------------------------------
//             // 3. Email body
//             // ----------------------------------------------------

//             String emailBody = """
//                     <html>
//                     <body style="font-family:Arial,Helvetica,sans-serif;">

//                         <h2>APEye — Session Summary Report</h2>

//                         <p>
//                             Your monitoring session has been completed.
//                         </p>

//                         <p>
//                             The complete session report is attached
//                             as a PDF.
//                         </p>

//                         <hr>

//                         <p>
//                             <b>Session Duration:</b> %s<br>
//                             <b>Total Requests:</b> %,d<br>
//                             <b>Estimated Spend:</b> $%.3f<br>
//                             <b>Anomalies Caught:</b> %d<br>
//                             <b>Peak Risk Level:</b> %s<br>
//                             <b>Peak Anomaly Score:</b> %.2f
//                         </p>

//                         <p>
//                             <b>Summary:</b><br>
//                             %s
//                         </p>

//                         <hr>

//                         <small>
//                             This report was generated automatically by APEye.
//                         </small>

//                     </body>
//                     </html>
//                     """.formatted(
//                             report.duration(),
//                             report.totalRequests(),
//                             report.estimatedSpend(),
//                             report.anomaliesCaught(),
//                             report.peakRiskLevel(),
//                             report.peakAnomalyScore(),
//                             report.summary()
//                     );


//             helper.setText(emailBody, true);


//             // ----------------------------------------------------
//             // 4. Attach PDF
//             // ----------------------------------------------------

//             helper.addAttachment(
//                     "APEye_Session_Report.pdf",
//                     () -> new java.io.ByteArrayInputStream(pdfBytes)
//             );


//             // ----------------------------------------------------
//             // 5. Send
//             // ----------------------------------------------------

//             mailSender.send(message);

//             System.out.println(
//                     "SESSION REPORT EMAIL + PDF SENT SUCCESSFULLY"
//             );

//         } catch (Exception e) {

//             e.printStackTrace();

//             throw new RuntimeException(
//                     "Failed to generate and send session report",
//                     e
//             );
//         }
//     }


//     // ============================================================
//     // PDF GENERATION
//     // ============================================================

//     private byte[] generateSessionReportPdf(
//             DecisionController.SessionReportRequest report)
//             throws Exception {

//         ByteArrayOutputStream outputStream =
//                 new ByteArrayOutputStream();

//         try (PDDocument document = new PDDocument()) {

//             PDPage page =
//                     new PDPage(PDRectangle.A4);

//             document.addPage(page);

//             try (PDPageContentStream content =
//                          new PDPageContentStream(
//                                  document,
//                                  page)) {

//                 float pageWidth =
//                         page.getMediaBox().getWidth();

//                 float margin = 50;

//                 float y = 760;


//                 // ------------------------------------------------
//                 // TITLE
//                 // ------------------------------------------------

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA_BOLD
//                         ),
//                         22
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         y
//                 );

//                 content.showText(
//                         "APEye"
//                 );

//                 content.endText();


//                 // ------------------------------------------------
//                 // SUBTITLE
//                 // ------------------------------------------------

//                 y -= 35;

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA_BOLD
//                         ),
//                         16
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         y
//                 );

//                 content.showText(
//                         "SESSION SUMMARY REPORT"
//                 );

//                 content.endText();


//                 // ------------------------------------------------
//                 // LINE
//                 // ------------------------------------------------

//                 y -= 20;

//                 content.moveTo(
//                         margin,
//                         y
//                 );

//                 content.lineTo(
//                         pageWidth - margin,
//                         y
//                 );

//                 content.stroke();


//                 // ------------------------------------------------
//                 // STATUS
//                 // ------------------------------------------------

//                 y -= 40;

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA_BOLD
//                         ),
//                         14
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         y
//                 );

//                 if (report.anomaliesCaught() > 0) {

//                     content.showText(
//                             "BILL-SHOCK PROTECTION REPORT"
//                     );

//                 } else {

//                     content.showText(
//                             "COST PROTECTION NORMAL"
//                     );
//                 }

//                 content.endText();


//                 // ------------------------------------------------
//                 // SESSION DETAILS
//                 // ------------------------------------------------

//                 y -= 50;

//                 y = addPdfRow(
//                         content,
//                         "Session Duration",
//                         report.duration(),
//                         y,
//                         margin
//                 );

//                 y = addPdfRow(
//                         content,
//                         "Total Requests",
//                         String.format(
//                                 "%,d",
//                                 report.totalRequests()
//                         ),
//                         y,
//                         margin
//                 );

//                 y = addPdfRow(
//                         content,
//                         "Estimated Spend",
//                         String.format(
//                                 "$%.3f",
//                                 report.estimatedSpend()
//                         ),
//                         y,
//                         margin
//                 );

//                 y = addPdfRow(
//                         content,
//                         "Anomalies Caught",
//                         String.valueOf(
//                                 report.anomaliesCaught()
//                         ),
//                         y,
//                         margin
//                 );

//                 y = addPdfRow(
//                         content,
//                         "Peak Risk Level",
//                         report.peakRiskLevel()
//                                 .toUpperCase(),
//                         y,
//                         margin
//                 );

//                 y = addPdfRow(
//                         content,
//                         "Peak Anomaly Score",
//                         String.format(
//                                 "%.2f",
//                                 report.peakAnomalyScore()
//                         ),
//                         y,
//                         margin
//                 );


//                 // ------------------------------------------------
//                 // SUMMARY
//                 // ------------------------------------------------

//                 y -= 25;

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA_BOLD
//                         ),
//                         14
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         y
//                 );

//                 content.showText(
//                         "RECOMMENDATION & SUMMARY"
//                 );

//                 content.endText();


//                 y -= 30;

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA
//                         ),
//                         11
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         y
//                 );

//                 content.showText(
//                         report.summary()
//                 );

//                 content.endText();


//                 // ------------------------------------------------
//                 // FOOTER
//                 // ------------------------------------------------

//                 content.beginText();

//                 content.setFont(
//                         new PDType1Font(
//                                 Standard14Fonts.FontName.HELVETICA
//                         ),
//                         9
//                 );

//                 content.newLineAtOffset(
//                         margin,
//                         40
//                 );

//                 content.showText(
//                         "Generated automatically by APEye"
//                 );

//                 content.endText();
//             }

//             document.save(outputStream);
//         }

//         return outputStream.toByteArray();
//     }


//     // ============================================================
//     // PDF ROW HELPER
//     // ============================================================

//     private float addPdfRow(
//             PDPageContentStream content,
//             String label,
//             String value,
//             float y,
//             float margin)
//             throws Exception {

//         content.beginText();

//         content.setFont(
//                 new PDType1Font(
//                         Standard14Fonts.FontName.HELVETICA_BOLD
//                 ),
//                 11
//         );

//         content.newLineAtOffset(
//                 margin,
//                 y
//         );

//         content.showText(
//                 label + ": "
//         );

//         content.setFont(
//                 new PDType1Font(
//                         Standard14Fonts.FontName.HELVETICA
//                 ),
//                 11
//         );

//         content.showText(
//                 value
//         );

//         content.endText();

//         return y - 28;
//     }
// }






















































































package com.saasguardian.decision;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.alert.email.from}")
    private String senderEmail;

    @Value("${app.alert.email.to}")
    private String recipientEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAnomalyAlert(
            int calls,
            double baseline,
            double score) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);

        message.setSubject("⚠️ APEye - Anomaly Detected");

        message.setText(
                "APEye detected unusual API usage.\n\n" +
                "Current API calls: " + calls + "\n" +
                "Previous baseline: " + baseline + "\n" +
                "Anomaly score: " + score + "\n\n" +
                "Please check your APEye dashboard for more details."
        );

        mailSender.send(message);
    }
    public void sendSessionReport(
        DecisionController.SessionReportRequest report) {

    jakarta.mail.internet.MimeMessage message =
            mailSender.createMimeMessage();

    try {
        org.springframework.mail.javamail.MimeMessageHelper helper =
                new org.springframework.mail.javamail.MimeMessageHelper(
                        message,
                        true,
                        "UTF-8"
                );

        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);

        helper.setSubject("APEye - Session Summary Report");

        String riskClass = report.peakRiskLevel().toLowerCase();

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
        </head>

        <body style="
            margin:0;
            padding:30px;
            background:#f4f4f8;
            font-family:Arial,Helvetica,sans-serif;
            color:#242430;
        ">

            <div style="
                max-width:650px;
                margin:auto;
                background:white;
                border-radius:18px;
                overflow:hidden;
                border:1px solid #e5e5ed;
            ">

                <div style="
                    padding:28px 32px;
                    border-bottom:1px solid #e8e8ef;
                ">
                    <div style="
                        font-size:11px;
                        letter-spacing:2px;
                        color:#7357ff;
                        font-weight:bold;
                        margin-bottom:8px;
                    ">
                        SESSION SUMMARY REPORT
                    </div>

                    <div style="
                        font-size:25px;
                        font-weight:bold;
                    ">
                        Monitoring Session Completed
                    </div>
                </div>

                <div style="padding:26px 32px 10px 32px;">

                    <div style="
                        background:#fff1f1;
                        border:1px solid #ffcaca;
                        border-radius:14px;
                        padding:18px;
                    ">

                        <div style="
                            font-size:17px;
                            font-weight:bold;
                            margin-bottom:6px;
                        ">
                             Bill-Shock Protection Report
                        </div>

                        <div style="
                            color:#777783;
                            font-size:14px;
                        ">
                            %d usage anomaly event(s) were detected during this session.
                        </div>

                    </div>

                </div>

                <div style="padding:18px 32px;">

                    <table width="100%%" cellspacing="0" cellpadding="0">

                        <tr>

                            <td style="padding:6px;width:50%%;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        SESSION DURATION
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        %s
                                    </div>
                                </div>
                            </td>

                            <td style="padding:6px;width:50%%;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        TOTAL REQUESTS
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        %,d
                                    </div>
                                </div>
                            </td>

                        </tr>

                        <tr>

                            <td style="padding:6px;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        ESTIMATED SPEND
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        $%.3f
                                    </div>
                                </div>
                            </td>

                            <td style="padding:6px;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        ANOMALIES CAUGHT
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        %d
                                    </div>
                                </div>
                            </td>

                        </tr>

                        <tr>

                            <td style="padding:6px;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        PEAK RISK LEVEL
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        %s
                                    </div>
                                </div>
                            </td>

                            <td style="padding:6px;">
                                <div style="
                                    border:1px solid #e5e5ed;
                                    border-radius:12px;
                                    padding:18px;
                                ">
                                    <div style="
                                        color:#9999aa;
                                        font-size:10px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                    ">
                                        PEAK ANOMALY SCORE
                                    </div>

                                    <div style="
                                        font-size:22px;
                                        font-weight:bold;
                                        margin-top:8px;
                                    ">
                                        %.2f
                                    </div>
                                </div>
                            </td>

                        </tr>

                    </table>

                </div>

                <div style="padding:10px 32px 32px 32px;">

                    <div style="
                        background:#f7f5ff;
                        border-left:4px solid #7357ff;
                        border-radius:10px;
                        padding:20px;
                    ">

                        <div style="
                            color:#7357ff;
                            font-size:12px;
                            font-weight:bold;
                            letter-spacing:1px;
                            margin-bottom:10px;
                        ">
                            RECOMMENDATION &amp; SUMMARY
                        </div>

                        <div style="
                            color:#666674;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            %s
                        </div>

                    </div>

                </div>

                <div style="
                    padding:18px 32px;
                    border-top:1px solid #e8e8ef;
                    color:#9999aa;
                    font-size:11px;
                ">
                    APEye · Intelligent API usage monitoring
                </div>

            </div>

        </body>
        </html>
        """.formatted(
                report.anomaliesCaught(),
                report.duration(),
                report.totalRequests(),
                report.estimatedSpend(),
                report.anomaliesCaught(),
                report.peakRiskLevel(),
                report.peakAnomalyScore(),
                report.summary()
        );
        html = html
        .replace("${report.anomaliesCaught()}", String.valueOf(report.anomaliesCaught()))
        .replace("${report.duration()}", report.duration())
        .replace("${String.format(\"%,d\", report.totalRequests())}",
                String.format("%,d", report.totalRequests()))
        .replace("${String.format(\"%.3f\", report.estimatedSpend())}",
                String.format("%.3f", report.estimatedSpend()))
        .replace("${report.peakRiskLevel()}", report.peakRiskLevel())
        .replace("${String.format(\"%.2f\", report.peakAnomalyScore())}",
                String.format("%.2f", report.peakAnomalyScore()))
        .replace("${report.summary()}", report.summary());

helper.setText(html, true);

        mailSender.send(message);

    } catch (Exception e) {
        throw new RuntimeException(
                "Failed to send session report email",
                e
        );
    }
}
public void sendSessionReport(
        String duration,
        int totalCalls,
        double totalCost,
        int anomalies,
        String peakRisk,
        double peakScore) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom(senderEmail);
    message.setTo(recipientEmail);

    message.setSubject("APEye - Session Summary Report");

    String conclusion;

    if (anomalies > 0) {
        conclusion =
                "During this session, " + anomalies +
                " usage anomaly event(s) were detected. " +
                "Notifications were dispatched to safeguard API spending.";
    } else {
        conclusion =
                "Session concluded safely with 0 anomalies. " +
                "API traffic remained within normal baseline boundaries.";
    }

    message.setText(
            "APEye — SESSION SUMMARY REPORT\n\n" +

            "Monitoring Session Completed\n" +
            "----------------------------------------\n\n" +

            "SESSION DURATION: " + duration + "\n" +
            "TOTAL REQUESTS: " + totalCalls + "\n" +
            "ESTIMATED SPEND: $" + String.format("%.3f", totalCost) + "\n" +
            "ANOMALIES CAUGHT: " + anomalies + "\n" +
            "PEAK RISK LEVEL: " + peakRisk.toUpperCase() + "\n" +
            "PEAK ANOMALY SCORE: " + String.format("%.2f", peakScore) + "\n\n" +

            "RECOMMENDATION & SUMMARY\n" +
            "----------------------------------------\n" +
            conclusion + "\n\n" +

            "This report was generated automatically by APEye."
    );

    mailSender.send(message);
}
}























































































// package com.saasguardian.decision;

// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.pdmodel.PDPage;
// import org.apache.pdfbox.pdmodel.PDPageContentStream;
// import org.apache.pdfbox.pdmodel.common.PDRectangle;
// import org.apache.pdfbox.pdmodel.font.PDType1Font;
// import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;

// import jakarta.mail.internet.MimeMessage;

// import java.awt.Color;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;

// @Service
// public class EmailService {

//     private final JavaMailSender mailSender;

//     @Value("${app.alert.email.from}")
//     private String senderEmail;

//     @Value("${app.alert.email.to}")
//     private String recipientEmail;

//     public EmailService(JavaMailSender mailSender) {
//         this.mailSender = mailSender;
//     }

//     // =========================================================
//     // ANOMALY ALERT EMAIL
//     // =========================================================

//     public void sendAnomalyAlert(
//             int calls,
//             double baseline,
//             double score) {

//         SimpleMailMessage message = new SimpleMailMessage();

//         message.setFrom(senderEmail);
//         message.setTo(recipientEmail);

//         message.setSubject("APEye - Anomaly Detected");

//         message.setText(
//                 "APEye detected unusual API usage.\n\n" +
//                 "Current API calls: " + calls + "\n" +
//                 "Previous baseline: " + baseline + "\n" +
//                 "Anomaly score: " + String.format("%.2f", score) + "\n\n" +
//                 "Please check your APEye dashboard for more details."
//         );

//         mailSender.send(message);
//     }


//     // =========================================================
//     // SESSION REPORT EMAIL + PDF
//     // =========================================================

//     public void sendSessionReport(
//             DecisionController.SessionReportRequest report) {

//         try {

//             // -------------------------------------------------
//             // Generate PDF
//             // -------------------------------------------------

//             byte[] pdfBytes = generateSessionReportPdf(report);


//             // -------------------------------------------------
//             // Create email
//             // -------------------------------------------------

//             MimeMessage message = mailSender.createMimeMessage();

//             MimeMessageHelper helper =
//                     new MimeMessageHelper(
//                             message,
//                             true,
//                             "UTF-8"
//                     );

//             helper.setFrom(senderEmail);
//             helper.setTo(recipientEmail);

//             helper.setSubject(
//                     "APEye - Session Summary Report"
//             );


//             // -------------------------------------------------
//             // Email body
//             // -------------------------------------------------

//             String emailBody = """
//                     APEye Session Summary Report

//                     Monitoring session completed successfully.

//                     Session Duration: %s
//                     Total Requests: %,d
//                     Estimated Spend: $%.3f
//                     Anomalies Caught: %d
//                     Peak Risk Level: %s
//                     Peak Anomaly Score: %.2f

//                     Recommendation & Summary:
//                     %s

//                     The complete session report is attached as a PDF.

//                     — APEye · Intelligent API usage monitoring
//                     """.formatted(
//                     report.duration(),
//                     report.totalRequests(),
//                     report.estimatedSpend(),
//                     report.anomaliesCaught(),
//                     report.peakRiskLevel().toUpperCase(),
//                     report.peakAnomalyScore(),
//                     report.summary()
//             );

//             helper.setText(emailBody);

//             // -------------------------------------------------
//             // Attach PDF
//             // -------------------------------------------------

//             helper.addAttachment(
//                     "APEye-Session-Report.pdf",
//                     new ByteArrayResource(pdfBytes)
//             );


//             // -------------------------------------------------
//             // Send
//             // -------------------------------------------------

//             mailSender.send(message);

//             System.out.println(
//                     "Session report email + PDF sent successfully."
//             );

//         } catch (Exception e) {

//             throw new RuntimeException(
//                     "Failed to generate/send session report PDF",
//                     e
//             );
//         }
//     }


//     // =========================================================
//     // PDF GENERATION
//     // =========================================================

//     private byte[] generateSessionReportPdf(
//             DecisionController.SessionReportRequest report)
//             throws IOException {

//         ByteArrayOutputStream output =
//                 new ByteArrayOutputStream();

//         try (PDDocument document = new PDDocument()) {

//             PDPage page =
//                     new PDPage(PDRectangle.A4);

//             document.addPage(page);


//             // -------------------------------------------------
//             // Fonts
//             // -------------------------------------------------

//             PDType1Font regular =
//                     new PDType1Font(
//                             Standard14Fonts.FontName.HELVETICA
//                     );

//             PDType1Font bold =
//                     new PDType1Font(
//                             Standard14Fonts.FontName.HELVETICA_BOLD
//                     );


//             // -------------------------------------------------
//             // Page dimensions
//             // -------------------------------------------------

//             float pageWidth =
//                     page.getMediaBox().getWidth();

//             float pageHeight =
//                     page.getMediaBox().getHeight();

//             float margin = 45;

//             float contentWidth =
//                     pageWidth - (margin * 2);


//             try (PDPageContentStream cs =
//                          new PDPageContentStream(
//                                  document,
//                                  page
//                          )) {


//                 // =================================================
//                 // HEADER
//                 // =================================================

//                 float y = pageHeight - 55;

//                 drawText(
//                         cs,
//                         bold,
//                         9,
//                         "SESSION SUMMARY REPORT",
//                         margin,
//                         y,
//                         new Color(115, 87, 255)
//                 );

//                 y -= 25;

//                 drawText(
//                         cs,
//                         bold,
//                         22,
//                         "Monitoring Session Completed",
//                         margin,
//                         y,
//                         new Color(35, 35, 45)
//                 );


//                 // separator

//                 y -= 25;

//                 drawLine(
//                         cs,
//                         margin,
//                         y,
//                         pageWidth - margin,
//                         y,
//                         new Color(225, 225, 235)
//                 );


//                 // =================================================
//                 // PROTECTION REPORT BOX
//                 // =================================================

//                 y -= 55;

//                 float alertHeight = 65;

//                 drawRoundedBox(
//                         cs,
//                         margin,
//                         y - alertHeight,
//                         contentWidth,
//                         alertHeight,
//                         new Color(255, 241, 241),
//                         new Color(255, 202, 202)
//                 );

//                 drawText(
//                         cs,
//                         bold,
//                         12,
//                         "Bill-Shock Protection Report",
//                         margin + 15,
//                         y - 22,
//                         new Color(50, 50, 60)
//                 );

//                 drawText(
//                         cs,
//                         regular,
//                         9,
//                         report.anomaliesCaught()
//                                 + " usage anomaly event(s) were detected during this session.",
//                         margin + 15,
//                         y - 40,
//                         new Color(115, 115, 125)
//                 );


//                 // =================================================
//                 // STATISTICS
//                 // =================================================

//                 y -= 95;

//                 float gap = 10;

//                 float cardWidth =
//                         (contentWidth - gap) / 2;

//                 float cardHeight = 75;


//                 // Row 1

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "SESSION DURATION",
//                         report.duration()
//                 );

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin + cardWidth + gap,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "TOTAL REQUESTS",
//                         String.format(
//                                 "%,d",
//                                 report.totalRequests()
//                         )
//                 );


//                 // Row 2

//                 y -= cardHeight + gap;

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "ESTIMATED SPEND",
//                         "$" + String.format(
//                                 "%.3f",
//                                 report.estimatedSpend()
//                         )
//                 );

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin + cardWidth + gap,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "ANOMALIES CAUGHT",
//                         String.valueOf(
//                                 report.anomaliesCaught()
//                         )
//                 );


//                 // Row 3

//                 y -= cardHeight + gap;

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "PEAK RISK LEVEL",
//                         report.peakRiskLevel().toUpperCase()
//                 );

//                 drawStatCard(
//                         cs,
//                         regular,
//                         bold,
//                         margin + cardWidth + gap,
//                         y,
//                         cardWidth,
//                         cardHeight,
//                         "PEAK ANOMALY SCORE",
//                         String.format(
//                                 "%.2f",
//                                 report.peakAnomalyScore()
//                         )
//                 );


//                 // =================================================
//                 // RECOMMENDATION
//                 // =================================================

//                 y -= cardHeight + 35;

//                 float conclusionHeight = 95;

//                 drawRoundedBox(
//                         cs,
//                         margin,
//                         y - conclusionHeight,
//                         contentWidth,
//                         conclusionHeight,
//                         new Color(247, 245, 255),
//                         new Color(220, 215, 255)
//                 );


//                 // Purple vertical line

//                 drawFilledRect(
//                         cs,
//                         margin,
//                         y - conclusionHeight,
//                         4,
//                         conclusionHeight,
//                         new Color(115, 87, 255)
//                 );


//                 drawText(
//                         cs,
//                         bold,
//                         9,
//                         "RECOMMENDATION & SUMMARY",
//                         margin + 15,
//                         y - 20,
//                         new Color(115, 87, 255)
//                 );


//                 // Summary text

//                 String summary =
//                         report.summary();

//                 drawWrappedText(
//                         cs,
//                         regular,
//                         10,
//                         summary,
//                         margin + 15,
//                         y - 42,
//                         contentWidth - 30,
//                         14,
//                         new Color(95, 95, 105)
//                 );


//                 // =================================================
//                 // FOOTER
//                 // =================================================

//                 float footerY = 55;

//                 drawLine(
//                         cs,
//                         margin,
//                         footerY + 15,
//                         pageWidth - margin,
//                         footerY + 15,
//                         new Color(230, 230, 235)
//                 );

//                 drawText(
//                         cs,
//                         regular,
//                         8,
//                         "APEye · Intelligent API usage monitoring",
//                         margin,
//                         footerY,
//                         new Color(150, 150, 160)
//                 );
//             }

//             document.save(output);
//         }

//         return output.toByteArray();
//     }


//     // =========================================================
//     // STAT CARD
//     // =========================================================

//     private void drawStatCard(
//             PDPageContentStream cs,
//             PDType1Font regular,
//             PDType1Font bold,
//             float x,
//             float y,
//             float width,
//             float height,
//             String label,
//             String value)
//             throws IOException {

//         drawRoundedBox(
//                 cs,
//                 x,
//                 y,
//                 width,
//                 height,
//                 Color.WHITE,
//                 new Color(225, 225, 235)
//         );

//         drawText(
//                 cs,
//                 bold,
//                 7,
//                 label,
//                 x + 12,
//                 y + height - 18,
//                 new Color(150, 150, 165)
//         );

//         drawText(
//                 cs,
//                 bold,
//                 16,
//                 value,
//                 x + 12,
//                 y + 27,
//                 new Color(40, 40, 50)
//         );
//     }


//     // =========================================================
//     // DRAW TEXT
//     // =========================================================

//     private void drawText(
//             PDPageContentStream cs,
//             PDType1Font font,
//             float size,
//             String text,
//             float x,
//             float y,
//             Color color)
//             throws IOException {

//         cs.beginText();

//         cs.setFont(font, size);

//         cs.setNonStrokingColor(color);

//         cs.newLineAtOffset(x, y);

//         cs.showText(
//                 text == null ? "" : text
//         );

//         cs.endText();
//     }


//     // =========================================================
//     // WRAPPED TEXT
//     // =========================================================

//     private void drawWrappedText(
//             PDPageContentStream cs,
//             PDType1Font font,
//             float fontSize,
//             String text,
//             float x,
//             float y,
//             float maxWidth,
//             float lineHeight,
//             Color color)
//             throws IOException {

//         if (text == null) {
//             return;
//         }

//         String[] words =
//                 text.split("\\s+");

//         StringBuilder line =
//                 new StringBuilder();

//         float currentY = y;

//         for (String word : words) {

//             String test =
//                     line.length() == 0
//                             ? word
//                             : line + " " + word;

//             float width =
//                     font.getStringWidth(test)
//                             / 1000 * fontSize;

//             if (width > maxWidth &&
//                     line.length() > 0) {

//                 drawText(
//                         cs,
//                         font,
//                         fontSize,
//                         line.toString(),
//                         x,
//                         currentY,
//                         color
//                 );

//                 currentY -= lineHeight;

//                 line =
//                         new StringBuilder(word);

//             } else {

//                 line =
//                         new StringBuilder(test);
//             }
//         }

//         if (line.length() > 0) {

//             drawText(
//                     cs,
//                     font,
//                     fontSize,
//                     line.toString(),
//                     x,
//                     currentY,
//                     color
//             );
//         }
//     }


//     // =========================================================
//     // RECTANGLES / LINES
//     // =========================================================

//     private void drawFilledRect(
//             PDPageContentStream cs,
//             float x,
//             float y,
//             float width,
//             float height,
//             Color color)
//             throws IOException {

//         cs.setNonStrokingColor(color);

//         cs.addRect(
//                 x,
//                 y,
//                 width,
//                 height
//         );

//         cs.fill();
//     }


//     private void drawLine(
//             PDPageContentStream cs,
//             float x1,
//             float y1,
//             float x2,
//             float y2,
//             Color color)
//             throws IOException {

//         cs.setStrokingColor(color);

//         cs.setLineWidth(1);

//         cs.moveTo(x1, y1);

//         cs.lineTo(x2, y2);

//         cs.stroke();
//     }


//     private void drawRoundedBox(
//             PDPageContentStream cs,
//             float x,
//             float y,
//             float width,
//             float height,
//             Color fill,
//             Color border)
//             throws IOException {

//         float radius = 8;

//         cs.setNonStrokingColor(fill);

//         cs.setStrokingColor(border);

//         cs.setLineWidth(1);

//         // Main rectangle

//         cs.addRect(
//                 x,
//                 y,
//                 width,
//                 height
//         );

//         cs.fillAndStroke();
//     }
// }