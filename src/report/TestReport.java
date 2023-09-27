package report;

import java.io.IOException;

import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class TestReport {

	public static void main(String[] args) throws ReportSDKException, IOException {
		// 設定檔案參數及路徑
		String rptPath = "D:/B5800.rpt";
		String xsdPath = "D:/TBBReport.xsd";
		String xmlPath = "D:/B5800.xml";
		String outputPath = "D:/B5800.pdf";

		// 開啟RPT
		ReportClientDocument report = new ReportClientDocument();
		report.open(rptPath, 0);

//		ParameterDiscreteValue discretevalue = new ParameterDiscreteValue();
//		discretevalue.Value = objValue; // Assign parameter
//		ParameterValues values = new ParameterValues();
//		values.Add(discretevalue);
//		crReport.DataDefinition.ParameterFields(i).ApplyCurrentValues(values);
		

		System.out.println("report end");

	}
}
