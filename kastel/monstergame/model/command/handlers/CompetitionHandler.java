package edu.kit.kastel.monstergame.model.command.handlers;

import edu.kit.kastel.monstergame.model.Monster;
import edu.kit.kastel.monstergame.model.combat.CombatSystem;
import edu.kit.kastel.monstergame.model.command.CommandHandler;
import edu.kit.kastel.monstergame.model.command.impl.ConsoleCommandInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles competition related commands and competition phase management.
 * @author uuifx
 */
public class CompetitionHandler {
    private final CommandHandler commandHandler;

    /**
     * Creates a new CompetitionHandler.
     * @param commandHandler The main command handler
     */
    public CompetitionHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Handles the competition command.
     * @param monsterNames Array of monster names for the competition
     */
    public void handleCompetition(String[] monsterNames) {
        if (monsterNames.length < 2) {
            System.out.println("Error: competition requires at least two monsters");
            return;
        }

        List<Monster> competitionMonsters = new ArrayList<>();
        Map<String, Integer> monsterCounts = new HashMap<>();

        for (String name : monsterNames) {
            boolean found = false;

            for (Monster monster : commandHandler.getGameData().getMonsters()) {
                if (monster.getName().equals(name)) {
                    found = true;

                    // Create a deep copy of the monster for the competition
                    Monster competitionMonster = createCompetitionMonster(monster, monsterCounts);
                    competitionMonsters.add(competitionMonster);
                    break;
                }
            }

            if (!found) {
                System.out.println("Error: unknown monster: " + name);
                return;
            }
        }

        // Initialize combat system with the selected monsters and the RandomUtil instance
        CombatSystem combatSystem = new CombatSystem(
                competitionMonsters,
                new ConsoleCommandInterface(commandHandler),
                commandHandler.isInDebugMode(),
                commandHandler.getRandomUtil());

        commandHandler.setCombatSystem(combatSystem);

        System.out.println("The " + competitionMonsters.size() + " monsters enter the competition!");

        // Starts the competition
        commandHandler.setInCompetition(true);

        // Move to Phase I
        startPhaseI();
    }

    /**
     * Creates a copy of a monster for competition, handling duplicate names.
     * @param monster The original monster
     * @param monsterCounts Map to track counts of each monsters name
     * @return A new Monster instance for the competition
     */
    private Monster createCompetitionMonster(Monster monster, Map<String, Integer> monsterCounts) {
        String name = monster.getName();

        // Check if this monster name appears multiple times
        monsterCounts.put(name, monsterCounts.getOrDefault(name, 0) + 1);
        int instanceNumber = monsterCounts.get(name);

        // If this is a duplicate monster name, add the #n suffix
        if (instanceNumber > 1) {
            return new Monster(
                    name + "#" + instanceNumber,
                    monster.getElement(),
                    monster.getBaseStats(),
                    monster.getActions());
        }

        return new Monster(
                name,
                monster.getElement(),
                monster.getBaseStats(),
                monster.getActions());
    }

    /**
     * Starts Phase I.
     */
    public void startPhaseI() {
        // Set current monster to active monster
        CombatSystem combatSystem = commandHandler.getCombatSystem();
        if (combatSystem != null) {
            Monster nextMonster = combatSystem.getNextMonsterForActionSelection();
            commandHandler.setCurrentMonster(nextMonster);
        }
    }

    /**
     * Moves to the next monster or phase after action selection.
     */
    public void nextMonsterOrPhase() {
        //Next monster
        CombatSystem combatSystem = commandHandler.getCombatSystem();
        if (combatSystem != null) {
            Monster nextMonster = combatSystem.getNextMonsterForActionSelection();
            commandHandler.setCurrentMonster(nextMonster);

            //move to Phase 2
            if (nextMonster == null) {
                startPhaseII();
            }
        }
    }

    /**
     * Starts Phase II of the competition.
     */
    public void startPhaseII() {
        CombatSystem combatSystem = commandHandler.getCombatSystem();
        if (combatSystem == null) {
            return;
        }

        // Execute all actions
        combatSystem.executeActionsPhase();

        // Check if the competition is over
        Monster winner = combatSystem.checkForWinner();
        if (winner != null) {
            handleCompetitionEnd(winner);
        } else {
            // Start a new round
            combatSystem.endOfRoundPhase();
            startPhaseI();
        }
    }

    /**
     * Handles the end of a competition.
     * @param winner The winning monster
     */
    private void handleCompetitionEnd(Monster winner) {
        System.out.println("\n=== Contest Results ===");
        if (winner != null) {
            System.out.println("Winner: " + winner.getName());
        } else {
            System.out.println("The contest ended in a draw!");
        }

        commandHandler.setInCompetition(false);
        commandHandler.setCurrentMonster(null);
    }
}