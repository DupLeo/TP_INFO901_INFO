package fr.usmb.tpinfo901;

/**
 * Message de diffusion (broadcast) envoyé à tous les processus.
 * <p>
 * Un {@code BroadcastMessage} est une extension de {@link Message} qui
 * représente l’envoi d’une information à l’ensemble des processus
 * d’un système distribué.
 * </p>
 * <p>
 * Contrairement à {@link MessageTo}, il n’est pas destiné à un seul
 * destinataire, mais doit être reçu par tous les autres processus.
 * </p>
 */
public class BroadcastMessage extends Message {

    /**
     * Construit un message de type broadcast.
     *
     * @param clock   horloge logique de Lamport associée à l’événement
     * @param sender  identifiant du processus émetteur
     * @param payload contenu transporté par le message
     */
    public BroadcastMessage(int clock, int sender, Object payload) {
        super(clock, sender, payload);
    }
}
