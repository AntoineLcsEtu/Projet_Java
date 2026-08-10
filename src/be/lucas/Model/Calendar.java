package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Calendar {
    private int calendarId;
    private Category category;
    private List<Ride> rides;

    public Calendar(int calendarId, Category category) {
        this.calendarId = calendarId;
        this.category = category;
        this.rides = new ArrayList<>();
        if (category != null) {
            category.setCalendar(this);
        }
    }

    public void addRide(Ride ride) {
        this.rides.add(ride);
    }

    public int getCalendarId() { return calendarId; }
    public void setCalendarId(int calendarId) { this.calendarId = calendarId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            category.setCalendar(this);
        }
    }
    public List<Ride> getRides() { return rides; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendar calendar = (Calendar) o;
        return calendarId == calendar.calendarId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarId);
    }
}