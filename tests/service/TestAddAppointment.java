package service;

import junit.framework.TestCase;

/**
 * Created by Palash on 4/10/2018.
 */
public class TestAddAppointment extends TestCase {

    LAMSService service;

    @Override
    protected void setUp() throws Exception {
        service = new LAMSService();
        System.out.println(service.initialize());
    }

    public void testAddCorrectOnce() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testDuplicate() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testBadTime() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>99:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testTooCloseAppointmentInDiffPSC() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>510</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:30</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testTooCloseAppointmentInSamePSC() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>510</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>9:55</time><patientId>220</patientId><physicianId>20</physicianId><pscId>510</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testSufficientlyFarAppointment() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>510</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:45</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

    public void testMultipleCorrectAppointments() {
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:00</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
        System.out.println(service.addAppointment("<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><date>2018-12-28</date><time>10:15</time><patientId>220</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9' /><test id='86609' dxcode='307.3' /></labTests></appointment>"));
    }

}
