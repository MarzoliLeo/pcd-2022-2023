package pcd.BackupFiles.executors;

import pcd.BackupFiles.Controller;
import pcd.BackupFiles.Model;
import pcd.BackupFiles.Monitor;

import java.io.File;
import java.util.Scanner;

public class MainTerminal {

    public static boolean isInvoked = false;

    public static void main(String[] args) {


        long start ;

        // Default refs.
        Model model = new Model();
        Monitor monitor = new Monitor();
        Controller controller = new Controller();

        // Testing the packages of Executors.
        ExecutorLibrary executorLibrary = new ExecutorLibrary();


        if(!isInvoked)
        {
            // Ask for user input and initializing model -- Default behavior.
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter number of files to display: ");
            int n = scanner.nextInt();
            model.setN(Integer.parseInt(String.valueOf(n)));
            System.out.print("Enter max number of lines: ");
            int maxL = scanner.nextInt();
            model.setMAXL(Integer.parseInt(String.valueOf(maxL)));
            System.out.print("Enter number of distributions: ");
            int ni = scanner.nextInt();
            model.setNI(Integer.parseInt(String.valueOf(ni)));
            System.out.print("Enter directory path: ");
            String directoryPath = scanner.next();
            model.setDirectory(String.valueOf(directoryPath));
            scanner.close();


            start = System.currentTimeMillis();
        }
        else
        {
            // Check number of arguments.
            if (args.length != 4) {
                System.err.println("Usage: java ConcurrentCodeCounter <files_to_display> <max_num_lines> <num_distributions> <directory>");
                System.exit(1);
            }
            // Initializing model. -- Invoked Behavior.
            model.setN(Integer.parseInt(args[0]));      // number of files to display.
            model.setMAXL(Integer.parseInt(args[1]));   // max number of lines.
            model.setNI(Integer.parseInt(args[2]));     // number of distributions.
            model.setDirectory(args[3]);                // directory.


            start = System.currentTimeMillis();
        }


        // Check on directory.
        if (!new File(model.getDirectory()).isDirectory() || model.getNI() > model.getMAXL() ) {
            System.err.println(model.getDirectory() + " is not a directory or you put NI > MAXL");
            System.exit(1);
        }

        // Check executors.
        executorLibrary.getReport(new File(model.getDirectory()), model, monitor);

        // Display in Terminal (default behaviour).
        controller.displayTopNSources(model);
        controller.displayDistribution(model);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Time elapsed: " + timeElapsed + " ms");
    }
}
