package application.model;

public class Car {
	
	private int speed;
	private boolean changeTrack=false;
	
	public Car(int speed){
		this.speed=speed;
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

}
