package report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.application.SubreportController;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBagHelper;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class ReportSub {
	public static void main(String[] args) throws ReportSDKException, IOException {

		// 設定檔案參數及路徑
//		String rptPath = "D:/B3800.rpt";
//		String xmlPath = "D:/B3800.xml";
//		String outputPath = "D:/B3800.pdf";

		String rptPath = "D:/B5800.rpt";
		String xmlPath = "D:/B5800.xml";
		String outputPath = "D:/B5800.pdf";

		String xsdPath = "D:/TBBReport.xsd";
		// 開啟RPT
		ReportClientDocument report = new ReportClientDocument();
		report.open(rptPath, OpenReportOptions._discardSavedData);

		// 替換主報表
		replaceMainTable(report, xsdPath, xmlPath);

		// 替換子報表
		replaceSubTable(report, xsdPath, xmlPath);

		// 設定輸出路徑及格式
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		report.getPrintOutputController().export(ReportExportFormat.PDF, outputStream);

		// 關閉串流(後開先關)
		outputStream.flush();
		outputStream.close();

		// 使用 Java 的 Desktop 類開啟 PDF 文件
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(new File(outputPath));
		}

		System.out.println("report end");
	}

	/**
	 * 替換子報表
	 * 
	 * @param report  ReportClientDocument開啟rtp的物件
	 * @param xsdPath xsd的路徑
	 * @param xmlPath xml的路徑
	 * @throws ReportSDKException
	 */
	private static void replaceSubTable(ReportClientDocument report, String xsdPath, String xmlPath)
			throws ReportSDKException {

		// 取得子報表集合
		SubreportController subController = report.getSubreportController();
		IStrings subTables = subController.getSubreportNames();

		// 若沒有子報表，就直接離開
		if (subTables.size() < 1) {
			return;
		}

		// 各別取出子報表
		for (int i = 0; i < subTables.size(); i++) {

			// 依子報表的名稱，取出子報表
			String subTableName = subTables.getString(i);
			Tables tables = subController.getSubreport(subTableName).getDatabaseController().getDatabase().getTables();
			System.out.println(subTableName);

			// 取出子報表的table
			for (int j = 0; j < tables.size(); j++) {
				// 取出原table後並複製一份
				ITable origTable = tables.getTable(j);
				ITable newTable = (ITable) origTable.clone(true);

				// 取出table的資料庫參數的資訊
				IConnectionInfo connectionInfo = newTable.getConnectionInfo();

				// 替換資料庫參數，主要是xml、及xsd
				PropertyBag propertyBag = new PropertyBag();
				propertyBag.put("Local XML File", xmlPath);
				propertyBag.put("Local Schema File", xsdPath);
				propertyBag.putStringValue(PropertyBagHelper.CONNINFO_SERVER_TYPE, "XML");
				propertyBag.put("Database DLL", "crdb_xml.dll");
				propertyBag.put("Server Type", "XML");
				connectionInfo.setAttributes(propertyBag);

				// 將報表的原table, 替換成新table
				report.getSubreportController().getSubreport(subTableName).getDatabaseController()
						.setTableLocation(origTable, newTable);
			}
		}
	}

	/**
	 * 替換主報表
	 * 
	 * @param report  ReportClientDocument開啟rtp的物件
	 * @param xsdPath xsd的路徑
	 * @param xmlPath xml的路徑
	 * @throws ReportSDKException
	 */
	private static void replaceMainTable(ReportClientDocument report, String xsdPath, String xmlPath)
			throws ReportSDKException {

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
	}

}
