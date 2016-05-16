package application.threads;

import java.util.Date;
import java.util.Random;

import application.model.Car;

public class DawdleThread extends Thread {
	private Master master;
	private Car[][] street;
	private Integer size;
	private Double p, p0;

	public DawdleThread(Master master, Car[][] street, Integer size, Double p, Double p0) {
		this.master = master;
		this.street = street;
		this.size = size;
		this.p = p;
		this.p0 = p0;
	}

	@Override
	public void run() {
		Long phi = 0L;
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			Date d = new Date();
			dawdleCars(index);
			phi += new Date().getTime() -d.getTime();
			index = master.getNextRange();
		}
		master.addPhi(phi);
	}

	private void dawdleCars(Integer index) {
		Random random = new Random();
		for (int t = 0; t < street.length; t++) {
			for (int i = index; i <= index + size; i++) {
				if(i>=street[0].length)
					break;
				Car c = street[t][i];
				if (c != null && c.getSpeed() > 0) {
					if (c.getSpeed() > 1) {
						if (random.nextFloat() < p) {
							c.setSpeed(c.getSpeed() - 1);
						}
					} else {
						if (random.nextFloat() < p0) {
							c.setSpeed(c.getSpeed() - 1);
						}
					}
				}
			}
		}
	}
}
