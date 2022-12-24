import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import Interfaces.ICard;
import Interfaces.ICardDeck;
import Interfaces.IGameLoop;
import Interfaces.IPlayer;
import Interfaces.IOutputFileWriter;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.*;

public class TestPlayer {

    private static ICardDeck mockDrawDeck;
    private static ICardDeck mockDiscardDeck;
    private static IOutputFileWriter mockOutputFileWriter;
    private static IGameLoop mockGameLoop;

    private static Class<? extends Player> playerClass = Player.class;
    private static Method drawMethod;
    private static Method discardMethod;
    private static Method hasWinningHandMethod;
    private static Method takeTurnMethod;
    private static Method gameLoopMethod;

    private final InputStream stdSystemIn = System.in;
    private final PrintStream stdSystemOut = System.out;

    private ByteArrayOutputStream testOut;

    //#region Setup

    @BeforeClass
    public static void setupReflection() throws Exception {
        drawMethod = playerClass.getDeclaredMethod("drawCardToHand");
        drawMethod.setAccessible(true);

        discardMethod = playerClass.getDeclaredMethod("discardCard");
        discardMethod.setAccessible(true);

        hasWinningHandMethod = playerClass.getDeclaredMethod("hasWinningHand");
        hasWinningHandMethod.setAccessible(true);

        takeTurnMethod = playerClass.getDeclaredMethod("takeTurn");
        takeTurnMethod.setAccessible(true);

        gameLoopMethod = playerClass.getDeclaredMethod("playerGameLoop");
        gameLoopMethod.setAccessible(true);
    }

    @Before
    public void setupMocks() {
        mockDrawDeck = new ICardDeck() {
            @Override
            public int getNumber() {
                return 1;
            }

            @Override
            public void writeContents() {}

            @Override
            public void insertCard(ICard card) {}

            @Override
            public ICard drawCard() {
                return new Card(1);
            }
        };

        mockDiscardDeck = new ICardDeck() {
            @Override
            public int getNumber() {
                return 2;
            }

            @Override
            public void writeContents() {}

            @Override
            public void insertCard(ICard card) {}

            @Override
            public ICard drawCard() {
                return null;
            }
        };

        mockOutputFileWriter = new IOutputFileWriter() {
            @Override
            public boolean writeLineToFile(String line) {
                System.out.println(line);
                return true;
            }

            @Override
            public boolean createOutputFile(String fileName) {return true;}
        };

        mockGameLoop = new IGameLoop() {
            @Override
            public void playerHasWon(IPlayer winningPlayer) {}

            @Override
            public void runGameLoop() {}
        };
    }

    @Before
    public void setupOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private String getOutput() {
        return testOut.toString();
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(stdSystemIn);
        System.setOut(stdSystemOut);
    }

    //#endregion

    @Test
    public void testSetGameLoop() throws Exception {
        var player = new Player(1, null, null, null);

        player.setGameLoop(mockGameLoop);

        var gameLoopField = playerClass.getDeclaredField("gameLoop");
        gameLoopField.setAccessible(true);

        var result = gameLoopField.get(player);

        assertEquals(mockGameLoop, result);
    }

    @Test
    public void testGetName() {
        var player = new Player(1, null, null, null);

        var expectedResult = "player 1";

        var result = player.getName();
        
        assertEquals(expectedResult, result);
    }

    @Test
    public void testAddCardToHand() throws Exception {
        var player = new Player(1, null, null, null);

        var expectedFirstResult = "1 ";
        var expectedSecondResult = "1 2 ";

        player.addCardToHand(new Card(1));

        var firstResult = player.getCardsString();

        player.addCardToHand(new Card(2));

        var secondResult = player.getCardsString();

        assertEquals(expectedFirstResult, firstResult);
        assertEquals(expectedSecondResult, secondResult);
    }

    //#region Player Has Won

    @Test
    public void testPlayerHasWonThis() {
        var player = new Player(1, null, null, mockOutputFileWriter);

        var playerName = player.getName();

        player.playerHasWon(playerName);

        var expectedResult = "";

        var result = getOutput().trim(); 
        
        assertEquals(expectedResult, result);
    }

    @Test
    public void testPlayerHasWonOther() {
        var player = new Player(1, null, null, mockOutputFileWriter);

        var playerName = player.getName();

        var winningPlayerName = "player 2";

        player.playerHasWon(winningPlayerName);

        var expectedResult = winningPlayerName + " has informed " + playerName + " that " + winningPlayerName + " has won";

        var result = getOutput().trim(); 
        
        assertEquals(expectedResult, result);
    }

    //#endregion

