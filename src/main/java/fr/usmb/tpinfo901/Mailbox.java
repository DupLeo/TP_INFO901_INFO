package fr.usmb.tpinfo901;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Boîte aux lettres (Mailbox) d’un processus.
 * <p>
 * Chaque processus possède une {@code Mailbox} qui stocke les messages
 * reçus. L’utilisation d’une {@link LinkedBlockingQueue} garantit la
 * sécurité des accès concurrents entre threads.
 * </p>
 */
public class Mailbox {

    /** File de messages reçus, thread-safe et bloquante. */
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    /**
     * Ajoute un message dans la boîte aux lettres.
     * <p>
     * Si la file est pleine (cas théorique avec LinkedBlockingQueue sans limite),
     * l’opération bloque jusqu’à ce qu’une place se libère.
     * </p>
     *
     * @param msg le message à ajouter
     */
    public void addMessage(Message msg) {
        try {
            messages.put(msg); // bloque si la queue est pleine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Récupère et retire le prochain message de la boîte aux lettres.
     * <p>
     * Cette méthode est bloquante tant qu’aucun message n’est disponible.
     * </p>
     *
     * @return le message reçu, ou {@code null} si le thread est interrompu
     */
    public Message getMsg() {
        try {
            return messages.take(); // bloque tant qu'il n'y a pas de message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Retourne le nombre de messages actuellement dans la boîte aux lettres.
     *
     * @return taille de la file des messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Indique si la boîte aux lettres est vide.
     *
     * @return {@code true} si aucun message n’est en attente,
     *         {@code false} sinon
     */
    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
