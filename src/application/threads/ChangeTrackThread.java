package application.threads;

import java.util.Random;

import application.model.Car;

public class ChangeTrackThread extends Thread {

	private Master master;
	private Car[][] street;
	private Double c;
	private Integer size;

	public ChangeTrackThread(Master master, Car[][] street, Double c, Integer size) {
		this.master = master;
		this.street = street;
		this.c = c;
		this.size = size;
	}

	@Override
	public void run() {
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			changeTrack(index);
			index = master.getNextRange();
		}

	}

	private void changeTrack(Integer index) {
		for (int t = 0; t < street.length; t++) {
			for (int i = index; i <= index + size; i++) {
				if(i>=street[0].length)
					break;
				if (street[t][i] != null && street[t][i].getSpeed() > 0) {
					if (street[t][i] != null) {
						if (checkTrackChange(i, t, 1 - t)) {
							street[t][i].setChangeTrack(true);
						}
					}
				}
			}
		}
	}

	private boolean checkTrackChange(int pos, int currentTrack, int targetTrack) {

		// check behind cars
		for (int i = 1; i <= 5; i++) {
			int check = pos - i;
			if (check < 0)
				check += street[0].length;
			if (street[targetTrack][check] != null) {
				return false;
			}
		}

		// check neighbour car
		if (street[targetTrack][pos] != null)
			return false;

		// check previous cars
		for (int i = 1; i <= street[currentTrack][pos].getSpeed() + 1; i++) {
			int check = pos + i;
			if (check >= street[0].length)
				check -= street[0].length;
			if (street[targetTrack][check] != null) {
				return false;
			}
		}

		// Check track change possibility
		Random random = new Random();
		if (random.nextFloat() < c) {
			return true;
		} else {
			return false;
		}
	}

}
