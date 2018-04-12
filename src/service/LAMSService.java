package service;

import components.data.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Palash on 4/9/2018.
 */
public class LAMSService {
    DBSingleton dbSingleton;

    /**
     * Initializes the database
     *
     * @return
     */
    public String initialize() {
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        return "Database Initialized";
    }

    /**
     * Return a list of all appointments and related information.
     *
     * @return
     */
    public String getAllAppointments() {
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        System.out.println("All appointments");
        List<Object> appointments = dbSingleton.db.getData("Appointment", "");
        if (appointments.isEmpty()) return "Appointment doesn't exist";

        Document document = DocumentHelper.createDocument();
        Element appointmentList = document.addElement("AppointmentList");

        for (Object appointment : appointments) {
            createAppointmentXML(appointmentList, (Appointment) appointment);
        }

        return document.asXML();
    }

    /**
     * Return a specific appointment and related information
     *
     * @param appointNumber
     * @return
     */
    public String getAppointment(String appointNumber) {
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        List<Object> appointments = dbSingleton.db.getData("Appointment", "patientid='" + appointNumber + "'");
        if (appointments.isEmpty()) return "Appointment doesn't exist";

        Document document = DocumentHelper.createDocument();
        Element appointmentList = document.addElement("AppointmentList");

        for (Object appointment : appointments) {
            createAppointmentXML(appointmentList, (Appointment) appointment);
        }

        return document.asXML();
    }

    private void createAppointmentXML(Element appointmentList, Appointment appointmentObj) {
        Element appointment = appointmentList.addElement("appointment");
        appointment
                .addAttribute("date", changeDateFormat(appointmentObj.getApptdate(), "YYYY-MM-dd"))
                .addAttribute("id", appointmentObj.getId())
                .addAttribute("time", changeTimeFormat(appointmentObj.getAppttime()));

        createPatientXML(appointmentObj, appointment);
        createPhlebotomistXML(appointmentObj, appointment);
        createPscXML(appointmentObj, appointment);
        createAllLabTestsXML(appointmentObj, appointment);
    }

    private void createPatientXML(Appointment appointmentObj, Element appointment) {
        Patient patientObj;
        patientObj = appointmentObj.getPatientid();
        Element patient = appointment.addElement("patient");
        patient.addAttribute("id", patientObj.getId());
        patient
                .addElement("name", patientObj.getName())
                .addElement("address", patientObj.getAddress())
                .addElement("insurance", String.valueOf(patientObj.getInsurance()))
                .addElement("dob", changeDateFormat(patientObj.getDateofbirth(), "YYYY-MM-dd"));
    }

    private void createPhlebotomistXML(Appointment appointmentObj, Element appointment) {
        Phlebotomist phlebotomistObj;
        phlebotomistObj = appointmentObj.getPhlebid();
        Element phlebotomist = appointment.addElement("phlebotomist");
        phlebotomist.addAttribute("id", phlebotomistObj.getId());
        phlebotomist.addElement("name", phlebotomistObj.getName());
    }

    private void createPscXML(Appointment appointmentObj, Element appointment) {
        PSC pscObj;
        pscObj = appointmentObj.getPscid();
        Element psc = appointment.addElement("psc");
        psc.addAttribute("id", pscObj.getId());
        psc.addElement("name", pscObj.getName());
    }

    private void createAllLabTestsXML(Appointment appointmentObj, Element appointment) {
        List<AppointmentLabTest> allLabTestsList;
        Element allLabTests = appointment.addElement("allLabTests");
        allLabTestsList = appointmentObj.getAppointmentLabTestCollection();
        for (AppointmentLabTest labtest : allLabTestsList) {
            createAppointmentLabTestXML(allLabTests, labtest);
        }
    }

    private void createAppointmentLabTestXML(Element allLabTests, AppointmentLabTest labtest) {
        Element appointmentLabTest = allLabTests.addElement("appointmentLabTest");
        appointmentLabTest
                .addAttribute("appointmentId", labtest.getAppointment().getId())
                .addAttribute("dxcode", labtest.getDiagnosis().getCode())
                .addAttribute("labTestId", labtest.getLabTest().getId());
    }

    private String changeTimeFormat(Time time) {
        Calendar t = Calendar.getInstance();
        t.setTimeInMillis(time.getTime());
        return t.get(Calendar.HOUR_OF_DAY) + "" + t.get(Calendar.MINUTE) + "" + t.get(Calendar.SECOND);
    }

    private String changeDateFormat(java.sql.Date date, String to) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(to);
        return DATE_FORMAT.format(date);
    }

    private String changeDateFormat(java.util.Date date, String to) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(to);
        return DATE_FORMAT.format(date);
    }

    /**
     * Create a new appointment providing the required information in XML and receiving XML or error message
     *
     * @param xmlStyle
     * @return
     */
    public String addAppointment(String xmlStyle) {
        Appointment newAppt = new Appointment("800", java.sql.Date.valueOf("2009-09-01"), java.sql.Time.valueOf("10:15:00"));
        List<AppointmentLabTest> tests = new ArrayList<AppointmentLabTest>();
        AppointmentLabTest test = new AppointmentLabTest("800", "86900", "292.9");
        test.setDiagnosis((Diagnosis) dbSingleton.db.getData("Diagnosis", "code='292.9'").get(0));
        test.setLabTest((LabTest) dbSingleton.db.getData("LabTest", "id='86900'").get(0));
        tests.add(test);
        newAppt.setAppointmentLabTestCollection(tests);

//        patient = ((Appointment) obj).getPatientid();
//        phleb = ((Appointment) obj).getPhlebid();
//        psc = ((Appointment) obj).getPscid();
//
//        newAppt.setPatientid(patient);
//        newAppt.setPhlebid(phleb);
//        newAppt.setPscid(psc);

        boolean good = dbSingleton.db.addData(newAppt);
        List<Object> objs = dbSingleton.db.getData("Appointment", "");
        for (Object obj : objs) {
            System.out.println(obj);
            System.out.println("");
        }

        return "<result></result>";
    }
}
