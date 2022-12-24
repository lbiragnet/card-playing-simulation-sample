import Interfaces.IPackFileLoader;
import Interfaces.IPlayer;

import java.util.ArrayList;

import Interfaces.ICardDeck;
import Interfaces.ICardDistributor;
import Interfaces.IGameElements;
import Interfaces.IGameSetup;
import Interfaces.IInputTaker;

public class GameSetup implements IGameSetup {
    
    //#region Attributes

    private final IInputTaker inputTaker;
    private final IPackFileLoader packFileLoader;
    private final ICardDistributor cardDistributor;

    //#endregion
    
    //#region Methods

    public GameSetup(IInputTaker inputTaker, IPackFileLoader packFileLoader, ICardDistributor cardDistributor) {
        this.inputTaker = inputTaker;
        this.packFileLoader = packFileLoader;
        this.cardDistributor = cardDistributor;
    }

    @Override
    public IGameElements setupGame() {

        // Get the number of players and the input pack from the user input
        var numberOfPlayers = inputTaker.takeNumberOfPlayersInput();

        var inputPack = inputTaker.takePackFileInput(packFileLoader, numberOfPlayers);

        // Generate an empty deck for each player
        var decks = new ArrayList<ICardDeck>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            var deck = new CardDeck(i, new OutputFileWriter());
            decks.add(deck);
        }

        // Generate players and assign them the relevant (empty) decks
        var players = new ArrayList<IPlayer>();
        for (int i = 0; i < numberOfPlayers; i++) {
            var discardDeckIndex = (i+1 >= numberOfPlayers) ? 0 : i+1;
            
            // Create each player with its relevant draw and discard decks
            var player = new Player(i+1, decks.get(i), decks.get(discardDeckIndex), new OutputFileWriter());
            players.add(player);
        }

        // Draw the players' initial hands and fill the initial decks
        cardDistributor.drawInitialHands(inputPack, players);

        cardDistributor.populateDecks(inputPack, decks);

        return new GameElements(players, decks);
    }

    //#endregion
}
