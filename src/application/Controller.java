package application;

import java.util.Date;
import java.util.Random;

import application.model.Car;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

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

	@FXML
	private TextField tfMaxSpeed, tfCarSize;

	@FXML
	private Canvas canvasAnalyticsOne, canvasAnalyticsTwo;

	@FXML
	private CheckBox showLive, showAnalyticsOne, showAnalyticsTwo;

	@FXML
	private TextField tfFramerate;
	private Integer framerate = 1000;

	private boolean pause = false, stop = true;

	private Car street[][];
	private Double p, p0, dichte;
	private Long dichteAbs;
	private Integer size;
	private int CAR_SIZE = 10;
	private int MAX_SPEED = 5;
	private Canvas canvas;
	private GraphicsContext gc, gcAnalyticsOne, gcAnalyticsTwo;

	@FXML
	private void pause() {
		pause = true;
		btnPlay.setDisable(false);
		btnStop.setDisable(false);
		btnPause.setDisable(true);
		piSimulating.setVisible(false);

		tfp.setDisable(true);
		tfp0.setDisable(true);
		tfC.setDisable(true);
		rbDichteAbs.setDisable(true);
		rbDichteProz.setDisable(true);
		tfdichte.setDisable(true);
		tfDichteAbs.setDisable(true);
		tfsize.setDisable(true);
		tfdisplayStart.setDisable(false);
		tfdisplayStop.setDisable(false);
		tfMaxSpeed.setDisable(true);
		tfCarSize.setDisable(false);
		tfFramerate.setDisable(false);
	}

	@FXML
	private void stop() {
		stop = true;
		btnPlay.setDisable(false);
		btnPause.setDisable(true);
		btnStop.setDisable(true);
		piSimulating.setVisible(false);

		tfp.setDisable(false);
		tfp0.setDisable(false);
		tfC.setDisable(false);

		rbDichteAbs.setDisable(false);
		rbDichteProz.setDisable(false);

		if (rbDichteAbs.isSelected())
			tfDichteAbs.setDisable(false);
		else
			tfdichte.setDisable(false);
		
		tfsize.setDisable(false);
		tfdisplayStart.setDisable(false);
		tfdisplayStop.setDisable(false);
		tfMaxSpeed.setDisable(false);
		tfCarSize.setDisable(false);
		tfFramerate.setDisable(false);
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

		tfp.setDisable(true);
		tfp0.setDisable(true);
		tfC.setDisable(true);
		rbDichteAbs.setDisable(true);
		rbDichteProz.setDisable(true);
		tfdichte.setDisable(true);
		tfDichteAbs.setDisable(true);
		tfsize.setDisable(true);
		tfdisplayStart.setDisable(true);
		tfdisplayStop.setDisable(true);
		tfMaxSpeed.setDisable(true);
		tfCarSize.setDisable(true);
		tfFramerate.setDisable(true);

		pause = false;
		streetpane.getChildren().removeAll(streetpane.getChildren());
		canvas = null;
		displayStart = Integer.parseInt(tfdisplayStart.getText());
		displayStop = Integer.parseInt(tfdisplayStop.getText());
		if (!tfFramerate.getText().equals(""))
			framerate = Integer.parseInt(tfFramerate.getText());
		CAR_SIZE = Integer.parseInt(tfCarSize.getText());

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
			if (!tfdichte.getText().equals(""))
				dichte = Double.parseDouble(tfdichte.getText());
			if (!tfDichteAbs.getText().equals(""))
				dichteAbs = Long.parseLong(tfDichteAbs.getText());

			MAX_SPEED = Integer.parseInt(tfMaxSpeed.getText());
			size = Integer.parseInt(tfsize.getText());
			initSimulation();
			stop = false;
			if (gcAnalyticsOne != null)
				gcAnalyticsOne.clearRect(0, 0, canvasAnalyticsOne.getWidth(), canvasAnalyticsOne.getHeight());
			if (gcAnalyticsTwo != null)
				gcAnalyticsTwo.clearRect(0, 0, canvasAnalyticsTwo.getWidth(), canvasAnalyticsTwo.getHeight());
		}

		new Thread() {
			public void run() {
				while (!pause && !stop) {
					numSteps++;
					changeTracks();
					accelerateCars();
					breakCars();
					dawdleCars();
					moveCars();

					if (numSteps % framerate == 0) {
						Platform.runLater(new Runnable() {
							public void run() {
								if (showLive.isSelected())
									draw();
								if (showAnalyticsOne.isSelected())
									drawAnalyticsOne(numSteps);
								if (showAnalyticsTwo.isSelected())
									drawAnalyticsTwo(numSteps);
							}
						});
					}
				}
			}
		}.start();

	}

	private void initSimulation() {
		street = new Car[2][size];

		if (rbDichteProz.isSelected())
			initDichteProz();
		else
			initDichteAbs();
	}

	private void initDichteAbs() {
		Random random = new Random();
		if (dichteAbs == null)
			dichteAbs = 0L;
		while (dichteAbs > 0) {
			int t = random.nextInt((1 - 0) + 1);
			int pos = random.nextInt(size);
			if (street[t][pos] == null) {
				street[t][pos] = new Car(random.nextInt((MAX_SPEED - 1) + 1) + 1);
				dichteAbs--;
			}
		}
	}

	private void initDichteProz() {
		Random random = new Random();
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				if (random.nextFloat() < dichte) {
					street[t][i] = new Car(random.nextInt((MAX_SPEED - 1) + 1) + 1);
				}
			}
		}
	}

	private void changeTracks() {
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				if (street[t][i] != null && street[t][i].getSpeed() > 0) {
					for (int j = 1; j <= street[t][i].getSpeed() + 1; j++) {
						int check = i + j;
						if (i + j >= size)
							check -= size;
						if (street[t][check] != null) {
							if (checkTrackChange(i, t, 1 - t)) {
								if (street[1 - t][i] != null)
									System.out.println("crash");
								// street[1 - t][i] = street[t][i];
								// street[t][i] = null;
								street[t][i].setChangeTrack(true);
							}

							break;
						}
					}
				}
			}
		}
	}

	private void accelerateCars() {
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				Car c = street[t][i];
				if (c != null) {
					if (c.getSpeed() < MAX_SPEED) {
						c.setSpeed(c.getSpeed() + 1);
					}
					if (c.isChangeTrack()) {
						if (street[1 - t][i] != null)
							System.out.println("crash while changing tracks");
						street[t][i].setChangeTrack(false);
						street[1 - t][i] = street[t][i];
						street[t][i] = null;
					}

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
							street[t][i].setSpeed(j - 1);
							break;
						}
					}
				}
			}
		}
	}

	private boolean checkTrackChange(int pos, int currentTrack, int targetTrack) {

		// check behind cars
		for (int i = 1; i <= 5; i++) {
			int check = pos - i;
			if (check < 0)
				check += size;
			if (street[targetTrack][check] != null) {
				return false;
			}
		}

		// check neighbour car
		if (street[targetTrack][pos] != null)
			return false;

		// check previous cars
		for (int i = 1; i <= street[currentTrack][pos].getSpeed() + 1; i++) {
			int check = pos + i;
			if (check >= size)
				check -= size;
			if (street[targetTrack][check] != null) {
				return false;
			}
		}

		// Check track change possibility
		Random random = new Random();
		if (random.nextFloat() < c) {
			return true;
		} else {
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
						System.out.println("crash while moving");
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

	private void drawAnalyticsOne(Long numSteps) {
		if (gcAnalyticsOne == null) {
			gcAnalyticsOne = canvasAnalyticsOne.getGraphicsContext2D();
		}

		gcAnalyticsOne.setTextAlign(TextAlignment.CENTER);
		gcAnalyticsOne.setTextBaseline(VPos.CENTER);

		long trackOffset = (long) ((numSteps * 2)%canvasAnalyticsOne.getHeight());
		for (int t = street.length - 1; t >= 0; t--) {
			int offset = 0;
			for (int i = displayStart; i <= displayStop; i++) {

				Car c = street[t][i];
				if (c == null) {
					gcAnalyticsOne.setFill(Color.BLACK);
				} else {
					if (c.getSpeed() == 0) {
						gcAnalyticsOne.setFill(Color.WHITE);
					} else {
						gcAnalyticsOne.setFill(Color.hsb(
								(Color.GREEN.getHue()
										+ (Color.RED.getHue() - Color.GREEN.getHue()) * c.getSpeed() / MAX_SPEED),
								1.0, 1.0));
					}
				}

				gcAnalyticsOne.fillPolygon(new double[] { offset, offset + 1, offset + 1, offset },
						new double[] { 0 + trackOffset, 0 + trackOffset, 1 + trackOffset, 1 + trackOffset }, 4);
				offset += 1;
			}
			trackOffset += 1;
		}
	}

	private void drawAnalyticsTwo(Long numSteps) {
		if (gcAnalyticsTwo == null) {
			gcAnalyticsTwo = canvasAnalyticsTwo.getGraphicsContext2D();
		}

		gcAnalyticsTwo.setTextAlign(TextAlignment.CENTER);
		gcAnalyticsTwo.setTextBaseline(VPos.CENTER);

		long trackOffset = (long) ((numSteps * 2)%canvasAnalyticsTwo.getHeight());
		for (int t = street.length - 1; t >= 0; t--) {
			int offset = 0;
			for (int i = displayStart; i <= displayStop; i++) {

				Car c = street[t][i];
				if (c == null) {
					// gcAnalyticsTwo.setFill(Color.BLACK);
				} else {
					if (c.getSpeed() == 0) {
						gcAnalyticsTwo.setFill(Color.WHITE);
					} else {
						gcAnalyticsTwo.setFill(Color.hsb(
								(Color.GREEN.getHue()
										+ (Color.RED.getHue() - Color.GREEN.getHue()) * c.getSpeed() / MAX_SPEED),
								1.0, 1.0));
					}
				}

				gcAnalyticsTwo.fillPolygon(new double[] { offset, offset + 1, offset + 1, offset },
						new double[] { 0 + trackOffset, 0 + trackOffset, 1 + trackOffset, 1 + trackOffset }, 4);
				offset += 1;
			}
			trackOffset += 1;
		}
	}

	private void draw() {
		if (canvas == null) {
			canvas = new Canvas(streetpane.getWidth(), streetpane.getHeight());
			gc = canvas.getGraphicsContext2D();
			streetpane.getChildren().add(canvas);
		}
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.clearRect(0, 0, streetpane.getWidth(), streetpane.getHeight());

		for (int i = 0; i <= MAX_SPEED + 1; i++) {
			if (i == 0)
				gc.setFill(Color.WHITE);
			else if (i > MAX_SPEED)
				gc.setFill(Color.BLACK);
			else
				gc.setFill(
						Color.hsb((Color.GREEN.getHue() + (Color.RED.getHue() - Color.GREEN.getHue()) * i / MAX_SPEED),
								1.0, 1.0));
			gc.fillPolygon(new double[] { (i * 25) + 10, (i * 25) + 20, (i * 25) + 20, (i * 25) + 10 },
					new double[] { 0, 0, 10, 10 }, 4);
			gc.setFill(Color.BLACK);

			if (i > MAX_SPEED)
				gc.fillText("no car", (i * 25) + 40, 5);
			else
				gc.fillText(i + "", (i * 25) + 26, 5);
		}

		int trackOffset = 20;
		for (int t = street.length - 1; t >= 0; t--) {
			int offset = 0;
			for (int i = displayStart; i <= displayStop; i++) {

				Car c = street[t][i];
				if (c == null) {
					gc.setFill(Color.BLACK);
				} else {
					if (c.getSpeed() == 0) {
						gc.setFill(Color.WHITE);
					} else {
						gc.setFill(Color.hsb(
								(Color.GREEN.getHue()
										+ (Color.RED.getHue() - Color.GREEN.getHue()) * c.getSpeed() / MAX_SPEED),
								1.0, 1.0));
					}
				}

				gc.fillPolygon(new double[] { offset, offset + CAR_SIZE, offset + CAR_SIZE, offset }, new double[] {
						0 + trackOffset, 0 + trackOffset, CAR_SIZE + trackOffset, CAR_SIZE + trackOffset }, 4);
				offset += CAR_SIZE;
			}
			trackOffset += CAR_SIZE;
		}

		gc.setFill(Color.BLUE);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		lblNumSteps.setText("#Iter.: " + numSteps);
		lblAvgTime.setText("Avg. Time (ms): " + ((new Date().getTime() - startTime.getTime()) / numSteps));

	}

}
