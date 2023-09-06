package pcd.ex1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import java.io.File;


public class MainClass {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ReportSystem");

        // Create Monitor actor
        ActorRef reading = system.actorOf(Props.create(ReadingActor.class), "monitor");

        // Create Model actor
        ModelEController model = new ModelEController();

        // Create Report actor
        ActorRef report = system.actorOf(Props.create(ReportActor.class, reading, model), "report");

        // Send message to Report actor to start processing
        report.tell(new StartProcessingMessage(new File("D:\\Users\\Xmachines\\Desktop\\ProvaAssignment1")), ActorRef.noSender());

        // Create an instance of View
        View view = new View();

    }

}

