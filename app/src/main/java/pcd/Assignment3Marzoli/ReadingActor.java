package pcd.Assignment3Marzoli;

import akka.actor.UntypedAbstractActor;

import java.io.File;
import java.io.IOException;

public class ReadingActor extends UntypedAbstractActor {
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof CountLinesMessage) {
            CountLinesMessage countLinesMessage = (CountLinesMessage) message;
            countLines(countLinesMessage.getFile(), countLinesMessage.getModelActor());
        } else {
            unhandled(message);
        }
    }

    private void countLines(File file, ModelEController model) {
        try {
            // Count lines of code for the file
            // Update the model with the count
            model.countLines(file);
            model.displayTopNFiles();
            model.displayDistribution();
        } catch (IOException e) {
            System.err.println("Error processing file " + file);
        }
    }
}

