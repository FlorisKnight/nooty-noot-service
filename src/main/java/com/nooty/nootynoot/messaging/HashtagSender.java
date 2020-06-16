package com.nooty.nootynoot.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class HashtagSender {
    private final static String QUEUE_NAME_CREATE = "newHashtag";
    private final static String QUEUE_NAME_DELETE = "deleteHashtag";


    public void newHashtag(String msg) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("nooty-rabbitmq");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME_CREATE, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME_CREATE, null, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + msg + "'");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHashtag(String msg) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("nooty-rabbitmq");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME_DELETE, false, false, false, null);
            String message = msg;
            channel.basicPublish("", QUEUE_NAME_DELETE, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
