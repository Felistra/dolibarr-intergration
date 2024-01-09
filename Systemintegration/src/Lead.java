import java.util.ArrayList;
import java.util.logging.Logger;

public class Lead {
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());
	private String leadID;
	private String company;
	private String address;
	private String zipCode;
	private String city;
	private String contact;
	private String phoneNumber;
	private String size;
	private String currentProvider;
	private String email;
	private Status status;
	private ArrayList<String> faultyFields; 
	private String numRegex, alphaRegex;
	private static final int IS_EMPTY_MANDATORY_FIELD = 0;
	private static final int MAX_LENGTH_LARGE = 50;
	private static final int MAX_LENGTH_MEDIUM = 30;
	private static final int MAX_LENGTH_ZIP = 5;
	private static final int MAX_LENGTH_PHONE = 20;
	private static final int MAX_LENGTH_SIZE = 10;
	private static final int INTEGER_FOR_STATUS_CONTACTED = 4;
	private static final int INTEGER_FOR_STATUS_CUSTOMER = 5;
	private static final int INTEGER_FOR_STATUS_LEAD = 6;
	
	public Lead() {
		numRegex = ".*[0-9].*";
		alphaRegex = ".*[A-Z].*";
		faultyFields = new ArrayList<String>(); 
	}
	
	/**
	 * Method that verifies a lead when received from Webscraper. 
	 * All verification requirements are stated in the requirements list provided from Dynamic Commercials. 
	 * Inspiration about regex from: https://stackoverflow.com/questions/11533474/java-how-to-test-if-a-string-contains-both-letters-and-numbers.
	 * @return
	 * Returns true if a lead is approved. Returns false if a lead is not approved according to the mandatory fields. 
	 */
	public boolean verifyLead() {
		try {
			if(company.length() > MAX_LENGTH_LARGE || company.length() == IS_EMPTY_MANDATORY_FIELD) {
				faultyFields.add("company");
			}
			
			if(address.length() > MAX_LENGTH_MEDIUM || !address.matches(alphaRegex) || !(address.matches(numRegex))) { 
				faultyFields.add("address");
			}
			
			if(zipCode.length() > MAX_LENGTH_ZIP || !(zipCode.matches(numRegex))) { 
				faultyFields.add("zipcode");
			} 
			
			if(city.length() > MAX_LENGTH_MEDIUM || !(city.matches(alphaRegex))) { 
				faultyFields.add("city");
			} 
			
			if(contact.length() > MAX_LENGTH_LARGE || !(contact.matches(alphaRegex))  || contact.length() == IS_EMPTY_MANDATORY_FIELD) { 
				faultyFields.add("contact");
			} 
			
			if(phoneNumber.length() > MAX_LENGTH_PHONE || !(phoneNumber.matches(numRegex)) || phoneNumber.length() == IS_EMPTY_MANDATORY_FIELD) { 
				faultyFields.add("phonenumber");
			} 
			
			if(size.length() > MAX_LENGTH_SIZE || !(size.matches(numRegex))) { 
				faultyFields.add("size");
			} 
			
			if(currentProvider.length() > MAX_LENGTH_LARGE) {
				faultyFields.add("currentprovider");
			} 
			
			if(email.length() > MAX_LENGTH_LARGE || !(email.contains("@") && email.contains("."))) { 
				faultyFields.add("email");
			} 
			
			if(!getFaultyFields().isEmpty() && (getFaultyFields().contains("company") || getFaultyFields().contains("contact") || getFaultyFields().contains("phonenumber"))) {
				LOGGER.warning("Obligatoriska fält är felaktiga för lead ID: " + leadID);
				return false;
			}
			
			LOGGER.info("Obligatoriska fält är korrekta för lead med ID: " + leadID);
			return true;

		} catch (Exception e) {
			LOGGER.severe("Exception in Lead - verifyLead");
			return false;
		}

	}
	
	/**
	 * Method that converts the status number from the database to an enumeration status. 
	 * @param dolibarrStatus
	 * The number of the current status from the database - 4, 5 or 6. 
	 */
	public void checkStatus(int dolibarrStatus) {
		if(dolibarrStatus == INTEGER_FOR_STATUS_CONTACTED) {
			setStatus(Status.CONTACTED);
			
		} else if(dolibarrStatus == INTEGER_FOR_STATUS_CUSTOMER) {
			setStatus(Status.CUSTOMER);
			
		} else if(dolibarrStatus == INTEGER_FOR_STATUS_LEAD) {
			setStatus(Status.LEAD);
			
		} else {
			LOGGER.warning("Status could not be found.");
		}
	
	}
	
	/**
	 * Get method for receiving the list containing all the faulty fields after verification. 
	 * @return list of faulty fields for multiple leads. 
	 */
	public ArrayList<String> getFaultyFields() {
		return faultyFields;
	}
	
	/**
	 * Get method for receiving the lead ID for a lead. 
	 * @return leadID.
	 */
	public String getLeadID() {
		return leadID;
	}

	/**
	 * Set method for setting the incoming value of lead ID to leadID. 
	 * @param leadID
	 * Incoming leadID from Webscrapers API. 
	 */
	public void setLeadID(String leadID) {
		this.leadID = leadID;
	}

	/**
	 * Get method for receiving the company name for a lead. 
	 * @return company name. 
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * Set method for setting the incoming value of comapany name to company. 
	 * @param company
	 * Incoming company name from Webscrapers API.
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Get method for receiving the address for a lead.
	 * @return address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Set method for setting the incoming value of address to address.
	 * @param address
	 * Incoming address from Webscrapers API.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Get method for receiving the zipcode for a lead.
	 * @return zipcode. 
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Set method for setting the incoming value of zipcode to zipcode.
	 * @param zipCode
	 * Incoming zipcode from Webscrapers API.
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Get method for receiving the city for a lead.
	 * @return city. 
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Set method for setting the incoming value of city to city.
	 * @param city
	 * Incoming city from Webscrapers API.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Get method for receiving the name of contact for a lead.
	 * @return name of contact. 
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Set method for setting the incoming value of contact to contact.
	 * @param contact
	 * Incoming name of contact from Webscrapers API.
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * Get method for receiving the phonenumber for a lead.
	 * @return phonenumber.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Set method for setting the incoming value of phonenumber to phonenumber.
	 * @param phoneNumber
	 * Incoming phonenumber from Webscrapers API.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Get method for receiving the number of employees for a lead.
	 * @return number of employees. 
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Set method for setting the incoming value of number of employees to size.
	 * @param size
	 * Incoming value of number of employees from Webscrapers API.
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Get method for receiving the current provider, if any, for a lead.
	 * @return current provider.
	 */
	public String getCurrentProvider() {
		return currentProvider;
	}

	/**
	 * Set method for setting the incoming value of current provider to current provider.
	 * @param currentProvider
	 * Incoming value of current provider from Webscrapers API.
	 */
	public void setCurrentProvider(String currentProvider) {
		this.currentProvider = currentProvider;
	}

	/**
	 * Get method for receiving the email for a lead.
	 * @return email. 
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set method for setting the incoming value of email to email.
	 * @param email
	 * Incoming value of email from Webscrapers API.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get method for receiving the status for a lead.
	 * @return status. 
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set method for setting the status to status for a lead.
	 * @param status
	 * Incoming value of status from other functions. 
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
}