package pcd.BackupFiles.executors;

import pcd.ex1.Histogram;
import pcd.BackupFiles.Model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.*;

import static pcd.BackupFiles.Model.*;


public class View extends JFrame {

    private JLabel nLabel, niLabel, maxlLabel, directoryLabel;
    private JTextField nTextField, niTextField, maxlTextField, directoryTextField;
    private JButton startButton, stopButton;
    private JPanel mainPanel;

    public static boolean interrupted = false;

    private static List<Histogram> histogramOpenInstances = new ArrayList<>(); // static list that keeps track of all open instances of Histogram

    // A model object to be used in the View class.
    private Model model = new Model();

    // Set of data to apply the Executor environment.
    private ExecutorLibrary executorLibrary = new ExecutorLibrary();



    // Making a constructor.
    public View() {
        super("File Java Length Counter");

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Set the labels.
        nLabel = new JLabel("N:");
        niLabel = new JLabel("NI:");
        maxlLabel = new JLabel("MAXL:");
        directoryLabel = new JLabel("Directory:");

        // Set the buttons.
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");


        // Set the text fields.
        nTextField = new JTextField(Integer.toString(getN()));
        niTextField = new JTextField(Integer.toString(getNI()));
        maxlTextField = new JTextField(Integer.toString(getMAXL()));
        directoryTextField = new JTextField(getDirectory());


        // Create the top source panel as JTable.
        JPanel topSourcePanel = new JPanel(new BorderLayout());
        topSourcePanel.setBorder(BorderFactory.createTitledBorder("Top Source Files"));
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Filename");
        tableModel.addColumn("Lines of Code");

        JTable topSourceTable = new JTable(tableModel);
        JScrollPane topSourceTableScrollPane = new JScrollPane(topSourceTable);
        topSourceTableScrollPane.setPreferredSize(new Dimension(350, 200));
        topSourceTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        topSourcePanel.add(topSourceTableScrollPane, BorderLayout.CENTER);

        // Add the elements to the main panel
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(nLabel, c);

        c.gridx = 1;
        mainPanel.add(nTextField, c);

        c.gridx = 2;
        mainPanel.add(niLabel, c);

        c.gridx = 3;
        mainPanel.add(niTextField, c);

        c.gridx = 4;
        mainPanel.add(maxlLabel, c);

        c.gridx = 5;
        mainPanel.add(maxlTextField, c);

        c.gridx = 6;
        mainPanel.add(directoryLabel, c);

        c.gridx = 7;
        mainPanel.add(directoryTextField, c);

        c.gridx = 8;
        mainPanel.add(startButton, c);

        c.gridx = 9;
        mainPanel.add(stopButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 10;

        c.gridy = 2;
        mainPanel.add(topSourcePanel, c);


        // Add the main panel to the frame
        JScrollPane mainPanelScrollPane = new JScrollPane(mainPanel);
        add(mainPanelScrollPane);

        // Set the frame size and visibility
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


        // Add event listeners.
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                interrupted = false;
                // Refreshing the model with the new values.
                model.setNI(Integer.parseInt(niTextField.getText()));
                model.setMAXL(Integer.parseInt(maxlTextField.getText()));
                model.setN(Integer.parseInt(nTextField.getText()));
                model.setDirectory(directoryTextField.getText());
                model.setLinesPerSource(new HashMap<>());
                model.setDistribution(new ArrayList<>(Collections.nCopies(getNI(), 0)));
                Histogram.resetHistogram();

                //Check GUI with executors.
                executorLibrary.analyzeSources(View.this, startButton, tableModel);

            }
        });


        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);

                //Check GUI with executors.
                executorLibrary.analyzeSources(View.this, stopButton, tableModel);

            }
        });

    }

    // Getter, necessary in ExecutorLibrary class, in order to make the GUI interactive.
    public JButton getStartButton() {
        return startButton;
    }


    // Method to show the Top Source Files in the JTable.
    public void setTopSourceFiles(DefaultTableModel tableModel) {
        if(!interrupted)
        {
            tableModel.setRowCount(0);
            //Model model = new Model();
            model.getLinesPerSource().entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(getN())
                    .forEach(entry -> {
                        File file = new File(entry.getKey());
                        tableModel.addRow(new Object[]{file.getName(), entry.getValue()});
                    });
        }else {
            System.out.println("Cleaning the display of files.");
            tableModel.setRowCount(0);
        }
    }



    // Method to show the histogram.
    public void showHistogram() {
        Histogram histogram = new Histogram(getNI(), getMAXL());
        histogramOpenInstances.add(histogram);  // Adding the new Histogram JPanel to the list of opened Jframes.
        histogram.setVisible(true);

        if(!interrupted) {
            histogram.setVisible(true);
            Map<String, Integer> linesPerSource = model.getLinesPerSource();
            for (Integer lines : linesPerSource.values()) {
                int range = 0;
                if (lines > getMAXL()) {        // if the number of lines is greater than the maximum number of lines, I put it in the last range.
                    range = getNI() - 1;
                } else {                        // otherwise I put it in the correct range.
                    for (int i = 0; i < getNI() - 1; i++) {
                        if (lines >= i * (getMAXL() / getNI()) && lines < (i + 1) * (getMAXL() / getNI())) {
                            range = i;
                            break;
                        }
                    }
                }
                histogram.incrementDistribution(range); // I increment the specified distribution of the histogram.
            }
        }  else {
            System.out.println("Cleaning the display of histogram.");
            // Dispose of all histogram instances and clear the list.
            for (Histogram h : histogramOpenInstances) {
                h.setVisible(false);
                h.dispose();
            }
            histogramOpenInstances.clear();
        }
    }

    // Method to start the program.
    public void startProgram() {

        // Get the parameters from the text fields of the GUI.
        int n = Integer.parseInt(nTextField.getText());
        int maxl = Integer.parseInt(maxlTextField.getText());
        int ni = Integer.parseInt(niTextField.getText());
        String directory = directoryTextField.getText();

        pcd.BackupFiles.executors.MainTerminal.isInvoked = true;
        // Call the main method with the specified parameters.
        pcd.BackupFiles.executors.MainTerminal.main(new String[]{Integer.toString(n), Integer.toString(maxl), Integer.toString(ni), directory});

    }

    // Show the GUI at startup.
    public static void main(String[] args) {
        new View();
    }

}

