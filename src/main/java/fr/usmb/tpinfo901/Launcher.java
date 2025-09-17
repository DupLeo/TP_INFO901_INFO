package fr.usmb.tpinfo901;

import java.util.ArrayList;

public class Launcher{

	public static void main(String[] args){

		ArrayList<Process> processes = new ArrayList<Process>();

		for(int i=0; i<Process.maxNbProcess; i++) {
			processes.add(new Process("P"+i));
		}

		int last = Process.maxNbProcess - 1;
		TokenMessage initial = new TokenMessage(0, last, 0);
		EventBusService.getInstance().postEvent(initial);

		try{
			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}

		for(int i=0; i<Process.maxNbProcess; i++) {
			processes.get(i).stop();
		}
	}
}
