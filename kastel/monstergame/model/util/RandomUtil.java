package edu.kit.kastel.monstergame.model.util;

import java.util.Random;
import java.util.Scanner;

/**
 * Utility class for generating random numbers.
 * @author uuifx
 */
public class RandomUtil {
    private final Random random;
    private final boolean inDebugMode;
    private final Scanner scanner;

    /**
     * Creates a new RandomUtil instance.
     *
     * @param seed The seed for the random number generator
     * @param debugMode Whether to run in debug mode with interactive prompts
     * @author uuifx
     */
    public RandomUtil(long seed, boolean debugMode) {
        this.random = new Random(seed);
        this.inDebugMode = debugMode;
        this.scanner = debugMode ? new Scanner(System.in) : null;
    }

    /**
     * Generates a boolean value with the given probability.
     * Used for critical hits, hit rates, and status condition endings.
     *
     * @param probability The probability of returning true (0-100)
     * @param decisionDescription Description for debug mode
     * @return true with the given probability
     * @author uuifx
     */
    public boolean rollChance(double probability, String decisionDescription) {
        if (inDebugMode) {
            System.out.printf("Decide %s: yes or no (y/n)? ", decisionDescription);
            String input = scanner.nextLine().trim().toLowerCase();
            while (!input.equals("y") && !input.equals("n") &&
                    !input.equals("yes") && !input.equals("no")) {
                System.out.println("Error, enter y or n.");
                System.out.printf("Decide %s: yes or no (y/n)? ", decisionDescription);
                input = scanner.nextLine().trim().toLowerCase();
            }
            return input.equals("y") || input.equals("yes");
        } else {
            return random.nextDouble() * 100 <= probability;
        }
    }

    /**
     * Generates a random double in the range.
     * Used for damage calculations with random factor.
     * @param min The minimum value
     * @param max The maximum value
     * @param decisionDescription Description for debug mode
     * @return A random double in the range
     * @author uuifx
     */
    public double getRandomDouble(double min, double max, String decisionDescription) {
        if (inDebugMode) {
            System.out.printf("Decide %s: a double between %.2f and %.2f? ",
                    decisionDescription, min, max);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < min || value >= max) {
                    System.out.println("Error, out of range.");
                    return getRandomDouble(min, max, decisionDescription);
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error, invalid number format.");
                return getRandomDouble(min, max, decisionDescription);
            }
        } else {
            return random.nextDouble(min, max);
        }
    }

    /**
     * Generates a random integer in the range.
     * Used for random repeat counts and protection durations.
     * @param min The minimum value
     * @param max The maximum value
     * @param decisionDescription Description for debug mode
     * @return A random integer in the range [min, max]
     * @author uuifx
     */
    public int getRandomInt(int min, int max, String decisionDescription) {
        if (inDebugMode) {
            System.out.printf("Decide %s: an integer between %d and %d? ",
                    decisionDescription, min, max);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.println("Error, out of range.");
                    return getRandomInt(min, max, decisionDescription);
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error, invalid number format.");
                return getRandomInt(min, max, decisionDescription);
            }
        } else {
            return random.nextInt(min, max + 1);
        }
    }
}