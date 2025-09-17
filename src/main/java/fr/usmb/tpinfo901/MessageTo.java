package fr.usmb.tpinfo901;

public class MessageTo extends Message {
    private int to;     // destinataire
    private int from;   // expéditeur

    public MessageTo(int clock, String text, int from, int to) {
        super(clock, text);
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
