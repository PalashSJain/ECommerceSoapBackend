package business;

import components.data.*;
import service.DBSingleton;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

public class BusinessLayer {
    private DBSingleton dbSingleton;

    public BusinessLayer() {
        dbSingleton = DBSingleton.getInstance();
    }

    // 8am to 5pm
    public boolean isTimeValid(Time time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.getTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 8 && hour < 17 && (hour != 16 || calendar.get(Calendar.MINUTE) <= 45);
    }

    public boolean isAppointmentAvailable(Patient patient, Phlebotomist phlebotomist, PSC psc, Time time, Date date) {
        if (hasAppointment(patient, time, date)) return false;

        Appointment appointment = getLatestAppointment(phlebotomist, time, date);
        if (appointment == null) {
            return true;
        }

        Time apptTime = appointment.getAppttime();
        long diff = time.getTime() - apptTime.getTime();
        long minute = 60 * 1000;
        if (appointment.getPscid().getId().equals(psc.getId())) {
            return diff / minute >= 15;
        } else {
            return diff / minute >= 45;
        }
    }

    private Appointment getLatestAppointment(Phlebotomist phlebotomist, Time time, Date date) {
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.setTimeInMillis(date.getTime());

        Appointment a = null;
        List<Object> appointments = dbSingleton.db.getData("Appointment", "phlebid='" + phlebotomist.getId() + "' and apptdate='" + date.toString() + "'");

        for (Object obj : appointments) {
            Appointment appointment = (Appointment) obj;
            if (appointment.getAppttime().getTime() <= time.getTime()) a = appointment;
        }
        return a;
    }

    private boolean hasAppointment(Patient patient, Time time, Date date) {
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.setTimeInMillis(date.getTime());
        String d = (dCalendar.get(Calendar.MONTH) + 1) + "/" + dCalendar.get(Calendar.DATE) + "/" + dCalendar.get(Calendar.YEAR); // 2/1/2017

        List<Object> appointments = dbSingleton.db.getData("Appointment", "patientid='" + patient.getId() + "' and apptdate='" + d + "'");
        if (appointments == null || appointments.isEmpty()) return false;

        Calendar tCalendar = Calendar.getInstance();
        tCalendar.setTimeInMillis(time.getTime());

        ListIterator<Object> iterator = appointments.listIterator();
        while (iterator.hasNext()) {
            Appointment appointment = (Appointment) iterator.next();
            Calendar aCalendar = Calendar.getInstance();
            aCalendar.setTimeInMillis(appointment.getAppttime().getTime());

            int aMin = (aCalendar.get(Calendar.HOUR) * 60) + aCalendar.get(Calendar.MINUTE);
            int tMin = (tCalendar.get(Calendar.HOUR) * 60) + tCalendar.get(Calendar.MINUTE);

            if (Math.abs(aMin - tMin) >= 15) {
                iterator.remove();
            }
        }

        return appointments.size() > 0;
    }

    public Patient getPatient(String patientId) {
        int id;
        try {
            id = Integer.parseInt(patientId);
        } catch (Exception e) {
            return null;
        }
        List<Object> patients = dbSingleton.db.getData("Patient", "id='" + id + "'");
        return (Patient) getObject(patients);
    }

    public Phlebotomist getPhlebotomist(String phlebotomistId) {
        int id;
        try {
            id = Integer.parseInt(phlebotomistId);
        } catch (Exception e) {
            return null;
        }
        List<Object> phlebotomists = dbSingleton.db.getData("Phlebotomist", "ID='" + id + "'");
        return (Phlebotomist) getObject(phlebotomists);
    }

    public Physician getPhysician(String physicianId) {
        int id;
        try {
            id = Integer.parseInt(physicianId);
        } catch (Exception e) {
            return null;
        }
        List<Object> physicians = dbSingleton.db.getData("Physician", "id='" + id + "'");
        return (Physician) getObject(physicians);
    }

    private Object getObject(List<Object> objects) {
        if (objects.isEmpty() || objects.size() > 1) return null;
        else return objects.get(0);
    }

    public PSC getPSC(String pscId) {
        int id;
        try {
            id = Integer.parseInt(pscId);
        } catch (Exception e) {
            return null;
        }
        List<Object> PSCs = dbSingleton.db.getData("PSC", "id='" + id + "'");
        return (PSC) getObject(PSCs);
    }

    public LabTest getLabTest(String labTestId) {
        int id;
        try {
            id = Integer.parseInt(labTestId);
        } catch (Exception e) {
            return null;
        }
        List<Object> labTests = dbSingleton.db.getData("LabTest", "id='" + id + "'");
        return (LabTest) getObject(labTests);
    }

    public Diagnosis getDiagnosis(String dxcode) {

        List<Object> dxcodes = dbSingleton.db.getData("Diagnosis", "CODE='" + dxcode + "'");
        return (Diagnosis) getObject(dxcodes);
    }

    public String getNewAppointmentID() {
        List<Object> appointments = dbSingleton.db.getData("Appointment", "ID is not null order by id desc");
        if (appointments.isEmpty()) return "1";
        else return String.valueOf(Integer.parseInt(((Appointment) appointments.get(0)).getId()) + 10);
    }

    public String initialize() {
        dbSingleton = DBSingleton.getInstance();
        dbSingleton.db.initialLoad("LAMS");
        return "Database Initialized";
    }

    public List<Object> getData(String table, String query) {
        return dbSingleton.db.getData(table, query);
    }

    public boolean addData(Object obj) {
        return dbSingleton.db.addData(obj);
    }
}
