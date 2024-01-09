
public class Controller {
	private Model model;
	private XMLHandler xmlHandler;
	
	/**
	 * An instance of Model and XMLHandler is created
	 * Calls handleFunction with model reference to create a new list with leads from Webscraper, check for duplicates from previous list and update leads 
	 * The first time the integration is run, an if statement is read to see if there are posts to be deleted 
	 * Calls function importLeadsToDolibarr to import the leadlist
	 */
	public Controller() {
		model = new Model(); 
		xmlHandler = new XMLHandler(); 
		model.handleFunction(xmlHandler.getDolibarrList()); 
		
		if(!model.getDeleteList().isEmpty()) {
			xmlHandler.deleteFromDolibarr(model.getDeleteList());
		}
		
		xmlHandler.importLeadsToDolibarr(model.getWebscraperList());
	}
}