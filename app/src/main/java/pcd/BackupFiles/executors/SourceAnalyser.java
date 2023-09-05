package pcd.BackupFiles.executors;

import pcd.BackupFiles.Model;
import pcd.BackupFiles.Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;

public interface SourceAnalyser {
    void getReport(File directory, Model model, Monitor monitor);
    void analyzeSources(View view, JButton button, DefaultTableModel tableModel);

}
