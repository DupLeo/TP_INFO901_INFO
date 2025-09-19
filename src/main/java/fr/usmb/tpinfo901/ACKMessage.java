package fr.usmb.tpinfo901;

public class ACKMessage extends Message{

        private int dest;

        public ACKMessage(int sender, int dest) {
            super(0, sender, "ACK");
            this.dest = dest;
        }

        public int getDest() {
            return dest;
        }

}
