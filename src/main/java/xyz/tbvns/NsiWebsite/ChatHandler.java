package xyz.tbvns.NsiWebsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.tbvns.NsiWebsite.Config.WebConf;
import xyz.tbvns.NsiWebsite.Database.User;
import xyz.tbvns.NsiWebsite.Database.UserService;
import xyz.tbvns.NsiWebsite.Objects.MessageDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final UserService userService;
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public ChatHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received:" + payload);
        ObjectMapper mapper = new ObjectMapper();
        MessageDTO messageData = mapper.readValue(payload, MessageDTO.class);
        
        User user = userService.validateToken(messageData.getToken());
        if (user == null) {
            session.sendMessage(
                    new TextMessage(
                            String.format(
                                    "<div class='message' style='text-color=red;'><strong>%s</strong>: %s</div>",
                                    "System",
                                    "Something went wrong, did you login on another device ? <br/> <b>ERROR</b>: Invalid token"
                            )
                    )
            );
            return;
        }

        if (!WebConf.allowHtmlInMessage) {
            messageData.setMsg(StringUtils.escapeHtml(messageData.getMsg()));
        }

        String htmlMessage = String.format(
            "<div class='message'><strong>%s</strong>: %s</div>",
            user.getUsername(), 
            messageData.getMsg()
        );

        String senderHtmlMessage = String.format(
                "<div class='message sent'><strong>%s</strong>: %s</div>",
                user.getUsername(),
                messageData.getMsg()
        );

        broadcastMessage(htmlMessage, senderHtmlMessage, session);
    }

    private void broadcastMessage(String message, String senderMessage, WebSocketSession sender) {
        System.out.println("Sent:" + message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    if (session == sender) {
                        session.sendMessage(new TextMessage(senderMessage));
                        continue;
                    }
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                // Handle exception
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}