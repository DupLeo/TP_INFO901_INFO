package fr.usmb.tpinfo901;

import java.util.ArrayList;

public class Launcher{

	public static void main(String[] args){

		ArrayList<Process> processes = new ArrayList<>();

		int maxNbProcess = 4;

		for(int i=0; i<maxNbProcess; i++) {
			processes.add(new Process("P"+i, maxNbProcess));
		}

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
