package fr.usmb.tpinfo901;

public class Process implements Runnable {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private Com com;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;

    public Process(String name, int maxNbProcess) {
        this.com = new Com(id, maxNbProcess);
        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();
    }

    public void run() {
        int loop = 0;
        Message msg = null;

        System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while (this.alive) {
            System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
            try {
                Thread.sleep(500);

                if (this.getName().equals("P0")) {
                    this.com.sendTo("j'appelle 1 et je te recontacte après", 1);

                    this.com.sendToSync("J'ai laissé un message à 1, je le rappellerai après, on se sychronise tous et on attaque la partie ?", 2);

                    this.com.sendToSync("1 est OK pour jouer, on se synchronise et c'est parti!", 1);

                    this.com.synchronize();

                    this.com.requestSC();
                    if (this.com.mailbox.isEmpty()) {
                        System.out.println("Catched !");
                        this.com.broadcast("J'ai gagné !!!");
                    } else {
                        msg = this.com.mailbox.getMsg();
                        System.out.println(msg.getSender() + " à eu le jeton en premier");
                    }
                    this.com.releaseSC();

                }
                if (this.getName().equals("P1")) {
                    System.out.println("Pour P" + this.id + " nombre de message : " + this.com.mailbox.getMessageCount());

                    if (!this.com.mailbox.isEmpty()) {
                        msg = this.com.mailbox.getMsg();
                        this.com.sendToSync(msg.toString(), 0);

                        this.com.synchronize();

                        this.com.requestSC();
                        if (this.com.mailbox.isEmpty()) {
                            System.out.println("Catched !");
                            this.com.broadcast("J'ai gagné !!!");
                        } else {
                            msg = this.com.mailbox.getMsg();
                            System.out.println(msg.getSender() + " à eu le jeton en premier");
                        }
                        this.com.releaseSC();
                    }
                }
                if (this.getName().equals("P2")) {
                    msg = this.com.mailbox.getMsg();
                    this.com.sendToSync(msg.toString(), 0);
                    this.com.sendToSync("OK", 0);

                    this.com.synchronize();

                    this.com.requestSC();
                    if (this.com.mailbox.isEmpty()) {
                        System.out.println("Catched !");
                        this.com.broadcast("J'ai gagné !!!");
                    } else {
                        msg = this.com.mailbox.getMsg();
                        System.out.println(msg.getSender() + " à eu le jeton en premier");
                    }
                    this.com.releaseSC();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            loop++;
        }

        System.out.println(Thread.currentThread().getName() + " stopped");
        this.dead = true;
    }

    public String getName() {
        return thread.getName();
    }

    public void waitStoped() {
        while (!this.dead) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.alive = false;
    }
}