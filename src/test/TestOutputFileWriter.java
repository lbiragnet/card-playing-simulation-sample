import java.nio.file.*;
import java.lang.reflect.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedWriter;

public class TestOutputFileWriter {

    private static final String relativePath = System.getProperty("user.dir");
    private static final String testFileName = "testFile.txt";
    private static final Path testFilePath = Paths.get(relativePath, testFileName);
    
    private static OutputFileWriter outputFileWriter = new OutputFileWriter();
    private static Class<? extends OutputFileWriter> outputFileWriterClass = OutputFileWriter.class;
    private static Field outputWriteBufferField;

    @BeforeClass
    public static void setupReflection() throws Exception {
        outputWriteBufferField = outputFileWriterClass.getDeclaredField("writeBuffer");
        outputWriteBufferField.setAccessible(true);
    }   

    @After
    public void deleteFile() throws Exception {
        var writeBuffer = (BufferedWriter)outputWriteBufferField.get(outputFileWriter);

        writeBuffer.close();

        Files.delete(testFilePath);
    }

    @Test
    public void testCreateOutputFile() {

        var output = outputFileWriter.createOutputFile(testFileName);

        var fileExists = Files.exists(testFilePath);

        assertEquals(true, output);
        assertEquals(true, fileExists);
    }

    @Test
    public void testCreateOutputFileAlreadyExists() throws Exception {

        Files.createFile(testFilePath);

        var fileExists = Files.exists(testFilePath);
        assertEquals(true, fileExists);

        var output = outputFileWriter.createOutputFile(testFileName);

        assertEquals(true, output);
    }

    @Test
    public void testWriteLineToFile() throws Exception {

        outputFileWriter.createOutputFile(testFileName);

        var firstLine = "test line 1";
        outputFileWriter.writeLineToFile(firstLine);
        
        var secondLine = "test line 2";
        outputFileWriter.writeLineToFile("test line 2");

        var fileLines = Files.readAllLines(testFilePath);

        assertEquals(firstLine, fileLines.get(0));
        assertEquals(secondLine, fileLines.get(1));
    }

}
