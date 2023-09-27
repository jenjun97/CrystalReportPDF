package report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.exportoptions.ExportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class WebSample {
	public static void main(String[] args) throws ReportSDKException, IOException {
		String rptPath = "D:/B5800.rpt";
		String xsdPath = "D:/TBBReport.xsd";
		String xmlPath = "D:/B5800.xml";
		String outputPath = "D:/B5800.pdf";

		ReportClientDocument report = new ReportClientDocument();
		report.open(rptPath, OpenReportOptions._discardSavedData);

		WebJRCHelper.changeXMLDataSource(report, xmlPath, xsdPath);
		ExportOptions exportOptions = new ExportOptions();
		exportOptions.setExportFormatType(ReportExportFormat.PDF);

		FileOutputStream outputStream = new FileOutputStream(outputPath);
		report.getPrintOutputController().export(ReportExportFormat.PDF, outputStream);
		outputStream.flush();
		outputStream.close();

		report.close();

		// 使用 Java 的 Desktop 類開啟 PDF 文件
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(new File(outputPath));
		}

		System.out.println("main end");
	}

}
