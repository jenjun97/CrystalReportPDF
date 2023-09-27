package report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBagHelper;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class ReportMain {
	public static void main(String[] args) throws ReportSDKException, IOException {
		// 設定檔案參數及路徑
		String rptPath = "D:/B5800.rpt";
		String xsdPath = "D:/TBBReport.xsd";
		String xmlPath = "D:/B5800.xml";
		String outputPath = "D:/B5800.pdf";

		// 開啟RPT
		ReportClientDocument report = new ReportClientDocument();
		report.open(rptPath, OpenReportOptions._discardSavedData);

		// 開啟主table
		Tables tables = report.getDatabaseController().getDatabase().getTables();
		for (int i = 0; i < tables.size(); i++) {
			// 取出原table
			ITable origTable = tables.getTable(i);

			// 複製一份為新table，並將原table參數名稱取出，放入到新table裡
			ITable newTable = (ITable) origTable.clone(true);
			newTable.setQualifiedName(origTable.getAlias());

			// 取出table的資料庫參數的資訊
			IConnectionInfo newConnInfo = newTable.getConnectionInfo();
			PropertyBag att = newConnInfo.getAttributes();
			// 顯示資料庫的所有參數
			Set<Entry<Object, Object>> entrySet = att.entrySet();
			for (Entry entry : entrySet) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}

			// 替換資料庫參數，主要是xml、及xsd
			PropertyBag propertyBag = new PropertyBag();
			propertyBag.put("Local XML File", xmlPath);
			propertyBag.put("Local Schema File", xsdPath);
			propertyBag.put("Database DLL", "crdb_xml.dll");
			propertyBag.putStringValue(PropertyBagHelper.CONNINFO_SERVER_TYPE, "XML");

			// 將新參數放到新table裡
			newConnInfo.setAttributes(propertyBag);

			// 將報表的原table, 替換成新table
			report.getDatabaseController().setTableLocation(origTable, newTable);
		}

		// 設定輸出路徑及格式
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		report.getPrintOutputController().export(ReportExportFormat.PDF, outputStream);
		// 關閉串流(後開先關)
		outputStream.flush();
		outputStream.close();
		report.close();

		// 使用 Java 的 Desktop 類開啟 PDF 文件
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(new File(outputPath));
		}

		System.out.println("report end");
	}

}
