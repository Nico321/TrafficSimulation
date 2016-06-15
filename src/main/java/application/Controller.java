package application;

import application.model.Car;
import application.threads.Master;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.util.Date;
import java.util.Random;

public class Controller {

	@FXML
	private TextField tfp;
	@FXML
	private TextField tfp0;
	@FXML
	private TextField tfdichte, tfDichteAbs, tfNumPerThread, tfNumThreads, tfTracks, tfPotNumOfThreads, tfAnzIter;
	private Integer numPerThread, numThreads, potNumThreads, anzIteration;
	@FXML
	private TextField tfsize;
	@FXML
	private Pane streetpane;

	@FXML
	private Button btnPlay, btnPause, btnStop, btnSaveArray, btnLoadArray, btnDiscardArray;

	@FXML
	private ProgressIndicator piSimulating;

	@FXML
	private TextField tfC;
	private Double c;

	@FXML
	private Label lblNumSteps, lblAvgTime, lblSpeedUp, lblKarp, lblEffizienz, lblSigma, lblPhi, lblKappa, lblSumTime, lblTs, lblTp, lblSerialFraction, lblPotPar, lblTpAmdahl, lblSAmdahl, lblFStrich, lblScaledSpeedUp;

	@FXML
	private RadioButton rbDichteProz, rbDichteAbs;

	@FXML
	private AnchorPane anchorPaneOne, anchorPaneTwo;

	@FXML
	private TextField tfdisplayStart, tfdisplayStop;
	private Integer displayStart = 0, displayStop = 0;

	@FXML
	private TextField tfMaxSpeed, tfCarSize;

	@FXML
	private Canvas canvasAnalyticsOne, canvasAnalyticsTwo;

	@FXML
	private CheckBox showLive, showAnalyticsOne, showAnalyticsTwo, chkMaxSpeedPerCar, chkEndless;

	@FXML
	private TextField tfFramerate;
	private Integer framerate = 1000;

	private boolean stop = true, canSaveArray = false, didLoadArray = false;

	private Car street[][];
	private Double p, p0, dichte;
	private Long dichteAbs;
	private Integer size;
	private int CAR_SIZE = 10;
	private int MAX_SPEED = 5;
	private Canvas canvas;
	private GraphicsContext gc, gcAnalyticsOne, gcAnalyticsTwo;
	private Controller controller = this;
	private Master master;
	private int numTracks;
	private Car loadedStreet[][];

	@FXML
	private void pause() {
		master.setPause(true);
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
		tfPotNumOfThreads.setDisable(true);
		chkEndless.setDisable(true);
		tfAnzIter.setDisable(true);
	}

	public void stopSimulation() {
		this.stop();
	}

	@FXML
	private void stop() {
		stop = true;
		master.setStop(true);
		btnPlay.setDisable(false);
		btnPause.setDisable(true);
		btnStop.setDisable(true);
		piSimulating.setVisible(false);

		tfTracks.setDisable(false);
		chkMaxSpeedPerCar.setDisable(false);

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
		tfNumPerThread.setDisable(false);
		tfNumThreads.setDisable(false);
		tfPotNumOfThreads.setDisable(false);
		chkEndless.setDisable(false);
		if (!chkEndless.isSelected())
			tfAnzIter.setDisable(false);
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
		Date start = new Date();

		disableAll();

		if (master != null)
			master.setPause(false);
		streetpane.getChildren().removeAll(streetpane.getChildren());
		canvas = null;
		displayStart = Integer.parseInt(tfdisplayStart.getText());
		displayStop = Integer.parseInt(tfdisplayStop.getText());
		canvasAnalyticsOne.setWidth(this.displayStop - this.displayStart);
		anchorPaneOne.setMinWidth(this.displayStop - this.displayStart);
		anchorPaneOne.setMaxWidth(this.displayStop - this.displayStart);
		anchorPaneOne.setPrefWidth(this.displayStop - this.displayStart);
		canvasAnalyticsOne.setHeight(5000);
		canvasAnalyticsTwo.setWidth(this.displayStop - this.displayStart);
		anchorPaneTwo.setMinWidth(this.displayStop - this.displayStart);
		anchorPaneTwo.setMaxWidth(this.displayStop - this.displayStart);
		anchorPaneTwo.setPrefWidth(this.displayStop - this.displayStart);
		canvasAnalyticsTwo.setHeight(5000);

		if (!tfFramerate.getText().equals("")) {
			framerate = Integer.parseInt(tfFramerate.getText());
			if (master != null)
				master.setFramerate(framerate);
		}
		CAR_SIZE = Integer.parseInt(tfCarSize.getText());

		btnPlay.setDisable(true);
		btnPause.setDisable(false);
		btnStop.setDisable(false);
		piSimulating.setVisible(true);

		if (stop) {
			p = Double.parseDouble(tfp.getText());
			p0 = Double.parseDouble(tfp0.getText());
			c = Double.parseDouble(tfC.getText());
			if (!tfdichte.getText().equals(""))
				dichte = Double.parseDouble(tfdichte.getText());
			if (!tfDichteAbs.getText().equals(""))
				dichteAbs = Long.parseLong(tfDichteAbs.getText());
			numThreads = Integer.parseInt(tfNumThreads.getText());
			potNumThreads = Integer.parseInt(tfPotNumOfThreads.getText());
			numPerThread = Integer.parseInt(tfNumPerThread.getText());
			numTracks = Integer.parseInt(tfTracks.getText());
			if (!chkEndless.isSelected())
				anzIteration = Integer.parseInt(tfAnzIter.getText());
			else
				anzIteration = -1;

			MAX_SPEED = Integer.parseInt(tfMaxSpeed.getText());
			size = Integer.parseInt(tfsize.getText());
			initSimulation();
			stop = false;
			master = null;
			if (gcAnalyticsOne != null)
				gcAnalyticsOne.clearRect(0, 0, canvasAnalyticsOne.getWidth(), canvasAnalyticsOne.getHeight());
			if (gcAnalyticsTwo != null)
				gcAnalyticsTwo.clearRect(0, 0, canvasAnalyticsTwo.getWidth(), canvasAnalyticsTwo.getHeight());
		}

		new Thread() {
			public void run() {
				if (master == null)

					master = new Master(street[0].length, numPerThread, numThreads, c, street, MAX_SPEED, p, p0,
							controller, framerate, new Date().getTime() - start.getTime(), anzIteration);

				master.startSimulation();
			}
		}.start();

	}

