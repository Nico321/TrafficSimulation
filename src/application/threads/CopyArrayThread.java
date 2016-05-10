package application.threads;

import application.model.Car;

public class CopyArrayThread extends Thread {
	private Car[][] street, originalStreet;
	int start, stop;

	public CopyArrayThread(Car[][] street, Car[][] originalStreet, int start, int stop) {
		this.street=street;
		this.originalStreet = originalStreet;
		this.start=start;
		this.stop = stop;
	}

	@Override
	public void run() {
		for (int t = 0; t < street.length; t++) {
			for (int i = start; i < stop && i < street[0].length; i++) {
				if (street[t][i] != null) {
					originalStreet[t][i] = new Car(street[t][i].getSpeed());
				}
			}
		}
	}
}
