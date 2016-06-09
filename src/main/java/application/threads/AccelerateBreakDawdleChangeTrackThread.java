package application.threads;

import application.model.Car;

import java.util.SplittableRandom;

public class AccelerateBreakDawdleChangeTrackThread extends Thread {
	private Master master;
	private Car[][] street;
	private Integer size;
	private Double p, p0;

	private SplittableRandom random = new SplittableRandom();


	public AccelerateBreakDawdleChangeTrackThread(Master master, Car[][] street, Integer size, Double p, Double p0) {
		this.master = master;
		this.street = street;
		this.size = size;
		this.p = p;
		this.p0 = p0;
	}

	@Override
	public void run() {
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			for (int t = 0; t < street.length; t++) {
				for (int i = index; i <= index + size; i++) {

					if (i >= street[0].length)
						break;

					accelerateCar(t, i);
					breakCar(t, i);
					dawdleCar(t, i);
				}
			}
			index = master.getNextRange();
		}
	}

	private void accelerateCar(int t, int i) {
		if (street[t][i] != null) {
			if (street[t][i].getSpeed() < street[t][i].getMaxSpeed()) {
				street[t][i].incrementSpeed();
			}
		}
	}

	private void breakCar(int t, int i) {
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

	private void dawdleCar(int t, int i) {
		Car c = street[t][i];
		if (c != null && c.getSpeed() > 0) {
			if (c.getSpeed() > 1) {
				if (random.nextDouble() < p) {
					c.setSpeed(c.getSpeed() - 1);
				}
			} else {
				if (random.nextDouble() < p0) {
					c.setSpeed(c.getSpeed() - 1);
				}
			}
		}
	}
}
