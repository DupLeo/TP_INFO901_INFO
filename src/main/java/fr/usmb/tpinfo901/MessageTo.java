package fr.usmb.tpinfo901;

/**
 * Représente un message point-à-point échangé entre deux processus.
 * <p>
 * Contrairement à un {@link BroadcastMessage}, qui est envoyé à tous
 * les processus, le {@code MessageTo} est destiné à un processus
 * spécifique identifié par son {@code dest}.
 * </p>
 *
 * <p>
 * Ce message est associé à une horloge logique (Lamport),
 * ce qui permet de maintenir un ordre cohérent entre les événements
 * distribués.
 * </p>
 */
public class MessageTo extends Message {
    /**
     * Identifiant du destinataire du message.
     */
    private final int dest;

    /**
     * Construit un message point-à-point.
     *
     * @param clock   horloge logique au moment de l'envoi
     * @param sender  identifiant du processus expéditeur
     * @param dest    identifiant du processus destinataire
     * @param payload contenu du message (charge utile)
     */
    public MessageTo(int clock, int sender, int dest, Object payload) {
        super(clock, sender, payload);
        this.dest = dest;
    }

    /**
     * Retourne l'identifiant du destinataire.
     *
     * @return identifiant du processus destinataire
     */
    public int getDest() {
        return dest;
    }
}
