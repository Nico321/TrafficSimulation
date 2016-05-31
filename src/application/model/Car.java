package application.model;

import java.io.Serializable;

public class Car implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int speed, maxSpeed, targetTrack, moved=0, id;
	private boolean changeTrack = false;

	public Car(int speed, int maxSpeed){
		this.speed=speed;
		this.setMaxSpeed(maxSpeed);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isChangeTrack() {
		return changeTrack;
	}

	public void setChangeTrack(boolean changeTrack) {
		this.changeTrack = changeTrack;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getTargetTrack() {
		return targetTrack;
	}

	public void setTargetTrack(int targetTrack) {
		this.targetTrack = targetTrack;
	}

	public int getMoved() {
		return moved;
	}

	public void setMoved(int moved) {
		this.moved = moved;
	}

	public void incrementSpeed() {
		this.speed++;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
