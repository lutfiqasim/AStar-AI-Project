package com.example.astarproj;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.FileOutputStream;
import java.io.IOException;

public class DiGraphPainter {

    private StringBuilder graphBuilder = new StringBuilder();
    private String filePrefix;

    public DiGraphPainter() {
    }

    /**
     * @param filePrefix Only put filename prefix E.g. File1, File2, MyNewFile, myfile etc. It will use the filePrefix to create output dot file and png file with same name.
     */
    public DiGraphPainter(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    /**
     * @param line line contains a valid dot file graphviz text
     */
    public void addln(String line) {
        graphBuilder.append(line).append("\n");
    }

    public void add(String text) {
        graphBuilder.append(text);
    }

    public void addnewln() {
        graphBuilder.append("\n");
    }

    public void clear(){
        graphBuilder = new StringBuilder();
    }

    /**
     * Creates an output dot file and uses that to create graphviz png output using following command
     * dot -Tpng <filePrefix>.dot -o <filePrefix>.png
     * If you want to change the certain format, change below.
     */
    public void print() {
        try {

            if (filePrefix == null || filePrefix.isEmpty()) {
                filePrefix = "output";
            }

            graphBuilder.insert(0, "Digraph G {").append("\n");
            graphBuilder.append("labelloc=\"t\"\n"); // top of graph title
            graphBuilder.append("label=" + "\"" + filePrefix +  "\""); // adding the name of the file as title to the graph
            graphBuilder.append("}").append("\n");
            writeTextToFile(filePrefix + ".dot", graphBuilder.toString());

            StringBuilder command = new StringBuilder();
            command.append("dot -Tpng ").    // output type
                    append(filePrefix).append(".dot ").   // input dot file
                    append("-o ").append(filePrefix).append(".png");  // output image
//            System.out.println(command.toString());

            executeCommand(command.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void executeCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
    }

    private void writeTextToFile(String fileName, String text) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        outputStream.write(text.getBytes());
        outputStream.close();
    }
}
