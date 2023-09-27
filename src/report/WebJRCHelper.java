package report;

import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBagHelper;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class WebJRCHelper {
	public static void changeXMLDataSource(ReportClientDocument clientDoc, String xmlfile, String xsdfile)
			throws ReportSDKException {
		PropertyBag propertyBag = null;
		IConnectionInfo connectionInfo;
		ITable origTable = null;
		ITable newTable = null;
		// The next few parameters are optional parameters which you may want to
		// uncomment
		// You may wish to adjust the arguments of the method to pass these
		// values in if necessary
		// String TABLE_NAME_QUALIFIER = "new_table_name";
		// String SERVER_NAME = "new_server_name";
		// String CONNECTION_STRING = "new_connection_string";
		// String DATABASE_NAME = "new_database_name";
		// String URI = "new_URI";
		// Obtain collection of tables from this database controller
		Tables tables = clientDoc.getDatabaseController().getDatabase().getTables();
		// see http://www.programmereye.com/145284/

		for (int i = 0; i < tables.size(); i++) {
			origTable = tables.getTable(i);
			newTable = (ITable) origTable.clone(true);
			// We set the Fully qualified name to the Table Alias to keep the
			// method generic
			// This workflow may not work in all scenarios and should likely be
			// customized to work
			// in the developer's specific situation. The end result of this
			// statement will be to strip
			// the existing table of it's db specific identifiers. For example
			// Xtreme.dbo.Customer becomes just Customer
			newTable.setQualifiedName(origTable.getAlias());
			// Change properties that are different from the original datasource
			// For example, if the table name has changed you will be required
			// to change it during this routine
			// Change connection information properties
			connectionInfo = newTable.getConnectionInfo();
			// Set new table connection property attributes
			propertyBag = new PropertyBag();
			propertyBag.put("Local XML File", xmlfile);
			propertyBag.put("Local Schema File", xsdfile);
			propertyBag.putStringValue(PropertyBagHelper.CONNINFO_SERVER_TYPE, "XML");
			propertyBag.put("Database DLL", "crdb_xml.dll");

			connectionInfo.setAttributes(propertyBag);
			// Set database username and passCredentials
			// NOTE: Even if the username and passCredentials properties do not change
			// when switching databases, the
			// database passCredentials is *not* saved in the report and must be set at
			// runtime if the database is secured.
			// connectionInfo.setUserName(DB_USER_NAME);
			// connectionInfo.setPassCredentials(DB_PASSCredentials);
			// Update the table information
			clientDoc.getDatabaseController().setTableLocation(origTable, newTable);
		}
		// Next loop through all the subreports and pass in the same
		// information. You may consider
		// creating a separate method which accepts
		IStrings subNames = clientDoc.getSubreportController().getSubreportNames();
		for (int subNum = 0; subNum < subNames.size(); subNum++) {
			tables = clientDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController()
					.getDatabase().getTables();
			for (int i = 0; i < tables.size(); i++) {
				origTable = tables.getTable(i);
				newTable = (ITable) origTable.clone(true);
				// We set the Fully qualified name to the Table Alias to keep
				// the method generic
				// This workflow may not work in all scenarios and should likely
				// be customized to work
				// in the developer's specific situation. The end result of this
				// statement will be to strip
				// the existing table of it's db specific identifiers. For
				// example Xtreme.dbo.Customer becomes just Customer
				// newTable.setQualifiedName(origTable.getAlias());
				// Change properties that are different from the original
				// datasource
				// table.setQualifiedName(TABLE_NAME_QUALIFIER);
				// Change connection information properties
				connectionInfo = newTable.getConnectionInfo();
				// Set new table connection property attributes
				propertyBag = new PropertyBag();
				// Overwrite any existing properties with updated values
				// Set new table connection property attributes
				propertyBag.put("Local XML File", xmlfile);
				propertyBag.put("Local Schema File", xsdfile);
				propertyBag.putStringValue(PropertyBagHelper.CONNINFO_SERVER_TYPE, "XML");
				propertyBag.put("Database DLL", "crdb_xml.dll");
				connectionInfo.setAttributes(propertyBag);
				// Set database username and passCredentials
				// NOTE: Even if the username and passCredentials properties do not
				// change when switching databases, the
				// database passCredentials is *not* saved in the report and must be
				// set at runtime if the database is secured.
				// connectionInfo.setUserName(DB_USER_NAME);
				// connectionInfo.setPassCredentials(DB_PASSCredentials);
				// Update the table information
				clientDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController()
						.setTableLocation(origTable, newTable);
			}
		}

	}
}
