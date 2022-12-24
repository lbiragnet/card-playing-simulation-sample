import java.util.ArrayList;

import Interfaces.ICard;
import Interfaces.ICardDeck;
import Interfaces.ICardDistributor;
import Interfaces.IPlayer;

public class CardDistributor implements ICardDistributor {

    //#region Methods

    @Override
    public void drawInitialHands(ArrayList<ICard> inputPack, ArrayList<IPlayer> players) {
        int playerIndex = 0;
        int numberOfCards = inputPack.size();
        // Hand out half the input pack to give each player 4 cards
        for (int i = 0; i < numberOfCards/2; i++) {
            // Make sure the cards are distributed in round-robin fashion
            if (playerIndex > players.size()-1) {
                playerIndex = 0;
            }
            // Add the card to relevant player's hand and remove it from the input pack
            players.get(playerIndex).addCardToHand(inputPack.get(0));
            inputPack.remove(0);
            playerIndex += 1;
        }
    }

    @Override
    public void populateDecks(ArrayList<ICard> inputPack, ArrayList<ICardDeck> decks) {
        int deckIndex = 0;
        int numberOfCards = inputPack.size();
        // Hand out the rest of the input pack to give each deck 4 cards
        for (int i = 0; i < numberOfCards; i++) {
            // Make sure the cards are distributed in round-robin fashion
            if (deckIndex > decks.size()-1) {
                deckIndex = 0;
            }
            // Add the card to the relevant deck and remove it from the input pack
            decks.get(deckIndex).insertCard(inputPack.get(0));
            inputPack.remove(0);
            deckIndex += 1;
        }   
    }

    //#endregion
}
