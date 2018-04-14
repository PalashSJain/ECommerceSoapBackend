package business;

import service.DBSingleton;

import java.util.List;

/**
 * Created by Palash on 4/9/2018.
 */
public class BusinessLayer {
    DBSingleton dbSingleton;

    public BusinessLayer() {
        dbSingleton = DBSingleton.getInstance();
    }

    public boolean isPatientValid(String id) {
        List<Object> objs = dbSingleton.db.getData("Patient", "ID='" + id + "'");
        return !objs.isEmpty();
    }

    public boolean isPhysicianValid(String id) {
        List<Object> objs = dbSingleton.db.getData("Physician", "ID='" + id + "'");
        return !objs.isEmpty();
    }

    public boolean isLabTestValid(String id) {
        List<Object> objs = dbSingleton.db.getData("LabTest", "ID='" + id + "'");
        return !objs.isEmpty();
    }

    public boolean isDiagnosisCodeValid(String id) {
        List<Object> objs = dbSingleton.db.getData("Diagnosis", "CODE='" + id + "'");
        return !objs.isEmpty();
    }

    public boolean isPhlebotomistValid(String id) {
        List<Object> objs = dbSingleton.db.getData("Phlebotomist", "ID='" + id + "'");
        return !objs.isEmpty();
    }

    public boolean isTimeValid(String time) {

        return false;
    }

    public boolean isDateValid(String date) {
        return false;
    }

    public boolean isAppointmentAvailable(String time, String date) {
        return false;
    }
}