	private void disableAll() {
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
		tfNumPerThread.setDisable(true);
		tfNumThreads.setDisable(true);
		tfTracks.setDisable(true);
		chkMaxSpeedPerCar.setDisable(true);
		tfPotNumOfThreads.setDisable(true);
		tfAnzIter.setDisable(true);
		chkEndless.setDisable(true);
	}

	public void updateGraphics() {
		Platform.runLater(new Runnable() {
			public void run() {
				if (showLive.isSelected())
					draw();
				if (showAnalyticsOne.isSelected())
					drawAnalyticsOne(master.getNumSteps());
				if (showAnalyticsTwo.isSelected())
					drawAnalyticsTwo(master.getNumSteps());
			}
		});
	}

	@FXML
	private void discardArray() {
		didLoadArray = false;
		canSaveArray = false;
		btnDiscardArray.setDisable(true);
		btnLoadArray.setDisable(false);
	}

	@FXML
	private void loadStreet() {
		piSimulating.setVisible(true);
		btnPlay.setDisable(true);

		new Thread() {
			public void run() {
				try {
					FileInputStream fis = new FileInputStream("street");
					ObjectInputStream in = new ObjectInputStream(fis);
					street = (Car[][]) in.readObject();
					in.close();
					canSaveArray = true;
					didLoadArray = true;
					btnLoadArray.setDisable(true);
					btnSaveArray.setDisable(false);
					btnDiscardArray.setDisable(false);


					loadedStreet = new Car[street.length][street[0].length];
					for (int t = 0; t < street.length; t++) {
						for (int i = 0; i < street[0].length; i++) {
							if (street[t][i] != null) {
								loadedStreet[t][i] = new Car(street[t][i].getSpeed(), street[t][i].getMaxSpeed());
							}
						}
					}

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							piSimulating.setVisible(false);
							btnPlay.setDisable(false);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	@FXML
	private void initArray() {
		piSimulating.setVisible(true);
		btnPlay.setDisable(true);

		new Thread() {
			public void run() {
				initSimulation();

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						piSimulating.setVisible(false);
						btnPlay.setDisable(false);
					}
				});
			}
		}.start();
	}

	@FXML
	private void saveStreet() {
		if (street != null && canSaveArray) {

			piSimulating.setVisible(true);
			btnPlay.setDisable(true);

			new Thread() {
				public void run() {
					try {
						FileOutputStream fos = new FileOutputStream("street");
						ObjectOutputStream out = new ObjectOutputStream(fos);
						out.writeObject(street);
						out.flush();
						out.close();

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								piSimulating.setVisible(false);
								btnLoadArray.setDisable(false);
								btnPlay.setDisable(false);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

		}
	}

	private void initSimulation() {
		if (!didLoadArray) {
			p = Double.parseDouble(tfp.getText());
			p0 = Double.parseDouble(tfp0.getText());
			c = Double.parseDouble(tfC.getText());
			if (!tfdichte.getText().equals(""))
				dichte = Double.parseDouble(tfdichte.getText());
			if (!tfDichteAbs.getText().equals(""))
				dichteAbs = Long.parseLong(tfDichteAbs.getText());
			numThreads = Integer.parseInt(tfNumThreads.getText());
			numPerThread = Integer.parseInt(tfNumPerThread.getText());
			numTracks = Integer.parseInt(tfTracks.getText());

			MAX_SPEED = Integer.parseInt(tfMaxSpeed.getText());
			size = Integer.parseInt(tfsize.getText());

			street = new Car[numTracks][size];

			if (rbDichteProz.isSelected())
				initDichteProz();
			else
				initDichteAbs();
		} else {
			street = new Car[loadedStreet.length][loadedStreet[0].length];
			for (int t = 0; t < loadedStreet.length; t++) {
				for (int i = 0; i < loadedStreet[0].length; i++) {
					if (loadedStreet[t][i] != null) {
						street[t][i] = new Car(loadedStreet[t][i].getSpeed(), loadedStreet[t][i].getMaxSpeed());
					}
				}
			}
		}

		canSaveArray = true;
		btnSaveArray.setDisable(false);
	}

	private void initDichteAbs() {
		Random random = new Random();
		if (dichteAbs == null)
			dichteAbs = 0L;
		while (dichteAbs > 0) {
			int t = random.nextInt((1 - 0) + 1);
			int pos = random.nextInt(size);
			if (street[t][pos] == null) {
				if(chkMaxSpeedPerCar.isSelected()){
					int carMaxSpeed = random.nextInt((MAX_SPEED - 1) + 1) + 1;
					street[t][pos] = new Car(random.nextInt((carMaxSpeed - 1) + 1) + 1, carMaxSpeed);
				}else{
					street[t][pos] = new Car(random.nextInt((MAX_SPEED - 1) + 1) + 1, MAX_SPEED);
				}

				dichteAbs--;
			}
		}
	}

	private void initDichteProz() {
		Random random = new Random();
		for (int t = 0; t < street.length; t++) {
			for (int i = 0; i < size; i++) {
				if (random.nextFloat() <= dichte) {
					if(chkMaxSpeedPerCar.isSelected()){
						int carMaxSpeed = random.nextInt((MAX_SPEED - 1) + 1) + 1;
						street[t][i] = new Car(random.nextInt((carMaxSpeed - 1) + 1) + 1, carMaxSpeed);
					}else{
						street[t][i] = new Car(random.nextInt((MAX_SPEED - 1) + 1) + 1, MAX_SPEED);
					}
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

		long trackOffset = (long) ((numSteps * street.length) % canvasAnalyticsOne.getHeight());
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

		long trackOffset = (long) ((numSteps * street.length) % canvasAnalyticsTwo.getHeight());
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

		lblNumSteps.setText("#Iter.: " + master.getNumSteps());
		lblAvgTime.setText(
				"Avg. Time (ms): " + ((new Date().getTime() - master.getStartTime().getTime()) / master.getNumSteps()));

	}

	public void setAnalytics(Long sigma, Long phi, Long kappa) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				Double speedUp = (sigma.doubleValue()+phi.doubleValue())/(sigma.doubleValue()+(phi.doubleValue()/potNumThreads.doubleValue())+kappa.doubleValue());
				Double effizenz = (sigma.doubleValue()+phi.doubleValue())/(potNumThreads.doubleValue()*sigma.doubleValue()+phi.doubleValue()+potNumThreads.doubleValue()*kappa.doubleValue());
				Double karp = (sigma.doubleValue()+kappa.doubleValue())/(sigma.doubleValue()+phi.doubleValue());
				Double serialFraction = (sigma.doubleValue())/(sigma.doubleValue()+phi.doubleValue()); //Serieller Anzeil

				Double f = phi.doubleValue()/(sigma.doubleValue()+phi.doubleValue()); //Potenziell Parallel ausführbare Zeit
				Double ts = sigma.doubleValue()+phi.doubleValue(); //Sequentielle Laufzeit
				Double tp = sigma.doubleValue()+(phi.doubleValue()/potNumThreads.doubleValue())+kappa.doubleValue(); //Parallele Ausführungszeit
				Double tpAmdahl = ts * ((1-f)+(f/potNumThreads.doubleValue())); //Parallele Ausführungszeit nach Amdahlschen Gesetz

				Double sAmdahl = ts/tpAmdahl;
				Double fStrich = phi.doubleValue()/(potNumThreads.doubleValue()*sigma.doubleValue()+phi.doubleValue());
				Double scaledSpeedUp = (1.0-fStrich)+potNumThreads.doubleValue()*fStrich;

				lblSigma.setText("Sigma= " + sigma + " ms");
				lblPhi.setText("Phi= " + phi + " ms");
				lblKappa.setText("Kappa= " + kappa + " ms");
				lblSumTime.setText("Gesamt= " + (sigma + phi + kappa + " ms"));

				lblSpeedUp.setText("SpeedUp= " + speedUp);
				lblEffizienz.setText("Effizenz= " + effizenz);
				lblTs.setText("Ts= " + ts);
				lblTp.setText("Tp= " + tp);
				lblSerialFraction.setText("Serial Fraction= " + serialFraction);
				lblPotPar.setText("Pot. par. Anteil= " + f);
				
				lblTpAmdahl.setText("Tp= " + tpAmdahl);
				lblSAmdahl.setText("S= " + sAmdahl);
				
				lblFStrich.setText("f'= " + fStrich);
				lblScaledSpeedUp.setText("S= " + scaledSpeedUp);
				
				lblKarp.setText("e= " + karp);
			}
		});
	}

	@FXML
	private void changeEndless() {
		if (chkEndless.isSelected()) {
			tfAnzIter.setDisable(true);
		} else {
			tfAnzIter.setDisable(false);
		}
	}

}
