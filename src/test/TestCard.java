import org.junit.Test;
import static org.junit.Assert.*;

public class TestCard {

    @Test
    public void testGetDenomination() {
        int value = 4;
        Card card = new Card(value);
        assertEquals(value, card.getDenomination());
    }
}