    @Test
    public void testPlayerGameLoopImmediateWin() throws Exception {
        var player = new Player(1, null, null, mockOutputFileWriter);

        for (int i = 0; i < 4; i++) {
            Card newCard = new Card(1);
            player.addCardToHand(newCard);
        }

        var playerName = player.getName();

        var expectedOutput = playerName + " wins\r\n" + playerName + " exits\r\n" + playerName + " final hand: 1 1 1 1";

        player.setGameLoop(mockGameLoop);

        gameLoopMethod.invoke(player);

        var output = getOutput().trim();

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testPlayerGameLoopNaturalWin() throws Exception {
        var player = new Player(1, mockDrawDeck, mockDiscardDeck, mockOutputFileWriter);

        for (int i = 0; i < 3; i++) {
            Card newCard = new Card(1);
            player.addCardToHand(newCard);
        }
        player.addCardToHand(new Card(2));

        var playerName = player.getName();

        var expectedOutput = "player 1 draws a 1 from deck 1\r\n"
                            + "player 1 discards a 2 to deck 2\r\n"
                            + "player 1 current hand is 1 1 1 1 \r\n"
                            + playerName + " wins\r\n" + playerName + " exits\r\n" + playerName + " final hand: 1 1 1 1";

        player.setGameLoop(mockGameLoop);

        gameLoopMethod.invoke(player);

        var output = getOutput().trim();

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testPlayerGameLoopInterrupted() throws Exception {
        var player = new Player(1, mockDrawDeck, mockDiscardDeck, mockOutputFileWriter);

        for (int i = 1; i <= 4; i++) {
            Card newCard = new Card(i);
            player.addCardToHand(newCard);
        }

        var playerName = player.getName();

        var expectedOutput = playerName + " exits\r\n" + playerName + " final hand: 1 2 3 4";

        player.setGameLoop(mockGameLoop);

        Thread.currentThread().interrupt();

        gameLoopMethod.invoke(player);        

        var output = getOutput().trim();

        assertEquals(expectedOutput, output);
    }

    //#region Winning Hand

    @Test
    public void testHasWinningHandTrue() throws Exception {
        var player = new Player(1, null, null, null);

        for (int i = 0; i < 4; i ++) {
            Card newCard = new Card(1);
            player.addCardToHand(newCard);
        }

        var expectedResult = true;

        var result = hasWinningHandMethod.invoke(player);
        
        assertEquals(expectedResult, result);
    }

    @Test
    public void testHasWinningHandFalse() throws Exception {
        var player = new Player(1, null, null, null);

        for (int i = 1; i <= 4; i ++) {
            Card newCard = new Card(i);
            player.addCardToHand(newCard);
        }

        var expectedResult = false;

        var result = hasWinningHandMethod.invoke(player);
        
        assertEquals(expectedResult, result);
    }

    //#endregion

    //#region Take Turn

    @Test
    public void testTakeTurnUsefulCard() throws Exception {
        var player = new Player(1, mockDrawDeck, mockDiscardDeck, mockOutputFileWriter);

        var expectedResult = "1 ";
        var expectedOutput = "player 1 draws a 1 from deck 1\r\nplayer 1 current hand is 1";

        takeTurnMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();

        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    @Test
    public void testTakeTurnUselessCard() throws Exception {
        var player = new Player(0, mockDrawDeck, mockDiscardDeck, mockOutputFileWriter);

        var expectedResult = "";
        var expectedOutput = "player 0 draws a 1 from deck 1\r\nplayer 0 discards a 1 to deck 2\r\nplayer 0 current hand is";

        takeTurnMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();

        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    //#endregion

    @Test
    public void testDrawCardToHand() throws Exception {
        var player = new Player(1, mockDrawDeck, null, mockOutputFileWriter);

        for (int i = 1; i <= 4; i++) {
            Card newCard = new Card(i);
            player.addCardToHand(newCard);
        }

        var expectedResult = "1 2 3 4 1 ";
        var expectedOutput = "player 1 draws a 1 from deck 1";

        drawMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();
        
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    //#region Discard

    @Test
    public void testDiscardCardAllUseful() throws Exception {
        var player = new Player(1, null, mockDiscardDeck, mockOutputFileWriter);

        for (int i = 0; i < 4; i++) {
            Card newCard = new Card(1);
            player.addCardToHand(newCard);
        }

        var expectedResult = player.getCardsString();
        var expectedOutput = "";

        discardMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();
        
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    @Test
    public void testDiscardCardSomeUseful() throws Exception {
        var player = new Player(1, null, mockDiscardDeck, mockOutputFileWriter);

        for (int i = 0; i < 3; i++) {
            Card newCard = new Card(1);
            player.addCardToHand(newCard);
        }
        Card newCard = new Card(2);
        player.addCardToHand(newCard);

        var expectedResult = "1 1 1 ";
        var expectedOutput = "player 1 discards a 2 to deck 2";

        discardMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();
        
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    @Test
    public void testDiscardCardNoneUseful() throws Exception {
        var player = new Player(0, null, mockDiscardDeck, mockOutputFileWriter);

        for (int i = 1; i <= 4; i++) {
            Card newCard = new Card(i);
            player.addCardToHand(newCard);
        }

        var expectedResult = "2 3 4 ";
        var expectedOutput = "player 0 discards a 1 to deck 2";

        discardMethod.invoke(player);

        var result = player.getCardsString();
        var output = getOutput().trim();
        
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, output);
    }

    //#endregion
}
