package fr.usmb.tpinfo901;

/**
 * Représente un message spécial utilisé pour la gestion du jeton
 * dans l'algorithme de section critique distribuée basé sur un anneau.
 * <p>
 * Le {@code TokenMessage} circule de processus en processus et permet
 * d'assurer l'exclusion mutuelle. Un processus ne peut entrer dans
 * la section critique que s'il détient le jeton.
 * </p>
 *
 * <p>
 * Contrairement aux autres messages, le jeton n'incrémente pas l'horloge
 * logique car il est considéré comme un message système.
 * Pour indiquer cela, l'horloge est fixée à {@code -1}.
 * </p>
 */
public class TokenMessage extends Message {
    /**
     * Identifiant du destinataire du jeton.
     */
    private final int dest;

    /**
     * Construit un nouveau {@code TokenMessage}.
     *
     * @param sender l'identifiant du processus expéditeur
     * @param dest   l'identifiant du processus destinataire
     */
    public TokenMessage(int sender, int dest) {
        super(-1, sender, "TOKEN"); // horloge spéciale (-1) + payload par défaut
        this.dest = dest;
    }

    /**
     * Retourne l'identifiant du destinataire.
     *
     * @return l'identifiant du processus destinataire
     */
    public int getDest() {
        return dest;
    }

    /**
     * Fournit une représentation textuelle du message de jeton.
     *
     * @return une chaîne décrivant l'expéditeur et le destinataire du jeton
     */
    @Override
    public String toString() {
        return "TokenMessage{from=P" + getSender() + ", to=P" + dest + "}";
    }
}
