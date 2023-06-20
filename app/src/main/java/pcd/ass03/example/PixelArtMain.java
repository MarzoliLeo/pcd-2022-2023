package pcd.ass03.example;

import java.util.Random;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class PixelArtMain {
    public static int randomColor() {
        Random rand = new Random();
        return rand.nextInt(256 * 256 * 256);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "pixelart_exchange";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        var brushManager = new BrushManager();
        var localBrush = new BrushManager.Brush(0, 0, randomColor());
        var fooBrush = new BrushManager.Brush(0, 0, randomColor());
        brushManager.addBrush(localBrush);
        brushManager.addBrush(fooBrush);

        PixelGrid grid = new PixelGrid(40, 40);
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
        }

        PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);
        view.addMouseMovedListener((x, y) -> {
            localBrush.updatePosition(x, y);
            view.refresh();
        });
        view.addPixelGridEventListener((x, y) -> {
            grid.set(x, y, localBrush.getColor());
            view.refresh();

            String message = x + "," + y + "," + localBrush.getColor();
            channel.basicPublish(exchangeName, "", null, message.getBytes());
        });
        view.addColorChangedListener(localBrush::setColor);
        view.display();

        channel.basicConsume(queueName, true, createConsumer(view, grid, brushManager));
    }

    private static Consumer createConsumer(PixelGridView view, PixelGrid grid, BrushManager brushManager) {
        return new DefaultConsumer(view.getChannel()) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                String[] parts = message.split(",");
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int color = Integer.parseInt(parts[2]);
                    grid.set(x, y, color);
                    view.refresh();
                }
            }
        };
    }
}
