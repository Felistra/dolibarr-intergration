import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.activation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Leads {
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());
	private ArrayList<Lead> webscraperList, deleteList;
	private ArrayList<String> duplicateList;
	private int nbrOfFaultyLeads, nbrOfCorrectLeads;
	private Status status;
	private boolean prevListFlag;
	
	/**
	 * Constructor for Leads.
	 * Instantiates the list that will contain the leads from Webscraper, the counters for number of correct and incorrect leads and a bool flag.
	 */
	public Leads() {
		webscraperList = new ArrayList<Lead>(); 
		deleteList = new ArrayList<Lead>(); 
		this.nbrOfFaultyLeads = 0; 
		this.nbrOfCorrectLeads = 0;
		prevListFlag = false;
		//sendEmail(); 
	}
	
	/**
	 * Inspiration from: https://www.tutorialspoint.com/java/java_sending_email.htm
	 */
	/*public void sendEmail() {
		String emailTo1 = "felistra102@student.kau.se";
		String emailTo2 = "lucke111222@gmail.com";
		
		String emailFrom = "web@gmail.com";
		String host = "localhost";
		
		Properties properties = System.getProperties();
		
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(properties);
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailFrom));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo1));
			message.setSubject("Fel vid hämtning");
			message.setText("En liten text");
			Transport.send(message);
			System.out.println("Meddalenadet har skickats.");
			
		} catch(MessagingException mex) {
			mex.printStackTrace();
		}
	}*/
	
	/**
	 * Get method for receiving number of correct leads after verifying a lead.
	 * @return number of correct leads after being verified. 
	 */
	public int getNbrOfCorrectLeads() {
		return nbrOfCorrectLeads;
	}
	
	/**
	 * Set method for setting the number of correct leads. 
	 * Method will set number of correct leads with +1 for every call to it. 
	 */
	public void setNbrOfCorrectLeads() {
		this.nbrOfCorrectLeads++;
	}
	
	/**
	 * Get method for receiving number of faulty leads after verifying a lead.
	 * @return number of faulty leads after being verified. 
	 */
	public int getNbrOfFaultyLeads() {
		return this.nbrOfFaultyLeads;
	}
	
	/**
	 * Set method for setting the number of faulty leads. 
	 * Method will set number of faulty leads with +1 for every call to it. 
	 */
	public void setNbrOfFaultyLeads() {
		this.nbrOfFaultyLeads++;
	}
	
	/**
	 * Get method for receiving the list that contains leads from Webscraper. 
	 * @return list of leads from Webscraper. 
	 */
	public ArrayList<Lead> getWebscraperList() {
		return webscraperList;
	}
	
	/**
	 * Method that creates an instance of class Lead and add one lead at a time to the list webscraperList if ld.verifyLead = true. 
	 * @param leadElement
	 * Every lead is in parameter leadElement
	 */
	public void addLeads(Element leadElement) {
		Lead ld = new Lead();
		ld.setLeadID(leadElement.getAttribute("id"));
		ld.setCompany(leadElement.getElementsByTagName("name").item(0).getTextContent()); 
		ld.setAddress(leadElement.getElementsByTagName("address").item(0).getTextContent());
		ld.setZipCode(leadElement.getElementsByTagName("zip").item(0).getTextContent());
		ld.setCity(leadElement.getElementsByTagName("city").item(0).getTextContent());
		ld.setContact(leadElement.getElementsByTagName("contact").item(0).getTextContent());
		ld.setPhoneNumber(leadElement.getElementsByTagName("tele").item(0).getTextContent());
		ld.setSize(leadElement.getElementsByTagName("size").item(0).getTextContent());
		ld.setCurrentProvider(leadElement.getElementsByTagName("current_provider").item(0).getTextContent()); 
		ld.setEmail(leadElement.getElementsByTagName("email").item(0).getTextContent()); 
		ld.setStatus(status);
		if(ld.verifyLead()) {
			webscraperList.add(ld);
			for(int i = 0; i < ld.getFaultyFields().size(); i++) {
				LOGGER.warning("Felaktigt fält är " + ld.getFaultyFields().get(i) + " på lead med ID: " + ld.getLeadID());
			}
			setNbrOfCorrectLeads(); 
		} else {
			for(int i = 0; i < ld.getFaultyFields().size(); i++) {
				LOGGER.warning("Felaktigt fält är  " + ld.getFaultyFields().get(i) + " på lead med ID: " + ld.getLeadID());
			}
			setNbrOfFaultyLeads();
		}
	}
	
	/**
	 * Solution for API call inspired by https://solutions.posit.co/connections/clients/java/.
	 * Also inspired from: https://mkyong.com/java/java-properties-file-examples/.
	 * Method that opens a connection to Webscraper API to get access to new leads.
	 */
	public void getLeadsFromWebScraper() {
		try {
			
			InputStream input = Leads.class.getClassLoader().getResourceAsStream("config_local.properties");
			Properties prop = new Properties(); 
			
			prop.load(input);
			
			String CONNECT_API_KEY = prop.getProperty("ws.api.key"); 
			String CONNECT_API_URL = prop.getProperty("ws.api.url"); 
			
			URL url = new URL(CONNECT_API_URL);
			URLConnection urlc = url.openConnection();
			urlc.setRequestProperty("Authorization", "Bearer " + CONNECT_API_KEY);
			
			InputStreamReader inputStreamReader = new InputStreamReader(urlc.getInputStream());
			BufferedReader br = new BufferedReader(inputStreamReader);
			
			String l = null;
			while ((l=br.readLine())!=null) {
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		        Document doc = dBuilder.parse(new InputSource(new StringReader(l)));
		        
		        NodeList nodeLeadList = doc.getElementsByTagName("lead");

		        for (int i = 0; i < nodeLeadList.getLength(); i++) {
		            Node leadNode = nodeLeadList.item(i);
		            
		            if (leadNode.getNodeType() == Node.ELEMENT_NODE) {
		                Element leadElement = (Element) leadNode;
		                addLeads(leadElement);
		                LOGGER.info("API call to Webscraper was successful and leads was successfully retrieved.");
		            }
		        }
	        }

	        br.close();
	        inputStreamReader.close();
	        
		} catch(RuntimeException e) {
			LOGGER.severe("RuntimeException in Leads - getLeadsFromWebScraper");
		} catch (MalformedURLException e) {
			LOGGER.severe("MalformedURLException in Leads - getLeadsFromWebScraper");
		} catch (Exception e) {
			LOGGER.severe("Exception in Leads - getLeadsFromWebScraper");
		} 
	}	

	/**
	 * Method that remove leads with the status LEAD. 
	 * @param dolibarrList 
	 * dolibarrList contains the "old" leads from Dolibarr that might have been modified by Dynamic Commercials staff.
	 */
	public ArrayList<Lead> removeLeads(ArrayList<Lead> dolibarrList) {
		for(ListIterator<Lead> iterator = dolibarrList.listIterator(); iterator.hasNext();) {
			if(iterator.next().getStatus() == Status.LEAD) {
				deleteList.add(iterator.previous());
				iterator.remove();
			}
		}
		
		for(int i = 0; i < deleteList.size(); i++) {
			LOGGER.info("LEAD SOM RADERAS: "+ deleteList.get(i).getCompany());
		}
		
		return deleteList;		
	}
	
	/**
	 * Method that updates webscraperList that will be imported to Dolibarr.
	 * For each duplicate, if the duplicate has the status Customer or Contacted, the method assures that the "old" lead that is already in the database will be used and not overwritten. 
	 */
	public void updateWebscraperList(ArrayList<Lead> dolibarrList) {
		if(!duplicateList.isEmpty()) {
			for(Lead leadFromDolibarr : dolibarrList) {
				if(leadFromDolibarr.getStatus() == Status.CONTACTED || leadFromDolibarr.getStatus() == Status.CUSTOMER) {
					if(duplicateList.contains(leadFromDolibarr.getPhoneNumber())) {
						loopWebscraperList(leadFromDolibarr); 
					}
				}
			}
		}
	}
	
	/**
	 * Method that iterates through webscraperList so that the phonenumber for a lead is the same as the phonenumber in the duplicateList.
	 * If the phonenumber is the same, the lead is removed from webscraperList. 
	 * @param leadFromDolibarr
	 * Current lead in the loop.
	 */
	public void loopWebscraperList(Lead leadFromDolibarr) {
		Iterator<Lead> iterator = webscraperList.iterator();
        while (iterator.hasNext()) {
            Lead lead = iterator.next();
            if (lead.getPhoneNumber().equals(leadFromDolibarr.getPhoneNumber())) {
            	iterator.remove();
            }
        }
	}
	
	/**
	 * Method that checks for duplicates of leads. 
	 * If there are duplicates, they are added to list.
	 * @param dolibarrList
	 * The list of "old" leads from Dolibarr.
	 */
	public void checkForDuplicates(ArrayList<Lead> dolibarrList) {
		duplicateList = new ArrayList<String>();
		ArrayList<String> webscraperPhoneNumber= new ArrayList<String>();
		ArrayList<String> dolibarrPhoneNumber = new ArrayList<String>();

		for(int i = 0; i < dolibarrList.size(); i++) {
			dolibarrPhoneNumber.add(dolibarrList.get(i).getPhoneNumber());
		}
		
		for(int i = 0; i < webscraperList.size(); i++) {
			webscraperPhoneNumber.add(webscraperList.get(i).getPhoneNumber());
		}

        for (String phoneNumber : webscraperPhoneNumber) {
            if (dolibarrPhoneNumber.contains(phoneNumber)) {
                	duplicateList.add(phoneNumber);
            }
        }
        
        if(!duplicateList.isEmpty()) {
        	LOGGER.warning("Duplicates in list: " + duplicateList + "\n");
        }
	}
	
	/**
	 * Method that checks previous list.
	 * Method is used to assure that Webscraper is not providing a copy of a previous list.
	 * @param webscraperList
	 * The list that should contain new leads.
	 * @param dolibarrList
	 * The list that contains of "old" leads from Dolibarr.
	 */
	public void checkPrevList(ArrayList<Lead> dolibarrList) {
		for(int i = 0; i < dolibarrList.size(); i++) {
			for(int j = 0; j < webscraperList.size(); j++)  {
				if(
					dolibarrList.get(i).getCompany().contains(webscraperList.get(j).getCompany()) &&
					dolibarrList.get(i).getAddress().contains(webscraperList.get(j).getAddress()) &&
					dolibarrList.get(i).getZipCode().contains(webscraperList.get(j).getZipCode()) &&
					dolibarrList.get(i).getCity().contains(webscraperList.get(j).getCity()) &&
					dolibarrList.get(i).getContact().contains(webscraperList.get(j).getContact()) &&
					dolibarrList.get(i).getPhoneNumber().contains(webscraperList.get(j).getPhoneNumber()) &&
					dolibarrList.get(i).getSize().contains(webscraperList.get(j).getSize()) &&
					dolibarrList.get(i).getCurrentProvider().contains(webscraperList.get(j).getCurrentProvider()) &&
					dolibarrList.get(i).getEmail().contains(webscraperList.get(j).getEmail())) 
				{
					prevListFlag = true;
				}
			}
		}
		if(prevListFlag) {
			LOGGER.warning("Identical list from last week is being used. Contact WebScraper.");
		}
	}
}