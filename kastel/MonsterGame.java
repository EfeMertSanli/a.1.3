package edu.kit.kastel;
import edu.kit.kastel.monstergame.model.command.CommandHandler;
import edu.kit.kastel.monstergame.model.util.FileParser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Main class for the Monster Game.
 * @author uuifx
 */
public final class MonsterGame {
    /**
     * Private constructor to prevent instantiation.
     */
    private MonsterGame() {
    }
    /**
     * Main method to start the game.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar MonsterCompetition.jar <config_file_path> [seed|debug]");
        }

        String configFilePath = args[0];

        // Handle optional seed parameter or debug flag
        boolean debugMode = false;

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("debug")) {
                debugMode = true;
                System.out.println("Debug mode enabled");
            }
        }

        try {
            java.nio.file.Path path = Paths.get(configFilePath);
            java.util.List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading config file: " + e.getMessage());
        }

        // Parse the config file from the path
        FileParser.GameData gameData = FileParser.parseFile(configFilePath);

        if (gameData.getMonsters().isEmpty() || gameData.getActions().isEmpty()) {
            System.out.println("Error: Invalid or empty configuration file.");
        }

        System.out.println();
        System.out.println("Loaded " + gameData.getActions().size() + " actions, "
                + gameData.getMonsters().size() + " monsters.");
        // Initialize command handler
        CommandHandler commandHandler = new CommandHandler(gameData, debugMode);
        commandHandler.start();
    }
}