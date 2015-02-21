package it.polimi.hegira.command;

import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * @author Fabio Arcidiacono.
 */
public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("c", "clean", false, "clean datastore");
        options.addOption("a", "amount", true, "generate the given number of entities");

        if (args == null || args.length < 1 || args[0] == null) {
            printHelp(options);
            return;
        }

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("c")) {
                System.out.println("Clean datastore");
                Clean.clean();
            } else if (line.hasOption("a")) {
                int amount = Integer.valueOf(line.getOptionValue("a"));
                System.out.println("Generate " + amount + " entities per table");
                Generate g = new Generate(amount);
                g.generateAll();
            } else {
                printHelp(options);
            }
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount parameter. Reason: " + e.getMessage());
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(" ", options);
    }
}
