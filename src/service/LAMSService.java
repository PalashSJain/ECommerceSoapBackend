package service;

import java.util.*;

import components.data.*;
import business.*;

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
        List<Object> objs = dbSingleton.db.getData("Appointment", "");
        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            sb.append(obj);
        }
        return sb.toString();
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
        List<Object> objs = dbSingleton.db.getData("Appointment", "patientid='" + appointNumber + "'");
        if (objs.isEmpty()) return "Appointment doesn't exist";

        Patient patient = null;
        Phlebotomist phleb = null;
        PSC psc = null;

        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            sb.append(obj);
            // TODO format for appending patient, phleb and psc
            patient = ((Appointment) obj).getPatientid();
            phleb = ((Appointment) obj).getPhlebid();
            psc = ((Appointment) obj).getPscid();
        }
        return sb.toString();
    }

    /**
     * Create a new appointment providing the required information in XML and receiving XML or error message
     *
     * @param xmlStyle
     * @return
     */
    public String addAppointment(String xmlStyle) {
        Appointment newAppt = new Appointment("800",java.sql.Date.valueOf("2009-09-01"),java.sql.Time.valueOf("10:15:00"));
        List<AppointmentLabTest> tests = new ArrayList<AppointmentLabTest>();
        AppointmentLabTest test = new AppointmentLabTest("800","86900","292.9");
        test.setDiagnosis((Diagnosis)dbSingleton.db.getData("Diagnosis", "code='292.9'").get(0));
        test.setLabTest((LabTest)dbSingleton.db.getData("LabTest","id='86900'").get(0));
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
        for (Object obj : objs){
            System.out.println(obj);
            System.out.println("");
        }

        return "";
    }
}
