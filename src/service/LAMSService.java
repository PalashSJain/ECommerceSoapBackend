package service;

import business.BusinessLayer;
import service.helper.IResponseFormat;
import service.helper.ResponseFormatFactory;

import javax.ws.rs.*;
import java.util.List;

@Path("Services")
public class LAMSService {
    private BusinessLayer businessLayer;
    private IResponseFormat formatter;

    /**
     * Initializes the Business Layer
     *
     * @return String 'Database initialized'
     */
    @GET
    @Produces("application/xml")
    public String initialize() {
        businessLayer = new BusinessLayer();
        businessLayer.initialize();
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        return formatter.getInitializedOutput();
    }

    /**
     * Return a list of all appointments and related information.
     *
     * @return String XML formatted string of all appointments
     */
    @Path("Appointments")
    @GET
    @Produces("application/xml")
    public String getAllAppointments() {
        businessLayer = new BusinessLayer();
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        List<Object> appointments = businessLayer.getData("Appointment", "");
        if (appointments.isEmpty())
            return formatter.getDefaultAppointmentUnavailable();

        return formatter.createAppointments(appointments);
    }

    @Path("PSCs")
    @GET
    @Produces("application/xml")
    public String getAllPSCs() {
        businessLayer = new BusinessLayer();
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        List<Object> PSCs = businessLayer.getData("PSC", "");
        if (PSCs.isEmpty())
            return formatter.getDefaultPSCEmpty();

        return formatter.createPSCs(PSCs);
    }

    /**
     * Return a specific appointment and related information
     *
     * @param appointNumber String appointment number
     * @return String XML formatted string of appointment number appointNumber
     */
    @Path("Appointments/{appointment}")
    @GET
    @Produces("application/xml")
    public String getAppointment(@PathParam("appointment") String appointNumber) {
        return formatter.getAppointmentWithID(appointNumber);
    }

    @Path("Appointments/{appointment}/delete")
    @DELETE
    @Produces("application/xml")
    public String deleteAppointment(@PathParam("appointment") String appointNumber) {
        businessLayer = new BusinessLayer();
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        if (businessLayer.deleteData("AppointmentLabTest", "apptid='" + appointNumber + "'") && businessLayer.deleteData("Appointment", "id='" + appointNumber + "'")) {
            return "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" +
                    "<AppointmentList>" +
                    "<success>DELETED: Appointment deleted.</success>" +
                    "</AppointmentList>";
        } else {
            return formatter.getDefaultAppointmentUnavailable();
        }
    }

    @Path("PSCs/{psc}")
    @GET
    @Produces("application/xml")
    public String getPSC(@PathParam("psc") String pscNumber) {
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        return formatter.getPSCWithID(pscNumber);
    }

    /**
     * Create a new appointment providing the required information in XML and receiving XML or error message
     *
     * @param xml String for appointment input
     * @return String xml formatted get appointment information of newly added appointment
     */
    @Path("Appointments")
    @PUT
    @Consumes({"text/xml", "application/xml"})
    @Produces("application/xml")
    public String addAppointment(String xml) {
        xml = xml.trim();
        businessLayer = new BusinessLayer();
        formatter = ResponseFormatFactory.getResponseFormatter("xml");
        String newAppointmentID = businessLayer.getNewAppointmentID();

        return formatter.addAppointment(xml, newAppointmentID);
    }
}
