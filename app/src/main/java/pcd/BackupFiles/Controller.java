package pcd.BackupFiles;


import pcd.BackupFiles.executors.View;

import java.util.*;

public class Controller {

    // Method that displays the top N files with the most lines of code in Terminal.
    public static void displayTopNSources(Model model) {
        System.out.println("Top " + model.getN() + " sources by lines of code:");
        if (View.interrupted) {
            return; // interrupt.
        }
        model.getLinesPerSource().entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(model.getN())
                .forEach(entry -> System.out.println(entry.getValue() + "\t" + entry.getKey()));
    }

    //Method that displays the distribution of lines of code in Terminal.
    public static void displayDistribution(Model model) {
        System.out.println("Distribution of lines of code per source:");
        int intervalSize = model.getMAXL() / model.getNI();
        for (int i = 0; i < model.getNI() - 1; i++) {
            if (View.interrupted) {
                return; // interrupt.
            }
            int intervalStart = i * intervalSize;
            int intervalEnd = (i + 1) * intervalSize - 1;
            System.out.println(intervalStart + "-" + intervalEnd + ": " + model.getDistribution().get(i));
        }
        System.out.println(intervalSize * (model.getNI() - 1) + "+: " + model.getDistribution().get(model.getNI() - 1));
    }

}