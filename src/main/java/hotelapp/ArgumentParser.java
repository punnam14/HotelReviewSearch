package hotelapp;
import java.util.HashMap;

/**
 * The ParseArgs class is responsible for parsing and processing command-line arguments.
 * It populates and returns HashMaps containing hotel and review directory paths, number of threads, and output paths.
 */
public class ArgumentParser {
    private final HashMap<String,String> parsedArgs = new HashMap<>();

    /**
     * Parses the command-line arguments and returns a HashMap containing parsed information.
     *
     * @param args Command-line arguments array
     * @return HashMap containing parsed hotel directory, review directory, number of threads, and output path.
     */
    public void parseArgs(String args[]) {
        String hotelDirectory = "";
        String reviewDirectory = "";
        String numThreads = "";
        if (args.length == 0) {
            System.out.println("Empty Arguments");
        } else if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-hotels")) {
                    hotelDirectory = args[i + 1];
                } else if (args[i].equals("-reviews")) {
                    reviewDirectory = args[i + 1];
                } else if (args[i].equals("-threads")) {
                    numThreads = args[i + 1];
                }
            }
        }
        parsedArgs.put("hotelDirectory", hotelDirectory);
        parsedArgs.put("reviewDirectory", reviewDirectory);
        parsedArgs.put("numThreads", numThreads);
    }

    public String getArgument(String keyArgument){
        return parsedArgs.get(keyArgument);
    }
}
