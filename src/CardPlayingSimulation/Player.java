import Interfaces.ICard;
import Interfaces.ICardDeck;
import Interfaces.IGameLoop;
import Interfaces.IOutputFileWriter;
import Interfaces.IPlayer;

public class Player extends CardHolder implements IPlayer  {

    //#region Attributes

    private final int number;
    private final String name;
    private final String outputFileName;
    private final ICardDeck drawDeck;
    private final ICardDeck discardDeck;
    private final IOutputFileWriter outputFileWriter;
    private IGameLoop gameLoop;
    
    //#endregion

    //#region Methods

    public Player(int number, ICardDeck drawDeck, ICardDeck discardDeck, IOutputFileWriter outputFileWriter) {
        this.number = number;
        this.name = "player " + number;
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.outputFileName =  "player" + number + "_output.txt";
        this.outputFileWriter = outputFileWriter;
    }

    @Override
    public void setGameLoop(IGameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addCardToHand(ICard card) {
        this.cards.add(card);
    }

    @Override
    public void playerHasWon(String winningPlayerName) {
        if (winningPlayerName != this.name) {
            outputFileWriter.writeLineToFile(winningPlayerName + " has informed " + this.name + " that " + winningPlayerName + " has won");
        }   
    }

    /**
     * The main loop to execute.
     */
    @Override
    public void run() {

        // Create player output file and write the player's initial hand
        outputFileWriter.createOutputFile(outputFileName);

        outputFileWriter.writeLineToFile(this.name + " initial hand: " + this.getCardsString());

        // Run the game loop
        playerGameLoop();        
    }

    private void playerGameLoop() {
        while (!Thread.currentThread().isInterrupted()) {

            // Check if the player has a winning hand
            if (hasWinningHand()) {
                outputFileWriter.writeLineToFile(this.name + " wins");
                gameLoop.playerHasWon(this);
                break;
            }

            try {
                // This delay cancels out the advantage the first created thread gets
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                // In case a player wins whilst it's sleeping, don't swallow the interrupt
                break;
            }

            takeTurn();
        }
        outputFileWriter.writeLineToFile(this.name + " exits");
        outputFileWriter.writeLineToFile(this.name + " final hand: " + this.getCardsString());
    }

    /**
     * Determines whether the player's hand will win the game.
     * @return a Boolean representing whether the player's hand will win the game.
     */
    private boolean hasWinningHand() {
        for (int i = 0; i < 3; i++) {
            if (this.cards.get(i).getDenomination() != this.cards.get(i+1).getDenomination()) {
                return false;
            } 
        }
        return true;
    }

    private void takeTurn() {
        // Draw, discard a card and write the player's hand to the output file
        drawCardToHand();  

        discardCard();

        outputFileWriter.writeLineToFile(this.name + " current hand is " + getCardsString());
    }

    private void drawCardToHand() {
        // Draw a card from the deck to the player's hand
        var card = drawDeck.drawCard();
        this.cards.add(card);
        outputFileWriter.writeLineToFile(this.name + " draws a " + card.getDenomination() + " from deck " + this.drawDeck.getNumber());
    }

    private void discardCard() {
        // Discard a card if it does not have the correct denomination
        for (ICard card : this.cards) {
            if (card.getDenomination() != this.number) {
                this.cards.remove(card);
                this.discardDeck.insertCard(card);
                outputFileWriter.writeLineToFile(this.name + " discards a " + card.getDenomination() + " to deck " + this.discardDeck.getNumber());
                return;
            }
        }
    }

    //#endregion
}