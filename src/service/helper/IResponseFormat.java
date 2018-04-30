package service.helper;

import java.util.List;

public interface IResponseFormat {
    String getInitializedOutput();

    String createAppointments(List<Object> appointments);
    String getAppointmentWithID(String appointNumber);
    String getDefaultAppointmentUnavailable();
    String addAppointment(String xml, String newAppointmentID);

    String createPSCs(List<Object> PSCs);
    String getPSCWithID(String pscID);
    String getDefaultPSCEmpty();

}
