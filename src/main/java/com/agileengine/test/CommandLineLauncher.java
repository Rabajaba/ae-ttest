package com.agileengine.test;

import com.agileengine.test.xml.XMLComparatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Entry point to Application from CommandLine
 */
@Slf4j
public class CommandLineLauncher {
    // TODO this should also come as an input from arguments
    private static final String MAKE_EVERYTHING_OK_BUTTON_ID = "make-everything-ok-button";

    public static void main(String[] args) throws Exception {
        // parse args
        Options options = new Options();

        Option input = new Option("o", "original", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("c", "compared", true, "compared file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        String originalFilePath = cmd.getOptionValue("original");
        String comparedFilePath = cmd.getOptionValue("compared");
        // parsed all the stuff, now launch
        launchSeeker(originalFilePath, comparedFilePath);
    }

    private static void launchSeeker(String originalFilePath, String comparedFilePath) throws IOException {
        XMLComparatorService service = new XMLComparatorService();
        Path originalFile = Paths.get(originalFilePath);
        if (!Files.exists(originalFile)) {
            throw new FileNotFoundException("File \"" + originalFilePath + "\" wasn't found!");
        }
        Path comparedFile = Paths.get(comparedFilePath);
        if (!Files.exists(comparedFile)) {
            throw new FileNotFoundException("File \"" + comparedFilePath + "\" wasn't found!");
        }
        long startTime = System.currentTimeMillis();
        Optional<String> result = service.seek(Files.newInputStream(originalFile), MAKE_EVERYTHING_OK_BUTTON_ID, Files.newInputStream(comparedFile));
        if (result.isPresent()) {
            log.info("Found similar element at following path: {}.", result.get());
            log.debug("Seek time is {}ms.", System.currentTimeMillis() - startTime);
        } else {
            log.info("No similar elements were found!");
        }
    }
}
