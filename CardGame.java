import Interfaces.IGameElements;

public class CardGame {

    //#region Attributes

    private static IGameElements gameElements;

    //#endregion

    //#region Methods

    public static void main(String[] args) {

        // Set up the simulation
        var gameSetup = new GameSetup(new InputTaker(), new PackFileLoader(), new CardDistributor());

        gameElements = gameSetup.setupGame();

        var gameLoop = new GameLoop(gameElements);

        System.out.println("Beginning game loop...");

        // Run the game loop
        gameLoop.runGameLoop();
    }

    //#endregion
}