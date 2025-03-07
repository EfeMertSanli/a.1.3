package edu.kit.kastel.monstergame.model.util;

import edu.kit.kastel.monstergame.model.Action;
import edu.kit.kastel.monstergame.model.Monster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing configuration files containing monsters and actions.
 * Reads and processes text files to create game objects.
 *
 * @author uuifx
 */
public final class FileParser {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileParser() {
        // Private constructor to prevent instantiation
    }

    /**
     * Container class for game data loaded from a configuration file.
     * Stores lists of monsters and actions.
     */
    public static class GameData {
        private final List<Monster> monsters;
        private final List<Action> actions;
        private final Map<String, Action> actionMap;

        /**
         * Creates a new GameData object with the specified monsters and actions.
         *
         * @param monsters The list of monsters
         * @param actions The list of actions
         */
        public GameData(List<Monster> monsters, List<Action> actions) {
            this.monsters = monsters;
            this.actions = actions;
            this.actionMap = new HashMap<>();
            for (Action action : actions) {
                actionMap.put(action.getName(), action);
            }
        }

        /**
         * Gets the list of monsters.
         *
         * @return The list of monsters
         */
        public List<Monster> getMonsters() {
            return monsters;
        }

        /**
         * Gets the list of actions.
         *
         * @return The list of actions
         */
        public List<Action> getActions() {
            return actions;
        }

        /**
         * Gets an action by its name.
         *
         * @param name The name of the action to retrieve
         * @return The action, or null if not found
         */
        public Action getActionByName(String name) {
            return actionMap.get(name);
        }
    }

    /**
     * Parses a configuration file and creates game data.
     *
     * @param filename The path to the configuration file
     * @return A GameData object containing the parsed monsters and actions
     */
    public static GameData parseFile(String filename) {
        Map<String, Action> actionsMap = new HashMap<>();
        List<Monster> monsters = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            parseFileContent(reader, actionsMap, monsters);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new GameData(new ArrayList<>(), new ArrayList<>());
        } finally {
            closeReader(reader);
        }

        List<Action> actions = new ArrayList<>(actionsMap.values());
        return new GameData(monsters, actions);
    }

    /**
     * Parses the content of a file.
     *
     * @param reader The reader to read from
     * @param actionsMap The map to store parsed actions
     * @param monsters The list to store parsed monsters
     * @throws IOException If an I/O error occurs
     */
    private static void parseFileContent(BufferedReader reader, Map<String, Action> actionsMap,
                                         List<Monster> monsters) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("action ")) {
                Action action = ActionParser.parseAction(line, reader);
                if (action != null) {
                    actionsMap.put(action.getName(), action);
                }
            } else if (line.startsWith("monster ")) {
                Monster monster = MonsterParser.parseMonster(line, actionsMap);
                if (monster != null) {
                    monsters.add(monster);
                }
            }
        }
    }

    /**
     * Closes the reader safely.
     *
     * @param reader The reader to close
     */
    private static void closeReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }
    }
}