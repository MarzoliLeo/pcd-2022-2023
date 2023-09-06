package pcd.ex1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class View extends JFrame {
    private JPanel mainPanel;
    private JLabel nLabel;
    private JLabel niLabel;
    private JLabel maxlLabel;
    private JLabel directoryLabel;
    private JButton startButton;
    private JButton stopButton;
    private JTextField nTextField;
    private JTextField niTextField;
    private JTextField maxlTextField;
    private JTextField directoryTextField;
    private JTable topSourceTable;

    private static ModelEController model = new ModelEController();

    private static Boolean interrupted = false;
    // static list that keeps track of all open instances of Histogram
    private static List<Histogram> histogramOpenInstances = new ArrayList<>();

    public View() {
        super(".:: File Java Length Counter ::.");

        //New System for the actors based on GUI.
        ActorSystem system = ActorSystem.create("ReportSystem");
        // Create Monitor actor
        ActorRef reading = system.actorOf(Props.create(ReadingActor.class), "monitor");
        // Create Report actor
        ActorRef report = system.actorOf(Props.create(ReportActor.class, reading, model), "report");

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
        nTextField = new JTextField(Integer.toString(model.getN()));
        niTextField = new JTextField(Integer.toString(model.getNI()));
        maxlTextField = new JTextField(Integer.toString(model.getMAXL()));
        directoryTextField = new JTextField("D:\\Users\\Xmachines\\Desktop\\ProvaAssignment1");

        // Create the top source panel as JTable.
        JPanel topSourcePanel = new JPanel(new BorderLayout());
        topSourcePanel.setBorder(BorderFactory.createTitledBorder("Top Source Files"));
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Filename");
        tableModel.addColumn("Lines of Code");

        topSourceTable = new JTable(tableModel);
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
        mainPanel.add(topSourcePanel, c);

        // Add the main panel to the frame
        JScrollPane mainPanelScrollPane = new JScrollPane(mainPanel);
        add(mainPanelScrollPane);

        // Set the frame size and visibility
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Attach action listeners to the buttons
        startButton.addActionListener(e -> {
            // Perform start button action
            // This can include starting the program or initiating the processing
            // Retrieve the values from the text fields if needed
            model.setN(Integer.parseInt(getNTextField().getText()));
            model.setNI(Integer.parseInt(getNiTextField().getText())) ;
            model.setMAXL(Integer.parseInt(getMaxlTextField().getText()));
            String directory = getDirectoryTextField().getText();
            // Start the program or initiate the processing with the provided values

            startButton.addActionListener((ActionEvent) -> {
                report.tell(new StartProcessingMessage(new File(directory)), ActorRef.noSender());
                try {
                    Thread.sleep(1000);
                    interrupted = false;
                    displayGUITopNFiles(getTableModel());
                    showHistogram();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }


            });
        });

        stopButton.addActionListener(e -> {
            stopButton.addActionListener((ActionEvent) -> {
                // Stop the program or interrupt the processing logic here
                report.tell(new StopProcessingMessage(), ActorRef.noSender());
                interrupted = true;
                displayGUITopNFiles(getTableModel());
                showHistogram();
            });

        });
    }

    private void displayGUITopNFiles(DefaultTableModel tableModel) {
        if (!interrupted) {
            tableModel.setRowCount(0);
            model.getLinesPerSource().entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(model.getN())
                    .forEach(entry -> {
                        File file = new File(entry.getKey());
                        tableModel.addRow(new Object[]{file.getName(), entry.getValue()});
                    });
        } else {
            System.out.println("Cleaning the display of files.");
            tableModel.setRowCount(0);
        }
    }

    public void showHistogram() {
        Histogram histogram = new Histogram(model.getNI(), model.getMAXL());
        histogramOpenInstances.add(histogram);  // Adding the new Histogram JPanel to the list of opened Jframes.
        histogram.setVisible(true);

        if(!interrupted) {
            histogram.setVisible(true);
            Map<String, Integer> linesPerSource = model.getLinesPerSource();
            for (Integer lines : linesPerSource.values()) {
                int range = 0;
                if (lines > model.getMAXL()) {        // if the number of lines is greater than the maximum number of lines, I put it in the last range.
                    range = model.getNI() - 1;
                } else {                        // otherwise I put it in the correct range.
                    for (int i = 0; i < model.getNI() - 1; i++) {
                        if (lines >= i * (model.getMAXL() / model.getNI()) && lines < (i + 1) * (model.getMAXL() / model.getNI())) {
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


    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JTextField getNTextField() {
        return nTextField;
    }

    public JTextField getNiTextField() {
        return niTextField;
    }

    public JTextField getMaxlTextField() {
        return maxlTextField;
    }

    public JTextField getDirectoryTextField() {
        return directoryTextField;
    }

    public DefaultTableModel getTableModel() {
        return (DefaultTableModel) topSourceTable.getModel();
    }
}

