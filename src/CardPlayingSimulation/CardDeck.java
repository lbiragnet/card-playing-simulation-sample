import Interfaces.ICard;
import Interfaces.ICardDeck;
import Interfaces.IOutputFileWriter;

public class CardDeck extends CardHolder implements ICardDeck {

    //#region Attributes

    private final String outputFileName;
    private final int number;
    private final IOutputFileWriter outputFileWriter;

    //#endregion

    //#region Methods

    /**
     * Constructs an input pack.
     */
    public CardDeck() {
        this.number = 0;
        this.outputFileName = null;
        this.outputFileWriter = null;
    }

    /**
     * Constructs a regular deck.
     * @param number the number of the deck.
     * @param outputFileWriter the Output File Writer.
     */
    public CardDeck(int number, IOutputFileWriter outputFileWriter) {
        this.number = number;
        this.outputFileName = "deck" + number + "_output.txt";
        this.outputFileWriter = outputFileWriter;
        this.outputFileWriter.createOutputFile(outputFileName);
    }

    @Override
    public int getNumber() {
        // Return the number associated with the player
        return this.number;
    }

    @Override
    public ICard drawCard() {
        // Remove the top card of the deck and return it
        var card = cards.remove(0);
        return card;
    }

    @Override
    public void insertCard(ICard card) {
        // Add the given card to the bottom of the deck
        cards.add(card);
    }

    @Override
    public void writeContents() {
        // Write the contents of the deck (card denominations) to the output file
        outputFileWriter.writeLineToFile("deck" + this.number + " contents: " + getCardsString());
    }

    //#endregion
}