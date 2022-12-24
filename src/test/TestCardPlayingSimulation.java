import org.junit.runner.JUnitCore;

public class TestCardPlayingSimulation {
    public static void main(String[] args) {

        var result = JUnitCore.runClasses(TestIO.class, TestGameComponents.class);

        for (var failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        var closingMessage = result.wasSuccessful() ? "All test passed successfully." : "Tests complete, with some failure(s).";

        System.out.println(closingMessage);
    }
}
