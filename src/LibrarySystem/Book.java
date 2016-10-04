package LibrarySystem;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by hudo on 10/2/16.
 */
public class Book implements Serializable {
    private String name;
    private String owner;
    private Date returnDate;
    private Date reservationExpiryDate;
    private List<ClientCBHandler> reservationList = new ArrayList<>();
    private Timer reservationTimer;

    public Book(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public List<ClientCBHandler> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<ClientCBHandler> reservationList) {
        this.reservationList = reservationList;
    }

    public Date getReservationExpiryDate() {
        return reservationExpiryDate;
    }

    public void setReservationExpiryDate(Date reservationExpiryDate) {
        this.reservationExpiryDate = reservationExpiryDate;
    }

    public Timer getReservationTimer() {
        if(reservationTimer == null)
        {
            reservationTimer = new Timer();
        }
        return reservationTimer;
    }

    public void scheduleReservation() {
        Timer timer = getReservationTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!reservationList.isEmpty()) {
                    reservationList.remove(0);
                    try {
                        reservationList.get(0).callback(Book.this);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, reservationExpiryDate);
    }

    public void setReservationTimer(Timer reservationTimer) {
        this.reservationTimer = reservationTimer;
    }

    @Override
    public String toString() {
        return name;
    }

    public void removeReservation(Client c)
    {
        reservationList.remove(c);
    }
}

