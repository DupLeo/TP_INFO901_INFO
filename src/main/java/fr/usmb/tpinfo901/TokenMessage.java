package fr.usmb.tpinfo901;

public class TokenMessage extends Message {
    private int to;    // destinataire
    private int from;  // expéditeur

    public TokenMessage(int clock, int from, int to) {
        super(clock, "TOKEN");
        this.to = to;
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }
}
