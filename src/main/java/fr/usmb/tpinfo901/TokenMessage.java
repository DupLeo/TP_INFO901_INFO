package fr.usmb.tpinfo901;

public class TokenMessage extends Message {
    private int dest;

    public TokenMessage(int sender, int dest) {
        super(-1, sender, "TOKEN"); // horloge spéciale (-1) + payload par défaut
        this.dest = dest;
    }

    public int getDest() {
        return dest;
    }

    @Override
    public String toString() {
        return "TokenMessage{from=P" + getSender() + ", to=P" + dest + "}";
    }}
