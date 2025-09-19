package fr.usmb.tpinfo901;

import com.google.common.eventbus.Subscribe;
import java.util.concurrent.Semaphore;

public class Com {
    public Mailbox mailbox;
    private int id;
    private int clock = 0;
    private static int nbProcess = 0;
    private EventBusService bus;

    private final Semaphore lockSC = new Semaphore(0);      // contrôle la section critique
    private final Semaphore lockToken = new Semaphore(0);   // contrôle le jeton
    public boolean EndGame = false;


    public Com(int id, int totalProcesses) {
        this.id = id;
        this.mailbox = new Mailbox();
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this);
        this.nbProcess = totalProcesses;
    }

    // --- Gestion des messages de diffusion ---
    @Subscribe
    public void onMessageBus(BroadcastMessage m) {
        if (m.getSender() != id) {
            mailbox.addMessage(m);
            clock = Math.max(clock, m.getClock()) + 1;
            System.out.println("P" + id + " a reçu un broadcast : " + m.getPayload() + " [clock=" + clock + "]");
            if (m.getPayload().toString().contains("gagné")) {
                this.EndGame = true;
            }
        }
    }

    // Réception d'un message normal
    @Subscribe
    public void onMessageTo(MessageTo m) {
        if (m.getDest() == this.id) {
            receive(m);
        }
    }

    @Subscribe
    public void onACKMessageTo(ACKMessage m) {
        if (m.getDest() == this.id) {
            receiveACK(m);
        }
    }

    // --- Gestion du jeton ---
    @Subscribe
    public void onToken(TokenMessage m) {
        if (m.getDest() == this.id && !this.EndGame) {
            lockToken.release();
            System.out.println(">>> P" + id + " reçoit le jeton de P" + m.getSender());

            try {
                lockSC.acquire();
                int nextId = (id + 1) % nbProcess;
                bus.postEvent(new TokenMessage(id, nextId));
                System.out.println(">>> P" + id + " renvoie le jeton à P" + nextId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void receive(MessageTo m) {
        mailbox.addMessage(m);

        clock = Math.max(clock, m.getClock()) + 1;

        System.out.println("P" + id + " a reçu de P" + m.getSender()
                + " : " + m.getPayload() + " [clock=" + clock + "]");

        sendACK(m.getDest(), m.getSender());
    }


    public void receiveFromSync(Object o, int from) {
        boolean received = false;
        while (!received) {
            Message msg = mailbox.getMsg();
            if (msg.getSender() == from && msg.getPayload().equals(o)) {
                received = true;
                sendACK(from, msg.getSender());
            } else {
                mailbox.addMessage(msg);
            }
        }
    }

    private void receiveACK(ACKMessage m) {
        mailbox.addMessage(m);
    }

    // Envoi simple (async)
    public void sendTo(Object o, int to) {
        clock++;
        System.out.println("P" + id + " envoie à P" + to + " : " + o + " [clock=" + clock + "]");
        MessageTo m = new MessageTo(clock, id, to, o);
        bus.postEvent(m);
    }

    public void sendACK(int from, int to){
        ACKMessage m = new ACKMessage(from,to);
        bus.postEvent(m);
    }


    // Envoi bloquant (sync)
    public void sendToSync(Object o, int to) {
        clock++;
        System.out.println("P" + id + " envoie (sync) à P" + to + " : " + o + " [clock=" + clock + "]");
        MessageTo m = new MessageTo(clock, id, to, o);
        bus.postEvent(m);

        boolean ackReceived = false;
        while (!ackReceived) {
            Message reply = mailbox.getMsg();

            if (reply.getSender() == to && reply instanceof ACKMessage) {
                ackReceived = true;
            } else {
                mailbox.addMessage(reply);
            }
        }
    }

    // Section critique distribuée
    public void requestSC() {
        try {
            lockToken.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void releaseSC() {
        lockSC.release();
    }

    // Synchronisation de tous les processus
    public void synchronize() {
        broadcastSync("SYNC", 0);
    }


    // Broadcast
    public void broadcast(Object o) {
        BroadcastMessage m = new BroadcastMessage(++clock, id, o);
        System.out.println("P" + id + " broadcast : " + o);
        bus.postEvent(m);
    }


    public void broadcastSync(Object o, int from) {
        if (this.id == from) {
            // Envoyer à tous les autres
            for (int i = 0; i < nbProcess; i++) {
                if (i != from) {
                    sendTo(o, i);
                }
            }

            // Attendre les ACK de tous
            int ackCount = 0;
            while (ackCount < nbProcess - 1) {
                Message msg = mailbox.getMsg();
                if (msg instanceof ACKMessage) {
                    ackCount++;
                } else {
                    mailbox.addMessage(msg); // remettre les messages non-ACK
                }
            }
        } else {
            // Recevoir le message du processus "from"
            boolean received = false;
            while (!received) {
                Message msg = mailbox.getMsg();
                if (msg.getSender() == from && msg.getPayload().equals(o)) {
                    received = true;
                    sendACK(msg.getSender(),from );
                } else {
                    mailbox.addMessage(msg); // remettre les autres messages
                }
            }
        }
    }

}
