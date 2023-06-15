package pcd.BackupFiles;

import java.util.*;

public class Model {
    private static int N = 20;      // Change this to adjust the number of sources to display.
    private static int NI = 1000;   // Number of intervals.
    private static int MAXL = 2000; // Maximum number of lines of code to delimit the last interval.
    private static String directory = "D:\\Users\\Xmachines\\Desktop\\ProvaAssignment1";             // Directory to search for files.
    private static Map<String, Integer> linesPerSource = new HashMap<>();                            // Map of the form  <source, number of lines>.
    private static List<Integer> distribution = new ArrayList<>(Collections.nCopies(getNI(), 0)); // List of number of sources in a distribution.
    private static int count = 0;   // Count the number of files processed.

    // Getter for N.
    public static int getN() { return N; }
    // Getter for MAXL.
    public static int getMAXL() {return MAXL; }
    // Getter for NI.
    public static int getNI() { return NI; }
    // Getter for the directory.
    public static String getDirectory() { return directory; }
    // Getter for the linesPerSource.
    public static Map<String, Integer> getLinesPerSource() {return linesPerSource; }
    // Getter for the distribution.
    public static List<Integer> getDistribution() { return distribution; }
    // Getter for the count.
    public static int getCount() { return count; }


    // Setter for N.
    public void setN(int n) { this.N = n; }
    // Setter for MAXL.
    public void setMAXL(int maxl) { this.MAXL = maxl; }
    // Setter for NI.
    public void setNI(int ni) { this.NI = ni; }
    // Setter for linesPerSource map.
    public void setLinesPerSource(Map<String, Integer> newMap) { this.linesPerSource = newMap; }

    // Setter for distribution.
    public void setDistribution(List<Integer> distribution) { this.distribution = distribution; }

    // Setter for the directory.
    public void setDirectory(String dir) { this.directory = dir; }
    // Setter for the count.
    public void setCount(int count) { this.count = count; }

    // Reset the record.
    public static void resetRecord(){
        linesPerSource = new HashMap<>();
        distribution = new ArrayList<>(Collections.nCopies(getNI(), 0));
        count = 0;
    }
}
