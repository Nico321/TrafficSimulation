package application.threads;

import application.model.Car;

import java.util.SplittableRandom;

/**
 * Created by Nico on 09.06.2016.
 */
public class ChangeTrackThread extends Thread{

    private Master master;
    private Car[][] street;
    private Integer size, MAX_SPEED;
    private Double c;

    private SplittableRandom random = new SplittableRandom();


    public ChangeTrackThread(Master master, Car[][] street, Integer size,
                                                  Integer MAX_SPEED, Double c) {
        this.master = master;
        this.street = street;
        this.size = size;
        this.MAX_SPEED = MAX_SPEED;
        this.c = c;
    }

    @Override
    public void run(){
        Integer index = master.getNextRange();
        while (index < street[0].length) {
            for (int t = 0; t < street.length; t++) {
                for (int i = index; i <= index + size; i++) {

                    if (i >= street[0].length)
                        break;

                    changeTrackCar(t, i);
                }
            }
            index = master.getNextRange();
        }
    }

    private void changeTrackCar(int t, int i) {
        if (street[t][i] != null && street[t][i].getSpeed() > 0) {
            if (street[t][i] != null) {
                if (t > 0 && checkTrackChange(i, t, t - 1)) {
                    changeTrack(t, i, t - 1);
                } else if (t < (street.length - 1) && checkTrackChange(i, t, t + 1)) {
                    changeTrack(t, i, t + 1);
                }
            }
        }
    }

    private void changeTrack(int t, int i, int targetTrack) {
        if (street[targetTrack][i] != null)
            System.out.println(
                    "crash while changing tracks:" + street[t][i] + "-->" + targetTrack + "-" + street[targetTrack][i]);
        street[targetTrack][i] = street[t][i];
        street[t][i] = null;
    }

    private boolean checkTrackChange(int pos, int currentTrack, int targetTrack) {
        // check neighbour car
        if (street[targetTrack][pos] != null)
            return false;

        boolean blockOnOwnStreet = false;

        // Check track change possibility
        if (random.nextDouble() < c) {


            // check previous cars
            for (int i = 1; i <= street[currentTrack][pos].getSpeed() + 1; i++) {
                int check = pos + i;
                if (check >= street[0].length)
                    check -= street[0].length;
                if (street[targetTrack][check] != null) {
                    return false;
                }
                if (street[currentTrack][check] != null) {
                    blockOnOwnStreet = true;
                }
            }

            if (!blockOnOwnStreet)
                return false;

            // check behind cars
            for (int i = 1; i <= MAX_SPEED; i++) {
                int check = pos - i;
                if (check < 0)
                    check += street[0].length;
                if (street[targetTrack][check] != null) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
