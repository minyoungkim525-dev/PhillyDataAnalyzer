package common;

public class ParkingViolation {

    private String ticket_number;
    private String plate_id;
    private String date;
    private Integer zip_code;
    private String violation;
    private int fine;
    private String state;

    public ParkingViolation(String ticket_number,
                            String plate_id, String date, Integer zip_code,
                            String violation, int fine, String state){

        this.ticket_number = ticket_number;
        this.plate_id = plate_id;
        this.date = date;
        this.zip_code = zip_code;
        this.violation = violation;
        this.fine = fine;
        this.state = state;
    }

    public String getTicket_number(){ return ticket_number; }
    public String getPlate_id(){ return plate_id; }
    public String getDate(){ return date; }
    public Integer getZip_code(){ return zip_code; }
    public String getViolation(){ return violation; }
    public int getFine() { return fine;}
    public String getState() { return state; }

    @Override
    public String toString() {
        return "ParkingViolation{" +
                "ticket_number=" + ticket_number +
                ", plate_id=" + plate_id +
                ", date='" + date + '\'' +
                ", zip_code=" + zip_code +
                ", violation='" + violation + '\'' +
                ", fine=" + fine +
                ", state='" + state + '\'' +
                '}';
    }

}
