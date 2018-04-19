package service;

import junit.framework.TestCase;

/**
 * Created by Palash on 4/19/2018.
 */
public class GetAllAppointmentsTest extends TestCase {

    LAMSService service;

    @Override
    protected void setUp() throws Exception {
        service = new LAMSService();
        System.out.println(service.initialize());
    }

    public void testGetAllAppointments() {
        System.out.println(service.getAllAppointments());
    }
}