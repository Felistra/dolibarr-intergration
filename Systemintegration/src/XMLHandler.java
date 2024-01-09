import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class XMLHandler {
	private ArrayList<Lead> dolibarrList;
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());
	
	/**
	 * Constructor for XMLHandler.
	 * Instantiates dolibarrList.
	 * Calls function getLeadsFromDolibarr to receive leads from Dolibarr as the previous list.
	 */
	public XMLHandler() {
		dolibarrList = new ArrayList<Lead>();
		
		try {
			getLeadsFromDolibarr();
		} catch (ClassNotFoundException e) {
        	LOGGER.severe("ClassNotFoundException in XMLHandler - Konstruktor");
		} catch(Exception e) {
        	LOGGER.severe("Exception in XMLHandler - Konstruktor");
		}
	}
	
	/**
	 * When uploading new leads to Dolibarr, this function sets the status for all leads to "LEAD" where the status lead is 6 in the database and the default value when uploading leads is 0.
	 * SQL queries is used because it was not possible to use multiple streams within the same class without provoking exceptions.
	 * @throws ClassNotFoundException
	 * If the driver for executing the query is not found, the method throws a ClassNotFoundException.
	 */
	public void setLeadStatusInDolibarr() throws ClassNotFoundException {
        try {
        	String url = "jdbc:mysql://localhost:3306/dolibarr";
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String sql = "UPDATE llx_societe SET fk_stcomm = '6' WHERE fk_stcomm = '0'";
            statement.executeUpdate(sql);
            
        } catch(ClassNotFoundException e) {
        	LOGGER.severe("ClassNotFoundException i XMLHandler - setLeadStatusInDolibarr");
        } catch (SQLException e) {
            LOGGER.severe("SQLException in XMLHandler - setLeadStatusInDolibarr");
        } catch(Exception e) {
            LOGGER.severe("Exception in XMLHandler - setLeadStatusInDolibarr");
        }
    }
		
	/**
	 * Code inspired from: https://www.javaguides.net/2019/07/java-http-getpost-request-example.html och Dolibarrs Rest API Explorer med curl-kod.
	 * Upload leads to Dolibarr with a URL connection where the request method is POST.
	 */
	public void importLeadsToDolibarr(ArrayList<Lead> importList) {
		try {
			InputStream input = XMLHandler.class.getClassLoader().getResourceAsStream("config_local.properties");
			Properties prop = new Properties(); 
			
			prop.load(input);
			
			String CONNECT_API_KEY = prop.getProperty("doli.api.key"); 
			String CONNECT_API_URL = prop.getProperty("doli.api.url"); 
			
			URL url = new URL(CONNECT_API_URL);
			
			for (int i = 0; i < importList.size(); i++) {
				
				HttpURLConnection httpc = (HttpURLConnection) url.openConnection();
				httpc.setRequestMethod("POST");
				
				httpc.setRequestProperty("Content-Type", "application/xml");
				httpc.setRequestProperty("Accept", "application/json");
				httpc.setRequestProperty("DOLAPIKEY", CONNECT_API_KEY);
				
				httpc.setDoOutput(true);
				
				OutputStreamWriter writer = new OutputStreamWriter(httpc.getOutputStream());
				
				String data = "<?xml version=\"1.0\"?><lead>"
		 	            + "<name>" + importList.get(i).getCompany() + "</name>"
		 	            + "<name_alias>" + importList.get(i).getContact() + "</name_alias>"
		 	            + "<address>" + importList.get(i).getAddress() + "</address>"
		 	            + "<zip>" + importList.get(i).getZipCode() + "</zip>"
		 	            + "<town>" + importList.get(i).getCity() + "</town>"
		 	            + "<email>" + importList.get(i).getEmail() + "</email>"
		 	            + "<phone>" + importList.get(i).getPhoneNumber() + "</phone>"
		 	            + "<idprof1>" + importList.get(i).getCurrentProvider() + "</idprof1>"
		 	            + "<idprof2>" + importList.get(i).getSize() + "</idprof2>";
						data += "</lead>";
						
			    		writer.write(data);
						writer.close();
					    httpc.getOutputStream().close();			  

					    InputStream responseStream = httpc.getResponseCode() / 100 == 2
								? httpc.getInputStream()
								: httpc.getErrorStream();
					    setLeadStatusInDolibarr();
					    responseStream.close();
			}
			
		} catch(RuntimeException e) {
			LOGGER.severe("RuntimeException in XMLHandler - importLeadsToDolibarr");
		} catch (MalformedURLException e) {
			LOGGER.severe("MalformedURLException in XMLHandler - importLeadsToDolibarr");
		} catch (Exception e) {
			LOGGER.severe("Exception i XMLHandler - importLeadsToDolibarr");
		} 
	}
	
	/**
	 * Method that get all leads from Dolibarr through a query from the database.
	 * A query is used because there it was not possible to use multiple streams within the same class.
	 * @throws ClassNotFoundException
	 * If the driver for executing the query is not found, the method throws a ClassNotFoundException.
	 */
	public void getLeadsFromDolibarr() throws ClassNotFoundException {
        try {
        	String url = "jdbc:mysql://localhost:3306/dolibarr";
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            
            ResultSet result;
            String sql = "SELECT * FROM llx_societe";
            result = statement.executeQuery(sql);
            
            while(result.next()) {
            	setListWithDataFromDolibarr(result);
            }
            
        } catch(ClassNotFoundException e) {
        	LOGGER.severe("ClassNotFoundException i XMLHandler - getLeadsFromDolibarr");
        } catch (SQLException e) {
            LOGGER.severe("SQLException i XMLHandler - getLeadsFromDolibarr");
        } catch(Exception e) {
            LOGGER.severe("Exception i XMLHandler - getLeadsFromDolibarr");
        }
    }
	
	/**
	 * For each result from the getLeadsFromDolibar, a dolibarrLead is created and all values for lead is set.
	 * @throws SQLException 
	 * If there is a problem with the query when this method is called, a SQLException is thrown.
	 */
	public void setListWithDataFromDolibarr(ResultSet result) throws SQLException {
		try {
		Lead dolibarrLead = new Lead();
		dolibarrLead.setCompany(result.getString("nom"));
		dolibarrLead.setContact(result.getString("name_alias"));
		dolibarrLead.setAddress(result.getString("address"));
		dolibarrLead.setZipCode(result.getString("zip"));
		dolibarrLead.setCity(result.getString("town"));
		dolibarrLead.setEmail(result.getString("email"));
		dolibarrLead.setPhoneNumber(result.getString("phone"));
		dolibarrLead.setCurrentProvider(result.getString("siren"));
		dolibarrLead.setSize(result.getString("siret"));
		dolibarrLead.checkStatus(result.getInt("fk_stcomm"));
		dolibarrLead.setStatus(dolibarrLead.getStatus());	
	
		LOGGER.info("List with information about leads from Dolibarr was successfully retrieved");
		dolibarrList.add(dolibarrLead);
		
		} catch (SQLException e) {
			LOGGER.severe("SQLException in XMLHandler - setListWithDataFromDolibarr");
        } catch(Exception e) {
        	LOGGER.severe("Exception in XMLHandler - setListWithDataFromDolibarr");
        }
	}
	
	/**
	 * Method that deletes all leads with status "LEAD" in Dolibarr.
	 * @param deleteList
	 * A list that contains all leads that will be deleted.
	 */
	public void deleteFromDolibarr(ArrayList<Lead> deleteList) {
		try {
        	String url = "jdbc:mysql://localhost:3306/dolibarr";
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            
            String sql = "DELETE FROM `llx_societe` WHERE phone IN (";
    
            for(int i = 0; i < deleteList.size(); i++) {
            	 sql += "'" + deleteList.get(i).getPhoneNumber() + "', "; 
            	 
            	 if(i == deleteList.size() - 1) {
            		 sql += "'" + deleteList.get(i).getPhoneNumber() + "');";
            	 }
            }    
            
            statement.executeUpdate(sql);
            LOGGER.info("Leads with status LEAD was successfully deleted");
            
        } catch(ClassNotFoundException e) {
        	LOGGER.severe("ClassNotFoundException in XMLHandler - deleteFromDolibarr");
        } catch (SQLException e) {
        	LOGGER.severe("SQLException in XMLHandler - deleteFromDolibarr");
        } catch(Exception e) {
        	LOGGER.severe("Exception in XMLHandler - deleteFromDolibarr");
        }
	}
	
	/**
	 * Get method for receiving the leads from Dolibarr.
	 * @return the dolibarrList
	 */
	public ArrayList<Lead> getDolibarrList() {
		return dolibarrList;
	}

	/**
	 * @param dolibarrList the dolibarrList to set
	 */
	public void setDolibarrList(ArrayList<Lead> dolibarrList) {
		this.dolibarrList = dolibarrList;
	}

}
