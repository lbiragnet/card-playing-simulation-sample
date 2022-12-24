import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Interfaces.ICard;
import Interfaces.IPackFileLoader;

public class PackFileLoader implements IPackFileLoader {

    //#region Methods

    @Override
    public ArrayList<ICard> processPackFile(String packFileName, int expectedNumberOfValues) {
        // Load the pack file
        var packFileContents = loadPackFile(packFileName);
        if (packFileContents == null) {
            return null;
        }
        else if (packFileContents.size() != expectedNumberOfValues) {
            // Check if the pack file contains the right number of cards
            System.out.println("Incorrect number of values in the pack. Please ensure there are eight times as many values as players.");
            return null;
        }
        else {
            // Process the contents of the pack file
            return processPackFileContents(packFileContents);
        }        
    }

    /**
     * Loads a pack file and returns its contents.
     * @param packFileName the file path/name of the pack file to load.
     * @return a list of the values in the file, or null if the file is invalid.
     */
    private List<String> loadPackFile(String packFileName) {
        try {
            // Read the contents of the input pack file
            var packFile = new BufferedReader(new FileReader(packFileName));
            var packFileContents = packFile.lines().toList();

            try {
                packFile.close();
            }
            catch (IOException e) {
                System.out.println("Error when reading pack file: " + e.getMessage());
                return null;
            }

            // Return the contents of the pack file
            return packFileContents;
        }

        catch (FileNotFoundException e) {
            System.out.println("Sorry, the specified file doesn't exist.");
            return null;
        }
        
    }

    /**
     * Iterates over the list of values in a pack file and generates a {@link Card} for each one.
     * @param packFileContents the list of values from the pack file.
     * @return a list of the generated {@link Card Cards}, or null if the contents is invalid.
     */
    private ArrayList<ICard> processPackFileContents(List<String> packFileContents) {
        
        var cards = new ArrayList<ICard>();

        for (String line : packFileContents) {
            try {
                // Create a list of cards from the input pack file
                var value = Integer.parseInt(line);
                var card = new Card(value);
                cards.add(card);
            }
            catch (NumberFormatException e) {
                System.out.println("The contents of your pack file isn't valid.");
                return null;
            }
        }

        return cards;
    }

    //#endregion
}