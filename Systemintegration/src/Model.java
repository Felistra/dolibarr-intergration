import java.util.ArrayList;
import java.util.logging.Logger;

public class Model {
	private Leads leads;
	private ArrayList<Lead> deleteList;
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());

	public Model() {
		leads = new Leads(); 
		deleteList = new ArrayList<Lead>();
	}

	/**
	 * Calls functions that get data from Webscraper and calls functions that check for duplicates in dolibarrList.
	 * Method also calls method that check previous list, leads that will be deleted and updating webscraperList.
	 */
	public void handleFunction(ArrayList<Lead> dolibarrList) { 
		leads.getLeadsFromWebScraper();
		leads.checkForDuplicates(dolibarrList);
		leads.checkPrevList(dolibarrList);
		deleteList = leads.removeLeads(dolibarrList);
		leads.updateWebscraperList(dolibarrList);
				
		for(Lead ll : leads.getWebscraperList()) { 
			LOGGER.info("LEAD ID: " + ll.getLeadID());
			LOGGER.info("COMPANY: " + ll.getCompany());
			LOGGER.info("ADDRESS: " + ll.getAddress());
			LOGGER.info("ZIPCODE: " + ll.getZipCode());
			LOGGER.info("CITY: " + ll.getCity());
			LOGGER.info("CONTACT: " + ll.getContact());
			LOGGER.info("PHONENUMBER: " + ll.getPhoneNumber());
			LOGGER.info("SIZE: " + ll.getSize());
			LOGGER.info("CURRENT PROVIDER: " + ll.getCurrentProvider());
			LOGGER.info("EMAIL: " + ll.getEmail());
			LOGGER.info("----------------- " + "\n");
		}
		LOGGER.info("Antal ej godkända leads: " + leads.getNbrOfFaultyLeads());
		LOGGER.info("Antal godkända leads: " + leads.getNbrOfCorrectLeads());
	}
	
	/**
	 * Method that returns the list from Webscraper
	 * @return list with leads from Webscraper
	 */
	public ArrayList<Lead> getWebscraperList() {
		return leads.getWebscraperList();
	}
	
	/**
	 * Method that returns the list containing leads that will be deleted from Dolibarr.
	 * @return list containing items that will be deleted. 
	 */
	public ArrayList<Lead> getDeleteList() {
		return deleteList;
	}
}
