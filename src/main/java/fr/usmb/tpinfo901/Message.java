package fr.usmb.tpinfo901;

abstract public class Message {
    private int clock;
    private String text;
    public Message(int clock, String text){
        this.clock = clock;
        this.text = text;
    }

    public Message getMessage(){
        return this;
    }

    public int getClock(){
        return this.clock;
    }

    @Override
    public String toString(){
        return "Message: " + this.text + " clock: " + this.clock;
    }

}
