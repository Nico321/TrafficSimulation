package application;

import java.util.Date;
import java.util.Random;

import application.model.Car;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller {

	@FXML
	private TextField tfp;
	@FXML
	private TextField tfp0;
	@FXML
	private TextField tfdichte, tfDichteAbs;
	@FXML
	private TextField tfsize;
	@FXML
	private Pane streetpane;

	@FXML
	private Button btnPlay, btnPause, btnStop;

	@FXML
	private ProgressIndicator piSimulating;

	@FXML
	private TextField tfC;
	private Double c;

	@FXML
	private Label lblNumSteps, lblAvgTime;
	private Long numSteps = 0L;
	private Date startTime;

	@FXML
	private RadioButton rbDichteProz, rbDichteAbs;
	@FXML
	private TextField tfdisplayStart, tfdisplayStop;
	private Integer displayStart = 0, displayStop = 0;

	private boolean pause = false, stop = true;

	private Car street[][];
	private Double p, p0, dichte, dichteAbs;
	private Integer size;
	private final int CAR_SIZE = 10;
	private final int MAX_SPEED = 5;

	@FXML
	private void pause() {
		pause = true;
		btnPlay.setDisable(false);
		btnStop.setDisable(false);
		btnPause.setDisable(true);
		piSimulating.setVisible(false);
	}

	@FXML
	private void stop() {
		stop = true;
		btnPlay.setDisable(false);
		btnPause.setDisable(true);
		btnStop.setDisable(true);
		piSimulating.setVisible(false);
	}

	@FXML
	private void changedDichte() {
		if (rbDichteAbs.isSelected()) {
			tfdichte.setDisable(true);
			tfDichteAbs.setDisable(false);
		} else {
			tfdichte.setDisable(false);
			tfDichteAbs.setDisable(true);
		}
	}

	@FXML
	private void simulate() {
		pause = false;
		displayStart = Integer.parseInt(tfdisplayStart.getText());
		displayStop = Integer.parseInt(tfdisplayStop.getText());

		btnPlay.setDisable(true);
		btnPause.setDisable(false);
		btnStop.setDisable(false);
		piSimulating.setVisible(true);

		if (stop) {
			numSteps = 0L;
			startTime = new Date();
			p = Double.parseDouble(tfp.getText());
			p0 = Double.parseDouble(tfp0.getText());
			c = Double.parseDouble(tfC.getText());
			if(!tfdichte.getText().equals(""))
				dichte = Double.parseDouble(tfdichte.getText());
			if(!tfDichteAbs.getText().equals(""))
				dichteAbs = Double.parseDouble(tfDichteAbs.getText());
			size = Integer.parseInt(tfsize.getText());
			initSimulation();
			stop = false;
		}

		new Thread() {
			public void run() {
				while (!pause && !stop) {
					numSteps++;
					accelerateCars();
					breakCars();
					dawdleCars();
					moveCars();

					if (numSteps % 1000 == 0) {
						Platform.runLater(new Runnable() {
							public void run() {
								draw();
							}
						});
					}
				}
			}
		}.start();

	}

	private void initSimulation() {
		street = new Car[2][size];
		
		if(rbDichteProz.isSelected())
			initDichteProz();
		else
			initDichteAbs();
	}

	private void initDichteAbs(){
		Random random = new Random();
		while(dichteAbs > 0){
			int t = random.nextInt((1 - 0) + 1);
			int pos = random.nextInt(size);
			if(street[t][pos] == null){
				street[t][pos] = new Car(random.nextInt((5 - 1) + 1) + 1);
				dichteAbs--;
			}
		}
	}
	
	private void initDichteProz() {
		Random random = new Random();
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				if (random.nextFloat() < dichte) {
					street[t][i] = new Car(random.nextInt((5 - 1) + 1) + 1);
				}
			}
		}
	}

	private void accelerateCars() {
		for (int t = 0; t < street.length; t++) {
			for (Car c : street[t]) {
				if (c != null && c.getSpeed() < MAX_SPEED) {
					c.setSpeed(c.getSpeed() + 1);
				}
			}
		}

	}

	private void breakCars() {
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				if (street[t][i] != null && street[t][i].getSpeed() > 0) {
					for (int j = 1; j <= street[t][i].getSpeed(); j++) {
						int check = i + j;
						if (i + j >= size)
							check -= size;
						if (street[t][check] != null) {
							if(!checkTrackChange(i,t, 1-t)){
								street[t][i].setSpeed(j - 1);
							}else{
								street[1-t][i] = street[t][i];
								street[t][i] = null;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	private boolean checkTrackChange(int pos, int currentTrack, int targetTrack){
		
		//check behind cars
		for(int i=1;i<=5;i++){
			int check = pos-i;
			if(check < 0)
				check += size;
			if(street[targetTrack][check] != null){
				return false;
			}
		}
		
		//check neighbour car
		if(street[targetTrack][pos]!=null)
			return false;
		
		//check previous cars
		for(int i=1;i<=street[currentTrack][pos].getSpeed()+1;i++){
			int check = pos+i;
			if(check >= size)
				check -=size;
			if(street[targetTrack][check] != null){
				return false;
			}
		}
		
		//Check track change possibility
		Random random = new Random();
		if (random.nextFloat() < c) {
			return true;
		}
		else{
			return false;
		}
	}

	private void dawdleCars() {
		Random random = new Random();
		for (int t = 0; t < street.length; t++) {
			for (Car c : street[t]) {
				if (c != null && c.getSpeed() > 0) {
					if (c.getSpeed() > 1) {
						if (random.nextFloat() < p) {
							c.setSpeed(c.getSpeed() - 1);
						}
					} else {
						if (random.nextFloat() < p0) {
							c.setSpeed(c.getSpeed() - 1);
						}
					}
				}
			}
		}
	}

	private void moveCars() {
		for (int t = 0; t < street.length; t++) {
			boolean moving = true;
			int i = 0;
			while (moving) {
				Car c = street[t][i];
				if (c != null && c.getSpeed() > 0) {
					int pos = i + c.getSpeed();
					if (pos >= size) {
						pos -= size;
						moving = false;
					}
					if (street[t][pos] != null) {
						System.out.println("crash:" + i + "-" + c.getSpeed());
					}
					street[t][pos] = c;
					street[t][i] = null;
					i = pos;
				}
				i++;
				if (i >= size) {
					moving = false;
				}
			}
		}
	}

	private void draw() {
		Canvas canvas = new Canvas(streetpane.getWidth(), streetpane.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		streetpane.getChildren().add(canvas);
		int trackOffset = 0;
		for (int t = street.length - 1; t >= 0; t--) {
			trackOffset += CAR_SIZE;
			int offset = 0;
			for (int i = displayStart; i <= displayStop; i++) {

				Car c = street[t][i];
				if (c == null) {
					gc.setFill(Color.BLACK);
				} else {
					switch (c.getSpeed()) {
					case 0:
						gc.setFill(Color.WHITE);
						break;
					case 1:
						gc.setFill(Color.GREEN);
						break;
					case 2:
						gc.setFill(Color.YELLOWGREEN);
						break;
					case 3:
						gc.setFill(Color.YELLOW);
						break;
					case 4:
						gc.setFill(Color.ORANGE);
						break;
					case 5:
						gc.setFill(Color.RED);
						break;

					default:
						System.out.println(c.getSpeed());
						gc.setFill(Color.PINK);
						break;
					}
				}

				gc.fillPolygon(new double[] { offset, offset + CAR_SIZE, offset + CAR_SIZE, offset }, new double[] {
						0 + trackOffset, 0 + trackOffset, CAR_SIZE + trackOffset, CAR_SIZE + trackOffset }, 4);
				offset += CAR_SIZE;
			}

		}

		gc.setFill(Color.BLUE);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		lblNumSteps.setText("#Iterationen: " + numSteps);
		lblAvgTime.setText("Avg. Time (ms): " + ((new Date().getTime() - startTime.getTime()) / numSteps));

	}

}
