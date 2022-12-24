import Interfaces.ICard;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCardDeck {

    private static int card1Value;
    private static int card2Value;
    private static ICard card1;
    private static ICard card2;

    @BeforeClass
    public static void setupObjects() {
        // Set up Card objects used for testing
        card1Value = 4;
        card2Value = 5;
        card1 = new Card(card1Value);
        card2 = new Card(card2Value);
    }

    @Test
    public void testInsertCard() {
        var deck = new CardDeck();
        deck.insertCard(card1);

        var expectedResult1 = "4 ";
        var result1 = deck.getCardsString();

        assertEquals(expectedResult1, result1);

        deck.insertCard(card2);

        var expectedResult2 = "4 5 ";
        var result2 = deck.getCardsString();

        assertEquals(expectedResult2, result2);
    }

    @Test
    public void testDrawCard() {
        var deck = new CardDeck();
        deck.insertCard(card1);
        deck.insertCard(card2);

        assertEquals(card1, deck.drawCard());
        assertEquals(card2, deck.drawCard());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testDrawCardEmptyDeck() {
        var deck = new CardDeck();
        deck.drawCard();
        
        // Should raise IndexOutOfBoundsException
    }
}
