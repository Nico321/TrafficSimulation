package application.threads;

import application.model.Car;

public class AccelerateThread extends Thread {
	private Master master;
	private Car[][] street;
	private Integer size;

	public AccelerateThread(Master master, Car[][] street, Integer size) {
		this.master = master;
		this.street = street;
		this.size = size;
	}

	@Override
	public void run() {
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			accelerate(index);
			index = master.getNextRange();
		}
	}

	private void accelerate(Integer index) {

		
		for (int t = 0; t < street.length ; t++) {
			for (int i = index; i <= index + size; i++) {
				if (i >= street[0].length)
					break;
				Car c = street[t][i];
				if (c != null) {
					if (c.getSpeed() < c.getMaxSpeed()) {
						c.setSpeed(c.getSpeed() + 1);
					}
					if (c.isChangeTrack()) {
						if (street[c.getTargetTrack()][i] != null)
							System.out.println("crash while changing tracks:" + street[t][i] + "-->"
									+ c.getTargetTrack());

						street[t][i].setChangeTrack(false);
						street[c.getTargetTrack()][i] = street[t][i];
						street[t][i] = null;
					}
				}
			}
		}
	}
}
