package fr.usmb.tpinfo901;

public class MessageTo extends Message {
    private int dest;

    public MessageTo(int clock, int sender, int dest, Object payload) {
        super(clock, sender, payload);
        this.dest = dest;
    }

    public int getDest() {
        return dest;
    }
}

