public class Event extends Task {
    private String date;

    Event(String description, String date) {
        super(description);
        this.date = date;
    }

    @Override
    public String toString() {
        return "[E] " + super.toString() + " (at: " + date  + ")";
    }
}