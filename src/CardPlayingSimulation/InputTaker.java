import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Interfaces.ICard;
import Interfaces.IInputTaker;
import Interfaces.IPackFileLoader;

public class InputTaker implements IInputTaker {
    
    //#region Methods

    @Override
    public int takeNumberOfPlayersInput() {
        
        var scanner = new Scanner(System.in);

        int numberOfPlayers;

        do {
            // Request user input for the number of players
            System.out.print("Please enter the number of players, at least 2: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                try {
                    scanner.next(); 
                }
                // Stops the program from crashing when a test doesnt give it continuous input stream
                catch (NoSuchElementException e) {
                    return -1;
                }
            }
            // Set the user input to the actual number of players
            numberOfPlayers = scanner.nextInt();
        } while (numberOfPlayers < 2);

        return numberOfPlayers;
    }

    @Override
    public ArrayList<ICard> takePackFileInput(IPackFileLoader packFileLoader, int numberOfPlayers) {
        
        var scanner = new Scanner(System.in); 

        // Set expected number of values in the input pack to 8 times the number of players
        var expectedNumberOfValues = numberOfPlayers * 8;

        ArrayList<ICard> cards;

        String packFileName = "";
        
        do {
            // Request user input for the location of the input pack
            System.out.print("Please enter location of pack to load: ");

            try {
                packFileName = scanner.nextLine(); 
            }
            // Stops program from crashing when a test doesnt give it continuous input stream.
            catch (NoSuchElementException e) {
                return null;
            }

            // Process the input pack file to create a list of cards
            cards = packFileLoader.processPackFile(packFileName, expectedNumberOfValues);

            if (cards != null) {
                break;
            }
        } while (cards == null);

        return cards;
    }

    //#endregion
}
