package application.threads;

import application.Controller;
import application.model.Car;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	private Long sigma, phi, kappa;
	private Integer anzIteration;

	public Master(int streetSize, int rangeSize, int numOfThreads, Double c, Car[][] street, Integer MAX_SPEED,
			Double p, Double p0, Controller controller, Integer framerate, Long sigma,
			Integer anzIteration) {

		Date startConstructDate = new Date();

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

		this.kappa = 0L;
		this.phi = 0L;

		this.anzIteration = anzIteration;

		this.sigma = sigma + new Date().getTime() - startConstructDate.getTime();
	}

	public Integer getFramerate() {
		return framerate;
	}

	public void setFramerate(Integer framerate) {
		this.framerate = framerate;
	}

	public void startSimulation() {
		this.index = 0;

		while (!pause && !stop && (anzIteration == -1 || numSteps < anzIteration)) {
			changeTrack();
			accelerateBreakDawdle();
			move();

			Date d = new Date();

			this.numSteps++;
			if (this.numSteps % framerate == 0)
				controller.updateGraphics();

			sigma += new Date().getTime() - d.getTime();
		}

		if (controller != null)
			controller.setAnalytics(sigma, phi, kappa);

		if (controller != null && numSteps >= anzIteration && anzIteration != -1)
			controller.stopSimulation();
	}

	private void changeTrack() {
		Date d = new Date();

		List<ChangeTrackThread> threads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			ChangeTrackThread t = new ChangeTrackThread(this, street,
					rangeSize, MAX_SPEED, c);
			t.start();
			threads.add(t);
		}

		this.kappa += new Date().getTime() - d.getTime();
		d = new Date();
		for (ChangeTrackThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.index = 0;
		this.addPhi(new Date().getTime() - d.getTime());
	}

	private void move() {
		Date d = new Date();
		List<MoveThread> moveThreads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			MoveThread t = new MoveThread(this, street, rangeSize);
			t.start();
			moveThreads.add(t);
		}

		this.kappa += new Date().getTime() - d.getTime();
		d = new Date();
		for (MoveThread t : moveThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.index = 0;
		this.addPhi(new Date().getTime() - d.getTime());
	}

	private void accelerateBreakDawdle() {
		Date d = new Date();

		List<AccelerateBreakDawdleThread> threads = new ArrayList<>();
		for (int i = 0; i < numOfThreads; i++) {
			AccelerateBreakDawdleThread t = new AccelerateBreakDawdleThread(this, street,
					rangeSize, p, p0);
			t.start();
			threads.add(t);
		}

		this.kappa += new Date().getTime() - d.getTime();
		d = new Date();
		for (AccelerateBreakDawdleThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.index = 0;
		this.addPhi(new Date().getTime() - d.getTime());
	}

	public synchronized void addPhi(Long p) {
		this.phi += p;
	}

	public synchronized Integer getNextRange() {
		Date d = new Date();

		index += (rangeSize + 1);
		Integer returnVal = (index - (rangeSize + 1));

		Long duration = new Date().getTime() - d.getTime();
		this.kappa += duration;
		this.phi -= duration;

		return returnVal;
	}

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
