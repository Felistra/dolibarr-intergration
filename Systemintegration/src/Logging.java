import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
	
	static Handler fileHandler = null;
    private static final Logger LOGGER = Logger.getLogger(Logging.class.getClass().getName());
    
    public static void setup() {
    	boolean append = true;
        try {
            fileHandler = new FileHandler("./logfile.log", append);
            SimpleFormatter simple = new SimpleFormatter();
            fileHandler.setFormatter(simple);

            LOGGER.addHandler(fileHandler);

        } catch (IOException e) {
            LOGGER.severe("IOException in Logging.");
        }
    }
}