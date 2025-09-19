package fr.usmb.tpinfo901;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Mailbox {
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    public void addMessage(Message msg) {
        try {
            messages.put(msg); // bloque si la queue est pleine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Message getMsg() {
        try {
            return messages.take(); // bloque tant qu'il n'y a pas de message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public int getMessageCount() {
        return messages.size();
    }


    public boolean isEmpty() {
        return messages.isEmpty();
    }
}

