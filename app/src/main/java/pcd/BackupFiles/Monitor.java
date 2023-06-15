package pcd.BackupFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Monitor {

    // Count the lines of a file.
    public synchronized void countLines(File file, Model model) throws IOException {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                lines++;
            }
        }
        model.getLinesPerSource().put(file.getAbsolutePath(), lines);
        model.setCount( model.getCount()+1 );
        if (model.getCount() % 100 == 0) {
            System.out.println(model.getCount() + " files processed"); //100 file processed, ecc ecc.
        }
        updateDistribution(lines,model);
    }

    // Updates the distribution of the number of lines.
    private void updateDistribution(int lines, Model model) {
        if (lines >= model.getMAXL()) {
            model.getDistribution().set(model.getNI() - 1, model.getDistribution().get(model.getNI() - 1) + 1);
            return;
        }
        int intervalSize = model.getMAXL() / model.getNI();
        for (int i = 0; i < model.getNI() - 1; i++) {
            int intervalStart = i * intervalSize;
            int intervalEnd = (i + 1) * intervalSize - 1;
            if (lines >= intervalStart && lines <= intervalEnd) {
                model.getDistribution().set(i, model.getDistribution().get(i) + 1);
                break;
            }
        }
    }
}
