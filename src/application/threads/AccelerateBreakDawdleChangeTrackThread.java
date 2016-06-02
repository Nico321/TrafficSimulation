package application.threads;

import java.util.SplittableRandom;
import application.model.Car;

public class AccelerateBreakDawdleChangeTrackThread extends Thread {
	private Master master;
	private Car[][] street;
	private Integer size, MAX_SPEED;
	private Double p, p0, c;

	private SplittableRandom random = new SplittableRandom();


	public AccelerateBreakDawdleChangeTrackThread(Master master, Car[][] street, Integer size, Double p, Double p0,
			Integer MAX_SPEED, Double c) {
		this.master = master;
		this.street = street;
		this.size = size;
		this.p = p;
		this.p0 = p0;
		this.MAX_SPEED = MAX_SPEED;
		this.c = c;
	}

	@Override
	public void run() {
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			for (int t = 0; t < street.length; t++) {
				for (int i = index; i <= index + size; i++) {

					if (i >= street[0].length)
						break;

					changeTrackCar(t, i);
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
				;
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

	private void changeTrackCar(int t, int i) {
		if (street[t][i] != null && street[t][i].getSpeed() > 0) {
			if (street[t][i] != null) {
				if (t > 0 && checkTrackChange(i, t, t - 1)) {
					changeTrack(t, i, t - 1);
				} else if (t < (street.length - 1) && checkTrackChange(i, t, t + 1)) {
					changeTrack(t, i, t + 1);
				}
			}
		}
	}

	private void changeTrack(int t, int i, int targetTrack) {
		if (street[targetTrack][i] != null)
			System.out.println(
					"crash while changing tracks:" + street[t][i] + "-->" + targetTrack + "-" + street[targetTrack][i]);
		street[targetTrack][i] = street[t][i];
		street[t][i] = null;
	}

	private boolean checkTrackChange(int pos, int currentTrack, int targetTrack) {
		// check neighbour car
		if (street[targetTrack][pos] != null)
			return false;

		boolean blockOnOwnStreet = false;

		// Check track change possibility
		if (random.nextDouble() < c) {


			// check previous cars
			for (int i = 1; i <= street[currentTrack][pos].getSpeed() + 1; i++) {
				int check = pos + i;
				if (check >= street[0].length)
					check -= street[0].length;
				if (street[targetTrack][check] != null) {
					return false;
				}
				if (street[currentTrack][check] != null) {
					blockOnOwnStreet = true;
				}
			}

			if (!blockOnOwnStreet)
				return false;

			// check behind cars
			for (int i = 1; i <= MAX_SPEED; i++) {
				int check = pos - i;
				if (check < 0)
					check += street[0].length;
				if (street[targetTrack][check] != null) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
