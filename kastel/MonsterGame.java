package edu.kit.kastel;
import edu.kit.kastel.monstergame.model.command.CommandHandler;
import edu.kit.kastel.monstergame.model.util.FileParser;
import edu.kit.kastel.monstergame.model.util.RandomUtil;

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
            return;
        }

        String configFilePath = args[0];
        boolean debugMode = false;
        long seed = 0; // Default seed

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("debug")) {
                debugMode = true;
            } else {
                try {
                    seed = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid seed value. Using default seed.");
                }
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
            return;
        }

        // Create a single RandomUtil instance with the provided seed
        RandomUtil randomUtil = new RandomUtil(seed, debugMode);

        // Parse the config file from the path
        FileParser.GameData gameData = FileParser.parseFile(configFilePath);

        if (gameData.getMonsters().isEmpty() || gameData.getActions().isEmpty()) {
            System.out.println("Error: Invalid or empty configuration file.");
            return;
        }

        System.out.println();
        System.out.println("Loaded " + gameData.getActions().size() + " actions, "
                + gameData.getMonsters().size() + " monsters.");

        // Initialize command handler with the randomUtil instance
        CommandHandler commandHandler = new CommandHandler(gameData, debugMode, randomUtil);
        commandHandler.start();
    }
}