package business;

import components.data.*;
import service.DBSingleton;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Palash on 4/9/2018.
 */
public class BusinessLayer {
    DBSingleton dbSingleton;

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
        if (hasAppointment(patient, time.getTime(), date)) return false;
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
        String d = (dCalendar.get(Calendar.MONTH)+1) + "/" + dCalendar.get(Calendar.DATE) + "/" + dCalendar.get(Calendar.YEAR); // 2/1/2017

        Appointment a = null;
        List<Object> appointments = dbSingleton.db.getData("Appointment", "phlebid='" + phlebotomist.getId() + "' and apptdate='" + d + "'");

        for (Object obj : appointments) {
            Appointment appointment = (Appointment) obj;
            if (appointment.getAppttime().getTime() <= time.getTime()) a = appointment;
        }
        return a;
    }

    private boolean hasAppointment(Patient patient, long t, Date date) {
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.setTimeInMillis(date.getTime());
        String d = (dCalendar.get(Calendar.MONTH)+1) + "/" + dCalendar.get(Calendar.DATE) + "/" + dCalendar.get(Calendar.YEAR); // 2/1/2017

        List<Object> appointments = dbSingleton.db.getData("Appointment", "patientid='" + patient.getId() + "' and apptdate='" + d + "'");
        for (Object obj : appointments) {
            Appointment appointment = (Appointment) obj;
            if (Math.abs(appointment.getAppttime().getTime() - t) < (15 * 60 * 1000)) {
                return true;
            }
        }
        return appointments != null && appointments.size() > 0;
    }

    public Patient getPatient(String patientId) {
        List<Object> patients = dbSingleton.db.getData("Patient", "id='" + patientId + "'");
        return (Patient) getObject(patients);
    }

    public Phlebotomist getPhlebotomist(String phlebotomistId) {
        List<Object> phlebotomists = dbSingleton.db.getData("Phlebotomist", "ID='" + phlebotomistId + "'");
        return (Phlebotomist) getObject(phlebotomists);
    }

    public Physician getPhysician(String physicianId) {
        List<Object> physicians = dbSingleton.db.getData("Physician", "id='" + physicianId + "'");
        return (Physician) getObject(physicians);
    }

    private Object getObject(List<Object> objects) {
        if (objects.isEmpty() || objects.size() > 1) return null;
        else return objects.get(0);
    }

    public PSC getPSC(String pscId) {
        List<Object> PSCs = dbSingleton.db.getData("PSC", "id='" + pscId + "'");
        return (PSC) getObject(PSCs);
    }

    public LabTest getLabTest(String labTestId) {
        List<Object> labTests = dbSingleton.db.getData("LabTest", "id='" + labTestId + "'");
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
}
