import Interfaces.IOutputFileWriter;

import java.io.*;

public class OutputFileWriter implements IOutputFileWriter {

    //#region Attributes
    
    private BufferedWriter writeBuffer;

    //#endregion

    //#region Methods

    @Override
    public boolean createOutputFile(String fileName) {

        // Create output file and a buffered writer for the player
        try {
            File outputFile = new File(fileName);

            outputFile.createNewFile();

            createBufferedWriter(outputFile);

            return true;
        }
        catch (IOException e) {
            System.out.println("An error occurred when creating the output file " + fileName + ":");
            e.printStackTrace();

            // Return false if the output file was not successfully created
            return false;
        }
    }
    
    @Override
    public boolean writeLineToFile(String line) {
        try {
            // Try to write the line to the output file
            writeBuffer.write(line);
            writeBuffer.newLine();
            writeBuffer.flush();
            return true;
        }
        catch (IOException e) {
            System.out.println("An error occurred during line writing.");
            e.printStackTrace();
            
            // Return false if the line was not successfully written to the output file
            return false;
        }
    }

    private void createBufferedWriter(File outputFile) {
        // Create a buffered writer for the player output file
        try {
            FileWriter fileWriter = new FileWriter(outputFile);

            BufferedWriter writeBuffer = new BufferedWriter(fileWriter);
            this.writeBuffer = writeBuffer;
        }
        catch (IOException e) {
            System.out.println("An error occurred during BufferedWriter creation for " + outputFile.getName() + ":");
            e.printStackTrace();
        }
    }

    //#endregion
}
