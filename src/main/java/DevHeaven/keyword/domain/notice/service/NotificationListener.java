package DevHeaven.keyword.domain.notice.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener implements MessageListener {

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String receivedMessage = new String(message.getBody());
    System.out.println("Received notification: " + receivedMessage);
    //

  }
}