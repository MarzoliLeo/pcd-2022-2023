package pcd.Assignment3Marzoli.RabbiMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publisher {
    private static final String EXCHANGE_NAME = "pixel_exchange";

    public static void main(String[] argv) throws Exception {
        // Set up connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Replace with your RabbitMQ server's hostname
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // Simulate mouse pointer movements
        String[] users = {"user1", "user2", "user3"}; // Replace with actual user IDs

        for (String user : users) {
            // Simulate mouse pointer movements
            int x = 10; // Replace with actual X coordinate
            int y = 20; // Replace with actual Y coordinate

            // Publish the mouse pointer movement
            String message = user + ": " + x + "," + y;
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));

            // Simulate pixel color updates
            int pixelX = 5; // Replace with actual pixel X coordinate
            int pixelY = 5; // Replace with actual pixel Y coordinate
            String color = "#FF0000"; // Replace with actual color

            // Publish the pixel color update
            String pixelMessage = user + ": " + pixelX + "," + pixelY + "," + color;
            channel.basicPublish(EXCHANGE_NAME, "", null, pixelMessage.getBytes("UTF-8"));

            System.out.println("Sent: " + message);
            System.out.println("Sent: " + pixelMessage);
        }

        // Clean up resources
        channel.close();
        connection.close();
    }
}
