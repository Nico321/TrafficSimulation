package application;

import java.util.Random;

import application.model.Car;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller {

	@FXML
	private TextField tfp;
	@FXML
	private TextField tfp0;
	@FXML
	private TextField tfdichte;
	@FXML
	private TextField tfsize;
	@FXML
	private Pane streetpane;

	private Car street[];
	private Double p, p0, dichte;
	private Integer size;
	private final int CAR_SIZE = 10;
	private final int MAX_SPEED = 5;

	private boolean init = false;

	@FXML
	private void simulate() {
		if (!init) {
			p = Double.parseDouble(tfp.getText());
			p0 = Double.parseDouble(tfp0.getText());
			dichte = Double.parseDouble(tfdichte.getText());
			size = Integer.parseInt(tfsize.getText());
			initSimulation();
			init = true;
		}
		accelerateCars();
		breakCars();
		dawdleCars();
		moveCars();
		draw();
	}

	private void moveCars() {
		boolean moving = true;
		int i = 0;
		while (moving) {
			Car c = street[i];
			if (c != null && c.getSpeed()>0) {
				int pos = i + c.getSpeed();
				if (pos >= street.length) {
					pos -= street.length;
					moving = false;
				}
				if(street[pos]!=null){
					System.out.println("crash:"+i+"-"+c.getSpeed());
				}
				street[pos] = c;
				street[i] = null;
				i = pos;
			}
			i++;
			if (i >= street.length) {
				moving = false;
			}
		}
	}

	private void dawdleCars() {
		Random random = new Random();
		for (Car c : street) {
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

	private void breakCars() {
		for (int i = 0; i < street.length; i++) {
			if (street[i] != null && street[i].getSpeed() > 0) {
				for (int j = 1; j <= street[i].getSpeed(); j++) {
					int check = i + j;
					if (i + j >= street.length)
						check -= street.length;
					if (street[check] != null) {
						street[i].setSpeed(j - 1);
					}
				}
			}
		}
	}

	private void accelerateCars() {
		for (Car c : street) {
			if (c != null && c.getSpeed() < MAX_SPEED) {
				c.setSpeed(c.getSpeed() + 1);
			}
		}
	}

	private void initSimulation() {
		Random random = new Random();
		street = new Car[size];
		for (int i = 0; i < size; i++) {
			if (random.nextFloat() < dichte) {
				street[i] = new Car(random.nextInt((5 - 1) + 1) + 1);
			}
		}
	}

	private void draw() {
		Canvas canvas = new Canvas(streetpane.getWidth(), streetpane.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		streetpane.getChildren().add(canvas);
		int offset = 0;
		for (Car c : street) {
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
			gc.fillPolygon(new double[] { offset, offset + CAR_SIZE, offset + CAR_SIZE, offset },
					new double[] { 0, 0, CAR_SIZE, CAR_SIZE }, 4);
			offset += CAR_SIZE;
		}

		gc.setFill(Color.BLUE);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

	}

}
