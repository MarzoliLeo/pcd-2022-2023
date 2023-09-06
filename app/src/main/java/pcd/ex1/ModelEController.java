package pcd.ex1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelEController {
    private static int MAXL = 1000; // Maximum lines of code per source
    private static int NI = 1000; // Number of intervals for line distribution
    private static int N = 1000; // Number of top sources to display

    private final Map<String, Integer> linesPerSource;
    private final List<Integer> distribution;

    public ModelEController() {
        linesPerSource = new HashMap<>();
        distribution = initializeDistribution();
    }

    public void countLines(File file) throws IOException {
        int linesCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    linesCount++;
                }
            }
        }
        String source = file.getName(); // Use the file name as the source
        linesPerSource.put(source, linesCount);
        updateDistribution(linesCount);

    }

    public void displayDistribution() {
        int intervalSize = MAXL / NI;
        for (int i = 0; i < NI - 1; i++) {
            int intervalStart = i * intervalSize;
            int intervalEnd = (i + 1) * intervalSize - 1;
            System.out.println(intervalStart + "-" + intervalEnd + ": " + distribution.get(i));
        }
        System.out.println(intervalSize * (NI - 1) + "+: " + distribution.get(NI - 1));
    }

    public void displayTopNFiles() {
        // Print the top N sources
        linesPerSource.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(N)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    //Getters
    public int getMAXL() {
        return MAXL;
    }

    public int getNI() {
        return NI;
    }

    public int getN() {
        return N;
    }

    public ModelEController getModel() {
        return this;
    }

    public Map<String, Integer> getLinesPerSource() {
        return linesPerSource;
    }

    public List<Integer> getDistribution() {
        return distribution;
    }

    //Setters
    public int setMAXL(int MAXL) { return this.MAXL = MAXL; }

    public int setNI(int NI) { return this.NI = NI; }

    public int setN(int N) { return this.N = N; }

    private List<Integer> initializeDistribution() {
        // Initialize the distribution list with zeros
        List<Integer> distribution = new ArrayList<>();
        for (int i = 0; i < NI; i++) {
            distribution.add(0);
        }
        return distribution;
    }

    private void updateDistribution(int linesCount) {
        int intervalSize = MAXL / NI;
        int intervalIndex = Math.min(linesCount / intervalSize, NI - 1);
        distribution.set(intervalIndex, distribution.get(intervalIndex) + 1);
    }

}
