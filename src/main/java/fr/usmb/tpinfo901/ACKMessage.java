package fr.usmb.tpinfo901;

/**
 * Message de type ACK (accusé de réception).
 * <p>
 * Un {@code ACKMessage} est utilisé pour confirmer la réception d’un message
 * par un processus destinataire. Il permet notamment d’assurer la fiabilité
 * lors des communications synchrones ou des diffusions (broadcast).
 * </p>
 */
public class ACKMessage extends Message {

    /** Identifiant du processus destinataire de l'ACK. */
    private final int dest;

    /**
     * Construit un message de type ACK.
     *
     * @param sender identifiant du processus qui envoie l’ACK
     * @param dest   identifiant du processus destinataire de l’ACK
     */
    public ACKMessage(int sender, int dest) {
        super(0, sender, "ACK"); // clock = 0 (ne doit pas impacter l'horloge logique)
        this.dest = dest;
    }

    /**
     * Retourne l’identifiant du destinataire de cet ACK.
     *
     * @return identifiant du processus destinataire
     */
    public int getDest() {
        return dest;
    }
}
