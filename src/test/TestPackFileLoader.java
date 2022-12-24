import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class TestPackFileLoader {
    
    private static final PackFileLoader packFileLoader = new PackFileLoader();

    private final InputStream stdSystemIn = System.in;
    private final PrintStream stdSystemOut = System.out;

    private ByteArrayOutputStream testOut;

    //#region Setup

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder(); 

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

    //#region Tests

    @Test
    public void TestProcessPackFileMissing() {

        var result = packFileLoader.processPackFile("MissingFile.txt", 8);

        assertEquals("Sorry, the specified file doesn't exist.", getOutput().trim());
        assertEquals(null, result);
    }

    @Test
    public void TestProcessPackFileNoExtension() throws IOException {

        var file = testFiles.newFile("NoExtension.txt");

        var filePath = file.getAbsolutePath();

        var noExtensionFilePath = filePath.substring(0, filePath.lastIndexOf("."));

        var result = packFileLoader.processPackFile(noExtensionFilePath, 8);

        assertEquals("Sorry, the specified file doesn't exist.", getOutput().trim());
        assertEquals(null, result);
    }

    @Test
    public void TestProcessPackFileEmpty() throws IOException {
    
        var file = testFiles.newFile("Empty.txt");
        
        var result = packFileLoader.processPackFile(file.getAbsolutePath(), 8);

        assertEquals("Incorrect number of values in the pack. Please ensure there are eight times as many values as players.", getOutput().trim());
        assertEquals(null, result);
    }

    @Test
    public void TestProcessPackFileInvalidContents() throws IOException {
        var file = testFiles.newFile("InvalidContents.txt");

        var fileWriter = new FileWriter(file);

        fileWriter.write("a");

        fileWriter.close();

        var result = packFileLoader.processPackFile(file.getAbsolutePath(), 1);

        assertEquals("The contents of your pack file isn't valid.", getOutput().trim());
        assertEquals(null, result);
    }

    @Test
    public void TestProcessPackFileTooFewValues() throws IOException {
        var file = testFiles.newFile("TooFewValues.txt");

        var fileWriter = new FileWriter(file);

        fileWriter.write("1\n");
        fileWriter.write("2\n");

        fileWriter.close();

        var result = packFileLoader.processPackFile(file.getAbsolutePath(), 3 );

        assertEquals("Incorrect number of values in the pack. Please ensure there are eight times as many values as players.", getOutput().trim());
        assertEquals(null, result);
    }

    @Test
    public void TestProcessPackFileValid() throws IOException {

        var file = testFiles.newFile("TooFewValues.txt");

        var fileWriter = new FileWriter(file);

        fileWriter.write("1\n");
        fileWriter.write("2\n");

        fileWriter.close();

        var result = packFileLoader.processPackFile(file.getAbsolutePath(), 2 );

        assertEquals("", getOutput().trim());
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getDenomination());
        assertEquals(2, result.get(1).getDenomination());
    }

    //#endregion

}
