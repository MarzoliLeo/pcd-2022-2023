package pcd.BackupFiles.executors;

import pcd.BackupFiles.Model;
import pcd.BackupFiles.Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static pcd.BackupFiles.Model.resetRecord;

public class ExecutorLibrary implements SourceAnalyser {

    @Override
    public void getReport(File directory, Model model, Monitor monitor) {
        try {
            // Get all the files in the directory.
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }
            List<File> javaFiles = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) { // If it's a directory, process it recursively.
                    getReport(file, model, monitor);
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file); // Add the file to the list of files to process.
                }
            }
            if (javaFiles.isEmpty()) {
                return;
            }
            int numJavaFiles = javaFiles.size();
            // Get the number of available cores.
            int availableCores = Runtime.getRuntime().availableProcessors();
            // Use the minimum of the number of available cores and the number of files to process.
            int numThreads = Math.min(availableCores, numJavaFiles);

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < numJavaFiles; i++) {
                final File file = javaFiles.get(i);
                futures.add(executor.submit(() -> {
                    try {
                        monitor.countLines(file, model);
                    } catch (IOException e) {
                        System.err.println("Error processing file " + file);
                    }
                }));
            }

            // Wait for all tasks to complete.
            for (Future<?> future : futures) {
                future.get();
            }

            executor.shutdown();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analyzeSources(View view, JButton button, DefaultTableModel tableModel) {
         // Start the program.
        ExecutorService executorService = Executors.newCachedThreadPool(); // Create a new ThreadPoolExecutor
        executorService.execute(new Runnable() { // Submit a new task to the ThreadPoolExecutor
            public void run() {
                try {
                    if (button == view.getStartButton()) {

                        System.out.println("Start button pressed: executing the program.");
                        view.startProgram();                // Start the program.
                        Thread.sleep(3000);
                        if (View.interrupted) {             // if the program is interrupted, I don't show anything.
                            return;
                        }
                        SwingUtilities.invokeLater(() -> {
                            view.setTopSourceFiles(tableModel);      // set the top source files in the JTable.
                            view.showHistogram();                    // show the histogram.
                            view.getStartButton().setEnabled(true);
                        });

                    } else {
                        try {
                            System.out.println("Stop button pressed: stopping the program.");
                            View.interrupted = true;                     // set interrupted to true to stop the program.
                            SwingUtilities.invokeLater(() -> {
                                view.setTopSourceFiles(tableModel);          // if the program is interrupted, I clean the JTable.
                                view.showHistogram();                        // if the program is interrupted, I close the histogram window.
                                view.getStartButton().setEnabled(true);
                                resetRecord();                      // reset the record in the model.
                            });
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                view.getStartButton().setEnabled(true);
                                resetRecord();                      // reset the record in the model.
                            });
                        }
                    }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        executorService.shutdown(); // Shut down the ThreadPoolExecutor
    }


}
