package com.nooty.nootynoot.messaging;

import com.google.gson.Gson;
import com.nooty.nootynoot.NootRepo;
import com.nooty.nootynoot.models.Noot;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Controller
public class NootReceiver{

    @Autowired
    private NootRepo nootRepo;

    private Gson gson;
    private final static String QUEUE_NAME = "noots";

    public NootReceiver(NootRepo nootRepo) throws Exception{
        gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setHost("172.18.0.20");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            saveNoot(message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
}

    private void saveNoot(String data) {
        Noot noot = gson.fromJson(data, Noot.class);
        this.nootRepo.save(noot);

        /* This will send a noot to online users.
        liveNootsSender.sendNootToSubs(gson.toJson(noot));

        this will check if the noot contains any hashtags and will send it to the hashtag service
        List<String> hashTags = checkHashtag(noot);
        if (hashTags.size() != 0) {
            for (String h: hashTags) {
                HashtagViewModel hvm = new HashtagViewModel();
                hvm.getUserId();
                hvm.setHashtag(h);
                hashtagSender.newHashtag(gson.toJson(hvm));
            }
        }
         */
    }

}
