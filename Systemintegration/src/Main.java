import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Method that executes the integration.
 * Logging method setup() is called to create a log file. 
 * Inspiration for logging from: https://stackoverflow.com/questions/44708680/how-to-get-the-java-default-logger-output-to-a-file
 * Inspiration on how to append log messages to one single log file: https://stackoverflow.com/questions/23698156/how-can-i-append-to-log-files-in-this-simple-java-logging-implementation
 * Inspiration for timer: https://stackoverflow.com/questions/14837568/how-do-i-specify-time-in-for-every-1-00-am-of-friday-in-milliseconds-java-for
 * An instance of class Controller is created.
 */
public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());
	
	public static void main(String[] args) {
		/*InputStream input = XMLHandler.class.getClassLoader().getResourceAsStream("config_local.properties");
		Properties prop = new Properties(); 

		try {
			prop.load(input);
		} catch (IOException e) {
			LOGGER.warning("Could not load config details.");
		}
		
		Calendar date = Calendar.getInstance();
		Timer timer = new Timer();
		
		date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(prop.getProperty("time.hour")));
		date.set(Calendar.MINUTE, Integer.parseInt(prop.getProperty("time.minute")));
		date.set(Calendar.SECOND, Integer.parseInt(prop.getProperty("time.second")));
		date.set(Calendar.MILLISECOND, Integer.parseInt(prop.getProperty("time.millisecond")));
		date.set(Calendar.DAY_OF_WEEK, Integer.parseInt(prop.getProperty("time.day")));
		
		if (date.before(Calendar.getInstance())) {
			date.add(Calendar.DAY_OF_YEAR, 7);
	    }
		
		long delay = date.getTimeInMillis() - new Date().getTime();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Logging.setup();
				LOGGER.info("Start of program");
				new Controller(); 
				LOGGER.info("End of program");
			}
		}, delay, 7*24*60*60*1000);*/
		Logging.setup();
		LOGGER.info("Start of program");
		new Controller(); 
		LOGGER.info("End of program");
	}
}