package application.threads;

import java.util.Date;

import application.model.Car;

public class BreakThread extends Thread {
	public BreakThread(Master master, Car[][] street, Integer size) {
		this.master = master;
		this.street = street;
		this.size = size;
	}

	private Master master;
	private Car[][] street;
	private Integer size;

	@Override
	public void run() {
		Long phi = 0L;
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			Date d = new Date();
			breakCars(index);
			phi += new Date().getTime() -d.getTime();
			index = master.getNextRange();
		}
		master.addPhi(phi);
	}

	private void breakCars(Integer index) {
		for (int t = 0; t < street.length; t++) {
			for (int i = index; i <= index + size; i++) {
				if(i>=street[0].length)
					break;
				if (street[t][i] != null && street[t][i].getSpeed() > 0) {
					for (int j = 1; j <= street[t][i].getSpeed(); j++) {
						int check = i + j;
						if (check >= street[0].length)
							check -= street[0].length;
						if (street[t][check] != null) {
							street[t][i].setSpeed(j - 1);
							break;
						}
					}
				}
			}
		}
	}
}
