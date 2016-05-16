package application.threads;

import java.util.Date;

import application.model.Car;

public class MoveThread extends Thread {

	private Master master;
	private Car[][] street;
	private Integer size;

	public MoveThread(Master master, Car[][] street, Integer size) {
		this.master = master;
		this.street = street;
		this.size = size;
	}

	@Override
	public void run() {
		Long phi = 0L;
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			Date d = new Date();
			moveCars(index);
			phi += new Date().getTime() -d.getTime();
			index = master.getNextRange();
		}
		master.addPhi(phi);
	}

	private void moveCars(Integer index) {
		for (int t = 0; t < street.length; t++) {
			boolean moving = true;

			int i = index;
			while (moving) {
				if (i >= street[0].length)
					break;
				Car c = street[t][i];
				if (c != null) {
					synchronized (c) {
						if (c.getMoved() <= master.getNumSteps()) {

							c.setMoved((int) (master.getNumSteps()+1));
							if (c.getSpeed() > 0) {
								int pos = i + c.getSpeed();
								if (pos >= street[0].length) {
									pos -= street[0].length;
									moving = false;
								}
								if (street[t][pos] != null) {
									System.out.println("crash while moving(" + index + "-" + (index + size) + "//"
											+ index + ") :" + t + "/" + i + "-->" + pos + "____" + c.getMoved() + "-"
											+ master.getNumSteps());
								}
								street[t][pos] = c;
								street[t][i] = null;
								i = pos;
								if (pos > index + size) {
									master.incrementRange(index + size + master.getMAX_SPEED());
								}
							}
						}
					}
				}

				i++;
				if (i > index + size) {
					moving = false;
				}
			}
		}
	}
}
