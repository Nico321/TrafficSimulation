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
				if (i >= street[0].length)
					break;
				if (street[t][i] != null && street[t][i].getSpeed() > 0) {
					if (street[t][i] != null) {
						if (t > 0 && checkTrackChange(i, t, t - 1)) {
							street[t][i].setChangeTrack(true);
							street[t][i].setTargetTrack(t - 1);
						}
						if (t < (street.length - 1) && checkTrackChange(i, t, t + 1)) {
							street[t][i].setChangeTrack(true);
							street[t][i].setTargetTrack(t + 1);
						}
					}
				}
			}
		}
	}

	private boolean checkTrackChange(int pos, int currentTrack, int targetTrack) {
		// check neighbour car
		if (street[targetTrack][pos] != null)
			return false;

		if (targetTrack < street.length-1){
			if(street[targetTrack + 1][pos] != null){
				if(street[targetTrack + 1][pos].isChangeTrack() && street[targetTrack + 1][pos].getTargetTrack() == targetTrack)
					return false;
			}
		}
		
		if (targetTrack > 0){
			if(street[targetTrack - 1][pos] != null){
				if(street[targetTrack - 1][pos].isChangeTrack() && street[targetTrack - 1][pos].getTargetTrack() == targetTrack){
					return false;
				}
			}
		}

		boolean blockOnOwnStreet = false;

		// Check track change possibility
		Random random = new Random();
		if (random.nextFloat() < c) {

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
			for (int i = 1; i <= 5; i++) {
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
