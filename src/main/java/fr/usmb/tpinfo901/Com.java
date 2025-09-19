package fr.usmb.tpinfo901;

import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Classe de communication distribuée pour les processus.
 * <p>
 * Chaque instance de {@code Com} est associée à un processus identifié par son {@code id}.
 * Elle gère :
 * <ul>
 *     <li>La messagerie point-à-point (asynchrone et synchrone avec ACK).</li>
 *     <li>Les diffusions (broadcast) avec accusés de réception.</li>
 *     <li>Le passage du jeton pour l'accès à la section critique.</li>
 *     <li>La gestion d'une horloge logique de Lamport ({@code clock}).</li>
 * </ul>
 * La communication repose sur un {@link EventBusService} partagé.
 */
public class Com {
    /** Boîte aux lettres locale pour stocker les messages reçus. */
    public Mailbox mailbox;

    /** Identifiant du processus associé à cette communication. */
    private final int id;

    /** Horloge logique de Lamport. */
    private int clock = 0;

    /** Nombre total de processus dans le système distribué. */
    private static int nbProcess = 0;

    /** Bus d'événements partagé entre tous les processus. */
    private final EventBusService bus;

    /** Sémaphore contrôlant l'entrée en section critique. */
    private final Semaphore lockSC = new Semaphore(0);

    /** Sémaphore gérant la possession du jeton. */
    private final Semaphore lockToken = new Semaphore(0);

    /** Indique si le jeu (simulation) est terminé. */
    public boolean EndGame = false;


    /**
     * Construit un objet de communication pour un processus donné.
     *
     * @param id             identifiant du processus
     * @param totalProcesses nombre total de processus dans le système
     */
    public Com(int id, int totalProcesses) {
        this.id = id;
        this.mailbox = new Mailbox();
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this);
        nbProcess = totalProcesses;

        // Le processus 0 crée le jeton initial après un petit délai
        if (id == 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(500); // attendre que tous s'abonnent
                    int nextId = (id + 1) % nbProcess;
                    bus.postEvent(new TokenMessage(id, nextId));
                    System.out.println(">>> P" + id + " crée le jeton et l'envoie à P" + nextId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    // ==========================
    // Réception des messages via EventBus
    // ==========================

    /**
     * Réception d'un message de type broadcast.
     *
     * @param m le message diffusé
     */
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

    /**
     * Réception d'un message direct.
     *
     * @param m le message reçu
     */
    @Subscribe
    public void onMessageTo(MessageTo m) {
        if (m.getDest() == this.id) {
            receive(m);
        }
    }

    /**
     * Réception d'un accusé de réception.
     *
     * @param m ACK reçu
     */
    @Subscribe
    public void onACKMessageTo(ACKMessage m) {
        if (m.getDest() == this.id) {
            receiveACK(m);
        }
    }

    /**
     * Réception du jeton pour accéder à la section critique.
     *
     * @param m le message contenant le jeton
     */
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

    // ==========================
    // Gestion des messages directs
    // ==========================

    /**
     * Ajoute un message reçu à la boîte aux lettres et envoie un ACK.
     *
     * @param m message reçu
     */
    public void receive(MessageTo m) {
        mailbox.addMessage(m);
        clock = Math.max(clock, m.getClock()) + 1;
        System.out.println("P" + id + " a reçu de P" + m.getSender()
                + " : " + m.getPayload() + " [clock=" + clock + "]");
        sendACK(m.getDest(), m.getSender());
    }

    /**
     * Attente d'un message précis (synchronisation).
     *
     * @param o    contenu attendu
     * @param from identifiant de l'expéditeur attendu
     */
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

    /**
     * Ajoute un accusé de réception à la boîte aux lettres.
     *
     * @param m ACK reçu
     */
    private void receiveACK(ACKMessage m) {
        mailbox.addMessage(m);
    }

    // ==========================
    // Envoi des messages
    // ==========================

    /**
     * Envoie asynchrone d'un message direct.
     *
     * @param o  contenu du message
     * @param to destinataire
     */
    public void sendTo(Object o, int to) {
        clock++;
        System.out.println("P" + id + " envoie à P" + to + " : " + o + " [clock=" + clock + "]");
        MessageTo m = new MessageTo(clock, id, to, o);
        bus.postEvent(m);
    }

    /**
     * Envoie d'un accusé de réception.
     *
     * @param from expéditeur de l’ACK
     * @param to   destinataire de l’ACK
     */
    public void sendACK(int from, int to) {
        ACKMessage m = new ACKMessage(from, to);
        bus.postEvent(m);
    }

    /**
     * Envoie synchrone d’un message direct (attente d’ACK).
     *
     * @param o  contenu du message
     * @param to destinataire
     */
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

    // ==========================
    // Section critique
    // ==========================

    /** Demande l’accès à la section critique (attente du jeton). */
    public void requestSC() {
        try {
            lockToken.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Libère la section critique et renvoie le jeton au suivant. */
    public void releaseSC() {
        lockSC.release();
    }

    // ==========================
    // Synchronisation
    // ==========================

    /** Synchronisation globale de tous les processus. */
    public void synchronize() {
        broadcastSync("SYNC", 0);
    }

    // ==========================
    // Broadcast
    // ==========================

    /**
     * Diffuse un message à tous les processus (sans ACK).
     *
     * @param o contenu du message
     */
    public void broadcast(Object o) {
        BroadcastMessage m = new BroadcastMessage(++clock, id, o);
        System.out.println("P" + id + " broadcast : " + o);
        bus.postEvent(m);
    }

    /**
     * Diffuse un message avec synchronisation (attente d’ACK).
     *
     * @param o    contenu du message
     * @param from identifiant de l’émetteur principal
     */
    public void broadcastSync(Object o, int from) {
        if (this.id == from) {
            // Envoyer à tous les autres
            for (int i = 0; i < nbProcess; i++) {
                if (i != from) {
                    sendTo(o, i);
                }
            }

            // Attendre les ACK de tous
            Set<Integer> ackSenders = new HashSet<>();
            while (ackSenders.size() < nbProcess - 1) {
                Message msg = mailbox.getMsg();

                if (msg instanceof ACKMessage) {
                    ackSenders.add(msg.getSender());
                    System.out.println("P" + id + " a reçu ACK de P" + msg.getSender());
                } else {
                    mailbox.addMessage(msg);
                }
            }
        } else {
            // Recevoir le message du processus "from"
            boolean received = false;
            while (!received) {
                Message msg = mailbox.getMsg();
                if (msg.getSender() == from && msg.getPayload().equals(o)) {
                    received = true;
                    sendACK(this.id, from);
                } else {
                    mailbox.addMessage(msg);
                }
            }
        }
    }
}
