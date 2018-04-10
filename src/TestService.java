import service.*;

/**
 * Created by Palash on 4/10/2018.
 */
public class TestService {
    public static void main(String[] args){
        LAMSService service = new LAMSService();
        System.out.println(service.initialize());
        System.out.println(service.getAllAppointments());
        System.out.println(service.getAppointment("210"));
//        System.out.println(service.addAppointment());
    }
}
