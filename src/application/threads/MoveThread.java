package application.threads;

import application.model.Car;

public class MoveThread extends Thread {

	private Master master;
	private Car[][] street, originalStreet;
	private Integer size, MAX_SPEED;

	public MoveThread(Master master, Car[][] street, Integer size, Car[][] originalStreet, Integer MAX_SPEED) {
		this.master = master;
		this.street = street;
		this.size = size;
		this.originalStreet = originalStreet;
		this.MAX_SPEED = MAX_SPEED;
	}

	@Override
	public void run() {
		Integer index = master.getNextRange();
		while (index < street[0].length) {
			moveCars(index);
			index = master.getNextRange();
		}
	}

	private void moveCars(Integer index) {
		for (int t = 0; t < street.length; t++) {
			boolean moving = true;
			int newIndex = index;
			if(index > 0){
				
				//Verschiebt den Index und guckt ob vorher schon ein Auto in einen zweiten Arraybereich geschoben wurde
				for (int i = index-1; i >= (index - MAX_SPEED); i--) {
					if (originalStreet[t][i] != null) {
						if ((i + originalStreet[t][i].getSpeed()) >= index) {
							newIndex = i + originalStreet[t][i].getSpeed() +1;
						}
						break;
					}
				}
			}

			int i = newIndex;
			while (moving) {
				if (i >= street[0].length)
					break;
				Car c = street[t][i];
				if (c != null && c.getSpeed() > 0) {
					int pos = i + c.getSpeed();
					if (pos >= street[0].length) {
						pos -= street[0].length;
						moving = false;
					}
					if (street[t][pos] != null) {
						System.out.println("crash while moving(" + index + "-" + (index + size) + "//"+newIndex+") :" + t + "/" + i
								+ "-->" + pos);
						System.out.println(street[t][pos] +"-"+originalStreet[t][pos]);
					}
					street[t][pos] = c;
					street[t][i] = null;
					i = pos;
				}
				i++;
				if (i > index + size) {
					moving = false;
				}
			}
		}
	}
}
