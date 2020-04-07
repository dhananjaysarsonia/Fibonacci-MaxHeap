package com.company;

import java.io.FileWriter;
import java.io.IOException;

public class WriterHelper {

    private boolean isFileMode;
    private String outputFilePath;
    private FileWriter writer;
    WriterHelper(boolean isFileMode, String outputFilePath) throws IOException {
        this.isFileMode = isFileMode;
        this.outputFilePath = outputFilePath;
        if(isFileMode){
            writer = new FileWriter(outputFilePath);
        }
    }

    public void write(String text) throws IOException {
        if(isFileMode) {
            writer.write(text + System.getProperty("line.separator"));
        }
        else
        {
            System.out.println(text);
        }
    }

    public void close() throws IOException {
        if(isFileMode)
        {
            writer.close();
        }
    }

}
