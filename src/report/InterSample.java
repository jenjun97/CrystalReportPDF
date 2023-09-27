package report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.Connection;
import com.crystaldecisions.sdk.occa.report.data.ConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.Connections;
import com.crystaldecisions.sdk.occa.report.data.IConnection;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.exportoptions.ExportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.IExportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class InterSample {
	public static void main(String[] args) throws ReportSDKException, IOException {
		String rptPath = "D:/B5800.rpt";
		String xsdPath = "D:/TBBReport.xsd";
		String xmlPath = "D:/B5800.xml";
		String outputPath = "D:/B5800.pdf";

		ReportClientDocument report = new ReportClientDocument();
		report.open(rptPath, 0);

		Connections conn = report.getDatabase().getConnections();
		for (int i = 0; i < conn.size(); i++) {
			IConnection iconn = conn.get(i);

			PropertyBag att = iconn.getConnectionInfo().getAttributes();
			Set<Entry<Object, Object>> entrys = att.entrySet();
			for (Entry entry : entrys) {
				System.out.println("舊：" + entry.getKey() + "\t=" + entry.getValue());
			}
		}

		// 設定各參數
		PropertyBag att = new PropertyBag();
		att.put("Database DLL", "crdb_xml.dll");
		att.put("Local Schema File", xsdPath);
		att.put("Server Type", "XML");
		att.put("Local XML File", xmlPath);
		att.put("Server Name", xmlPath + " " + xsdPath);
		att.put("PreQEServerName", xmlPath + " " + xsdPath);

		IConnectionInfo iconnInfo = new ConnectionInfo();
		iconnInfo.setAttributes(att);

		IConnection iconn = new Connection();
		iconn.setConnectionInfo(iconnInfo);

		conn.set(0, iconn);

		for (int i = 0; i < conn.size(); i++) {

			PropertyBag prop = conn.get(i).getConnectionInfo().getAttributes();
			Set<Entry<Object, Object>> entrys = att.entrySet();
			for (Entry entry : entrys) {
				System.out.println("新：" + entry.getKey() + "\t=" + entry.getValue());
			}
		}

		// Export report to PDF format
		IExportOptions exportOptions = new ExportOptions();
		exportOptions.setExportFormatType(ReportExportFormat.PDF);

		FileOutputStream outputStream = new FileOutputStream(outputPath);
		report.getPrintOutputController().export(ReportExportFormat.PDF, outputStream);
		outputStream.close();

		// 使用 Java 的 Desktop 類開啟 PDF 文件
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(new File(outputPath));
		}

		System.out.println("main end");

	}
}
