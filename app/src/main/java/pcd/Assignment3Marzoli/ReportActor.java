package pcd.Assignment3Marzoli;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReportActor extends UntypedAbstractActor {
    private final ActorRef reading;
    private final ModelEController model;


    public ReportActor(ActorRef reading, ModelEController model) {
        this.reading = reading;
        this.model = model;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof StartProcessingMessage) {
            StartProcessingMessage startMessage = (StartProcessingMessage) message;
            getReport(startMessage.getDirectory());
        } else if (message instanceof StopProcessingMessage) {
            //TODO Devi soltanto resettare le variabili e non effettivamente stoppare l'attore, è ciò che vuole Ricci.
            getContext().stop(getSelf()); // Stop the actor
        }
        else {
            unhandled(message);
        }
    }

    private void getReport(File directory) throws InterruptedException, ExecutionException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        List<File> javaFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("** Directory: " + file.getName() + "has been analized.");
                getReport(file);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }

        if (javaFiles.isEmpty()) {
            return;
        }

        int numJavaFiles = javaFiles.size();
        int availableCores = Runtime.getRuntime().availableProcessors();
        int numThreads = Math.min(availableCores, numJavaFiles);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numJavaFiles; i++) {
            final File file = javaFiles.get(i);
            futures.add(executor.submit(() -> reading.tell(new CountLinesMessage(file, model), getSelf())));
        }

        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

    }
}

