package fr.usmb.tpinfo901;

import com.google.common.eventbus.Subscribe;



public class Process  implements Runnable {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private Com com;

    public Process(String name){
        this.com = new Com();

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();
    }

    public void run(){
        int loop = 0;

        System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while(this.alive){
            System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
            try{
                Thread.sleep(500);

                if (this.getName() == "P0"){
                    this.com.sendTo("j'appelle 2 et je te recontacte après", 1);

                    this.com.sendToSync("J'ai laissé un message à 2, je le rappellerai après, on se sychronise tous et on attaque la partie ?", 2);
                    this.com.recevFromSync(msg, 2);

                    this.com.sendToSync("2 est OK pour jouer, on se synchronise et c'est parti!",1);

                    this.com.synchronize();

                    this.com.requestSC();
                    if (this.com.mailbox.isEmpty()){
                        print("Catched !");
                        this.com.broadcast("J'ai gagné !!!");
                    }else{
                        msg = this.com.mailbox.getMsg();
                        System.out.println(msg.getSender() + " à eu le jeton en premier");
                    }
                    this.com.releaseSC();

                }
                if (this.getName() == "P1"){
                    if (!this.com.mailbox.isEmpty()){
                        this.com.mailbox.getMessage();
                        this.com.recevFromSync(msg, 0);

                        this.com.synchronize();

                        this.com.requestSC();
                        if (this.com.mailbox.isEmpty()){
                            print("Catched !");
                            this.com.broadcast("J'ai gagné !!!");
                        }else{
                            msg = this.com.mailbox.getMsg();
                            print(str(msg.getSender())+" à eu le jeton en premier");
                        }
                        this.com.releaseSC();
                    }
                }
                if(this.getName() == "P2"){
                    this.com.recevFromSync(msg, 0);
                    this.com.sendToSync("OK", 0);

                    this.com.synchronize();

                    this.com.requestSC();
                    if (this.com.mailbox.isEmpty()){
                        print("Catched !");
                        this.com.broadcast("J'ai gagné !!!");
                    }else{
                        msg = this.com.mailbox.getMsg();
                        print(str(msg.getSender())+" à eu le jeton en premier");
                    }
                    this.com.releaseSC();
                }


            }catch(Exception e){
                e.printStackTrace();
            }
            loop++;
        }

        System.out.println(Thread.currentThread().getName() + " stopped");
        this.dead = true;
    }

    public void waitStoped(){
        while(!this.dead){
            try{
                Thread.sleep(500);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        this.alive = false;
    }
}