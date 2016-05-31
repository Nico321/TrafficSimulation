import org.junit.Assert;
import org.junit.Test;

import application.model.Car;
import application.threads.Master;

public class TrafficTest {

	Master master;

	@Test
	public void integrationWithoutDawdle() {
		System.out.println("vvvvvvvvvvStart without Dawdlevvvvvvvvvv");
		Integer max_speed = 5;

		Car[][] street = new Car[2][10];
		for (int t = 0; t < 2; t++) {
			for (int i = 0; i < 10; i++) {
				street[t][i] = null;
			}
		}

		street[1][2] = new Car(1, max_speed);
		street[1][2].setId(1);
		street[0][2] = new Car(5, max_speed);
		street[0][2].setId(2);
		street[0][4] = new Car(0, max_speed);
		street[0][4].setId(3);
		street[0][8] = new Car(1, max_speed);
		street[0][8].setId(4);
		street[0][9] = new Car(0, max_speed);
		street[0][9].setId(5);
		
		printStreet(street);
		master = new Master(street[0].length, 5, 4, 1.0, street, max_speed, 0.0, 0.0, null, 100, 0L, 1);
		master.startSimulation();


		printStreet(street);
		
		Assert.assertTrue(street[0][0].getSpeed() == 1);
		Assert.assertTrue(street[1][0].getSpeed() == 2);
		Assert.assertTrue(street[0][3].getSpeed() == 1);
		Assert.assertTrue(street[1][4].getSpeed() == 2);
		Assert.assertTrue(street[0][5].getSpeed() == 1);
		System.out.println("^^^^^^^Finished without Dawdle^^^^^^^^^^");
	}
	
	
	@Test
	public void integrationWithDawdle() {
		System.out.println("vvvvvvvvvvStart with Dawdlevvvvvvvvvv");
		Integer max_speed = 5;

		Car[][] street = new Car[2][10];
		for (int t = 0; t < 2; t++) {
			for (int i = 0; i < 10; i++) {
				street[t][i] = null;
			}
		}

		street[1][2] = new Car(1, max_speed);
		street[1][2].setId(1);
		street[0][2] = new Car(5, max_speed);
		street[0][2].setId(2);
		street[0][4] = new Car(0, max_speed);
		street[0][4].setId(3);
		street[0][8] = new Car(1, max_speed);
		street[0][8].setId(4);
		street[0][9] = new Car(0, max_speed);
		street[0][9].setId(5);
		
		printStreet(street);
		master = new Master(street[0].length, 5, 4, 1.0, street, max_speed, 1.0, 1.0, null, 100, 0L, 1);
		master.startSimulation();


		printStreet(street);
		
		Assert.assertTrue(street[0][2].getSpeed() == 0);
		Assert.assertTrue(street[0][4].getSpeed() == 0);
		Assert.assertTrue(street[0][9].getSpeed() == 0);
		Assert.assertTrue(street[1][3].getSpeed() == 1);
		Assert.assertTrue(street[1][9].getSpeed() == 1);
		System.out.println("^^^^^^^Finished with Dawdle^^^^^^^^^^");
	}

	private void printStreet(Car[][] street) {
		for (int t = 1; t >=0; t--) {
			String track = "";
			for (int i = 0; i < 10; i++) {
				if (street[t][i] == null)
					track += "0/0|";
				else
					track += street[t][i].getSpeed() + "/"+street[t][i].getId()+"|";
			}
			System.out.println(track);
		}
	}

}
