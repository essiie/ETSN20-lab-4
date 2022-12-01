import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/** Usage:            search <pattern> <file> [<options>]
 *  Possible options: -i  ignore case
 *                    -m num   stop reading the file after num matches
 *                    -n    each output line is preceded by its relative line number in the file, starting at line 1.
 *                    -v    inverted match, matches the lines where pattern is NOT present
 */
public class Searcher {
    public static String usageMessage = "Usage: java Searcher search <pattern> <file> [<options>]";
    private List<String> commands;
    //List initialization for shorter check if value exists in the list (compared to array)
    List<String> validCommands = Arrays.asList(new String[]{"-i", "-m", "-n", "-v"});
    //Only used if the -m command is used
    int numMatches;

    public Searcher(List<String> commands) {
        this.commands = commands;
        Iterator<String> it = commands.iterator();
        while(it.hasNext()) {
            String command = it.next();
            if(!validCommands.contains(command)) {
                System.out.println("Valid options: " + validCommands);
                System.exit(1);
            } else if(command.equals("-m")) {
            	try {
            		numMatches = Integer.parseInt(it.next());
            	} catch(NumberFormatException e) {
            		System.out.println(usageMessage);
            		System.exit(1);
            	}
            }
        }
    }

    private boolean isMatch(String line, String pattern) {
        if(commands.contains("-i")) {
            line = line.toLowerCase();
            pattern = pattern.toLowerCase();
        }
        if(commands.contains("-v")) {
            return(!line.contains(pattern));
        } else {
            return(line.contains(pattern));
        }
    }

    private void printMatches(List<String> matches) {
        for(int i = 0; i < matches.size(); i++) {
            if(commands.contains("-n")) {
                System.out.println(String.format("%d   %s", i+1, matches.get(i)));
            } else {
            	System.out.println(matches.get(i));
            }
        }
    }

    /** Returns a list of all lines in the file where the patterns occur. 
     * @throws FileNotFoundException */
    private List<String> search(String filename, String pattern) throws FileNotFoundException {
    	File file = new File(filename);
        Scanner scanner = new Scanner(file);
        List<String> matchingLines = new LinkedList<>();
        String line;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(isMatch(line, pattern)) { 
                matchingLines.add(line);
            }
            if(commands.contains("-m") && matchingLines.size() >= numMatches) break;
        }
        scanner.close();
        return matchingLines;
    }

    public static void main(String[] args) throws FileNotFoundException {
        if(args.length < 3) {
            System.out.println(usageMessage);
            System.exit(1);
        }
        String command = args[0];
        if(!command.equals("search")) {
            System.out.println(usageMessage);
            System.exit(1);
        }
        String pattern = args[1];
        String filename = args[2];
        List<String> commands = new LinkedList<>();
        for(int i = 3; i < args.length; i++) {
            commands.add(args[i]);
        }
        Searcher searcher = new Searcher(commands);
        List<String> matches = searcher.search(filename, pattern);
        searcher.printMatches(matches);
    }
}