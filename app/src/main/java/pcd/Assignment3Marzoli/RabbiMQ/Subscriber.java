package pcd.Assignment3Marzoli.RabbiMQ;

import com.rabbitmq.client.*;

import java.io.UnsupportedEncodingException;

public class Subscriber {
    private static final String EXCHANGE_NAME = "pixel_exchange";

    public static void main(String[] argv) throws Exception {
        // Set up connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Replace with your RabbitMQ server's hostname
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // Create a temporary queue with a random name
        String queueName = channel.queueDeclare().getQueue();

        // Bind the queue to the exchange
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        // Create a consumer to receive messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws UnsupportedEncodingException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                // Process the message and update the grid
                // Update the grid based on mouse pointer movements or pixel color updates
            }
        };

        // Start consuming messages
        channel.basicConsume(queueName, true, consumer);
    }
}
