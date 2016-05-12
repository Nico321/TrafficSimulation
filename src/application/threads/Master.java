package application.threads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.Controller;
import application.model.Car;

public class Master {
	private volatile Integer index;
	private Integer streetSize;
	private Integer MAX_SPEED;
	private Integer rangeSize;
	private Integer numOfThreads;
	private Car[][] street;
	private Double c, p, p0;
	private Controller controller;
	private Date startTime;
	private Long numSteps;
	private boolean pause = false, stop = false;
	private Integer framerate;

	public Integer getFramerate() {
		return framerate;
	}

	public void setFramerate(Integer framerate) {
		this.framerate = framerate;
	}

	public Master(int streetSize, int rangeSize, int numOfThreads, Double c, Car[][] street, Integer MAX_SPEED,
			Double p, Double p0, Controller controller, Integer framerate) {
		this.setStreetSize(streetSize);
		this.index = streetSize;
		this.rangeSize = rangeSize;
		this.numOfThreads = numOfThreads;
		this.MAX_SPEED = MAX_SPEED;
		this.street = street;
		this.controller = controller;
		this.c = c;
		this.p = p;
		this.p0 = p0;

		this.startTime = new Date();
		this.numSteps = 0L;
		this.framerate = framerate;
	}

	public void startSimulation() {
		this.index = 0;

		while (!pause && !stop) {
			changeTracks();
			accelerate();
			breakCars();
			dawdle();
			move();

			this.numSteps++;
			if (this.numSteps % framerate == 0)
				controller.updateGraphics();

			//countCars();
		}
	}

	private void countCars() {
		int counter = 0;
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < street[0].length; i++) {
				if(street[t][i] != null)
					counter++;
			}
		}
		System.out.println(counter);
	}

	private void move() {
		List<MoveThread> moveThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			MoveThread t = new MoveThread(this, street, rangeSize);
			t.start();
			moveThreads.add(t);
		}
		for (MoveThread t : moveThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.index = 0;
	}

	private void dawdle() {
		List<DawdleThread> dawdleThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			DawdleThread t = new DawdleThread(this, street, rangeSize, p, p0);
			t.start();
			dawdleThreads.add(t);
		}
		for (DawdleThread t : dawdleThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.index = 0;
	}

	private void breakCars() {
		List<BreakThread> breakThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			BreakThread t = new BreakThread(this, street, rangeSize);
			t.start();
			breakThreads.add(t);
		}
		for (BreakThread t : breakThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.index = 0;
	}

	private void accelerate() {
		List<AccelerateThread> accelerateThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			AccelerateThread t = new AccelerateThread(this, street, rangeSize);
			t.start();
			accelerateThreads.add(t);
		}
		for (AccelerateThread t : accelerateThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.index = 0;
	}

	private void changeTracks() {
		List<ChangeTrackThread> changeTrackThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			ChangeTrackThread t = new ChangeTrackThread(this, street, c, rangeSize);
			t.start();
			changeTrackThreads.add(t);
		}
		for (ChangeTrackThread t : changeTrackThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.index = 0;
	}

	public synchronized Integer getNextRange() {
		index += (rangeSize + 1);
		return (index - (rangeSize + 1));
	};

	public synchronized void incrementRange(int index) {
		this.index = index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getRangeSize() {
		return rangeSize;
	}

	public void setRangeSize(Integer rangeSize) {
		this.rangeSize = rangeSize;
	}

	public Integer getNumOfThreads() {
		return numOfThreads;
	}

	public void setNumOfThreads(Integer numOfThreads) {
		this.numOfThreads = numOfThreads;
	}

	public Integer getMAX_SPEED() {
		return MAX_SPEED;
	}

	public void setMAX_SPEED(Integer mAX_SPEED) {
		MAX_SPEED = mAX_SPEED;
	}

	public Integer getStreetSize() {
		return streetSize;
	}

	public void setStreetSize(Integer streetSize) {
		this.streetSize = streetSize;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getNumSteps() {
		return numSteps;
	}

	public void setNumSteps(Long numSteps) {
		this.numSteps = numSteps;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
