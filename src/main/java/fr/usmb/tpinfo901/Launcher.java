package fr.usmb.tpinfo901;

import java.util.ArrayList;

public class Launcher{

	public static void main(String[] args){

		ArrayList<Process> processes = new ArrayList<Process>();

		int maxNbProcess = 4;

		for(int i=0; i<maxNbProcess; i++) {
			processes.add(new Process("P"+i, maxNbProcess));
		}

		TokenMessage initial = new TokenMessage(0, 1); // P0 a le jeton, envoie au P1 après libération
		EventBusService.getInstance().postEvent(initial);

		try{
			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}

		for(int i=0; i<maxNbProcess; i++) {
			processes.get(i).stop();
		}
	}
}
