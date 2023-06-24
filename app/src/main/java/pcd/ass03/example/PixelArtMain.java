package pcd.ass03.example;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class PixelArtMain {

    public static int randomColor() {
        Random rand = new Random();
        return rand.nextInt(256 * 256 * 256);
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        // Connection to the message broker.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declaration of the message exchange.
        String exchangeName = "pixelart_exchange";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        // Declaration of the queue and binding to the exchange.
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        var brushManager = new BrushManager();
        var localBrush = new BrushManager.Brush(0, 0, randomColor());
        var fooBrush = new BrushManager.Brush(0, 0, randomColor());
        brushManager.addBrush(localBrush);
        brushManager.addBrush(fooBrush);

        PixelGrid grid = new PixelGrid(40, 40);
        /*Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
        }*/
        PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

        // set the position of the brush to the mouse position.
        view.addMouseMovedListener((x, y) -> {
            localBrush.updatePosition(x, y);
            view.refresh();
        });

        // set the color of the pixel at the mouse position.
        view.addPixelGridEventListener((x, y) -> {
            grid.set(x, y, localBrush.getColor());
            view.refresh();

            String message = x + "," + y + "," + localBrush.getColor();
            channel.basicPublish(exchangeName, "", null, message.getBytes());
        });

        // Lists of events to be executed when the window is closed...
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    String message = "Client disconnected";
                    channel.basicPublish(exchangeName, "", null, message.getBytes());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ... or opened.
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                try {
                    String message = "Client connected";
                    channel.basicPublish(exchangeName, "", null, message.getBytes());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        //TODO IN QUESTO MOMENTO QUESTO NON STA FUNZIONANDO, NON CI ENTRA PROPRIO. (strano perchè sotto l'ho fatto in una maniera differente usando lo stesso
        //TODO metodo e funziona), hai creato una interfaccia WindowOpenedListener e aggiunto un metodo in View che aggiunge un listener di questo tipo.
        //ivoke the "windowOpened" method of the listeners.
        /*view.addWindowOpenedListener(() -> {
            System.out.println(" ----------------- A client has connected. !!! -----------------");
            String message = "Client connected";
            channel.basicPublish(exchangeName, "", null, message.getBytes());
        });*/

        // Note that these methods invokation execute before the "addMouseMovedListener" and the "addPixelGridEventListener" methods.
        // Because they are asynchronous events.
        view.addColorChangedListener(localBrush::setColor);
        view.display();
        // Creation of a consumer for the queue.
        channel.basicConsume(queueName, true, createConsumer(view, grid, exchangeName, channel));

/*        // Add a separate queue for disconnection messages
        String disconnectQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(disconnectQueueName, exchangeName, "");

        // Consume messages from the disconnect queue
        channel.basicConsume(disconnectQueueName, true, createDisconnectConsumer(view, grid, brushManager, channel));*/

    }

    private static Consumer createConsumer(PixelGridView view, PixelGrid grid, String exchangeName, Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                String[] parts = message.split(",");
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int color = Integer.parseInt(parts[2]);
                    // Update the other client grids with the colors painted.
                    grid.set(x, y, color);
                    System.out.println(message);
                    view.refresh();
                } else if (parts.length == 1 && parts[0].equals("Client connected")) {
                    System.out.println(message);
                    sendGridInformation(view, grid, channel, exchangeName);
                    view.refresh();
                } else if (parts.length == 1 && parts[0].equals("Client disconnected")) {
                    System.out.println(message);
                } else {
                    System.out.println(message + " is unknown and do nothing in the program.");
                }
            }
        };
    }

    private static void sendGridInformation(PixelGridView view, PixelGrid grid, Channel channel, String exchangeName) throws IOException {
        List<String> gridInformation = getGridInformation(grid);
        for(String s : gridInformation) {
            String[] parts = s.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int color = Integer.parseInt(parts[2]);
            System.out.println("Sto lavorando sulla cella" + x + "," + y + "con colore" + color);
            String message = x + "," + y + "," + color;
            channel.basicPublish(exchangeName, "", null, message.getBytes());
        }

    }

    /*
    private static void sendGridInformation(PixelGridView view, PixelGrid grid, Channel channel, String exchangeName) throws IOException {
        // Obtain the information from the grid.
        List<String> gridInformation = getGridInformation(grid);
        // Create unique string from the list of information.
        String message = String.join(";", gridInformation);
        // Send the message to the clients.
        channel.basicPublish(exchangeName, "", null, message.getBytes());
    }*/

    private static List<String> getGridInformation(PixelGrid grid) {
        List<String> gridInformation = new ArrayList<>();
        int numRows = grid.getNumRows();
        int numColumns = grid.getNumColumns();
        for (int x = 0; x < numColumns; x++) {
            for (int y = 0; y < numRows; y++) {
                int color = grid.get(x, y);
                if (color != 0) {
                    // Retrieve the information of the cell...and saves them.
                    String cellInfo = x + "," + y + "," + color;
                    gridInformation.add(cellInfo);
                }
            }
        }
        return gridInformation;
    }



/*    private static Consumer createDisconnectConsumer(PixelGridView view, PixelGrid grid, BrushManager brushManager, Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
            }
        };
    }*/




}
