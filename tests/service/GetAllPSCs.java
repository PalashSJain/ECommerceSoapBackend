package service;

import junit.framework.TestCase;

/**
 * Created by Palash on 4/29/2018.
 */
public class GetAllPSCs extends TestCase {

    LAMSService service;

    @Override
    protected void setUp() throws Exception {
        service = new LAMSService();
        System.out.println(service.initialize());
    }

    public void testGetAllPSCs() {
        System.out.println(service.getAllPSCs());
    }

}
