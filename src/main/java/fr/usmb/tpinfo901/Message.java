package fr.usmb.tpinfo901;

abstract public class Message {
    private final int clock;
    private final Object payload;   // objet générique
    private final int sender;       // identifiant du processus

    public Message(int clock, int sender, Object payload) {
        this.clock = clock;
        this.sender = sender;
        this.payload = payload;
    }

    public int getClock() {
        return clock;
    }

    public int getSender() {
        return sender;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{sender=P" + sender + ", clock=" + clock + ", payload=" + payload + "}";
    }
}
