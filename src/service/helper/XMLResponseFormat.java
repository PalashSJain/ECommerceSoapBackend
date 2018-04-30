package service.helper;

import business.BusinessLayer;
import components.data.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class XMLResponseFormat implements IResponseFormat {
    private BusinessLayer businessLayer;

    private String getAppointmentURIXML(List<Object> appointments) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document doc = getAppointmentsURIXML(appointments, dbFactory);
            return asString(doc);
        } catch (ParserConfigurationException e) {
            return getDefaultAppointmentUnavailable();
        }
    }

    private Document getAppointmentsURIXML(List<Object> appointments, DocumentBuilderFactory dbFactory) throws ParserConfigurationException {
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element appointmentList = doc.createElement("AppointmentList");
        doc.appendChild(appointmentList);

        for (Object appointment : appointments) {
            appointmentList.appendChild(createURIXML(doc, businessLayer.getContext() + "Services/Appointments/" + ((Appointment) appointment).getId()));
        }
        return doc;
    }

    private Document getPSCsXML(List<Object> PSCs, DocumentBuilderFactory dbFactory) throws ParserConfigurationException {
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element pscList = doc.createElement("PSCList");
        doc.appendChild(pscList);

        for (Object PSC : PSCs) {
            pscList.appendChild(createPSCXML(doc, (components.data.PSC) PSC));
        }
        return doc;
    }

    private Document getAppointmentsXML(List<Object> appointments, DocumentBuilderFactory dbFactory) throws ParserConfigurationException {
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element appointmentList = doc.createElement("AppointmentList");
        doc.appendChild(appointmentList);

        for (Object appointment : appointments) {
            appointmentList.appendChild(createAppointmentXML(doc, (Appointment) appointment));
        }
        return doc;
    }

    private Element createPSCXML(Document doc, PSC pscObj) {
        Element psc = doc.createElement("psc");
        psc.setAttribute("id", pscObj.getId());

        Element name = doc.createElement("name");
        Text nameText = doc.createTextNode(pscObj.getName());
        name.appendChild(nameText);
        psc.appendChild(name);
        psc.appendChild(createURIXML(doc, businessLayer.getContext() + "Services/PSCs/" + pscObj.getId()));
        return psc;
    }

    private Element createAppointmentXML(Document doc, Appointment appointmentObj) {
        Element appointment = doc.createElement("appointment");

        appointment.setAttribute("date", changeDateFormat(appointmentObj.getApptdate(), "YYYY-MM-dd"));
        appointment.setAttribute("id", appointmentObj.getId());
        appointment.setAttribute("time", appointmentObj.getAppttime().toString());

        appointment.appendChild(createURIXML(doc, businessLayer.getContext() + "Services/Appointments/" + appointmentObj.getId()));
        appointment.appendChild(createPatientXML(doc, appointmentObj));
        appointment.appendChild(createPhlebotomistXML(doc, appointmentObj));
        appointment.appendChild(createPscXML(doc, appointmentObj));
        appointment.appendChild(createAllLabTestsXML(doc, appointmentObj));

        return appointment;
    }

    private Element createURIXML(Document doc, String uri) {
        Element wadl = doc.createElement("uri");
        Text wadlText = doc.createTextNode(uri);
        wadl.appendChild(wadlText);
        return wadl;
    }

    private Element createPatientXML(Document doc, Appointment appointmentObj) {
        Patient patientObj = appointmentObj.getPatientid();
        Element patient = doc.createElement("patient");
        patient.setAttribute("id", patientObj.getId());

        patient.appendChild(createURIXML(doc, ""));
        patient.appendChild(getElement(doc, "name", patientObj.getName()));
        patient.appendChild(getElement(doc, "address", patientObj.getAddress()));
        patient.appendChild(getElement(doc, "insurance", String.valueOf(patientObj.getInsurance())));
        patient.appendChild(getElement(doc, "dob", changeDateFormat(patientObj.getDateofbirth(), "YYYY-MM-dd")));

        return patient;
    }

    private Element createPhlebotomistXML(Document doc, Appointment appointmentObj) {
        Phlebotomist phlebotomistObj = appointmentObj.getPhlebid();
        Element phlebotomist = doc.createElement("phlebotomist");
        phlebotomist.setAttribute("id", phlebotomistObj.getId());

        phlebotomist.appendChild(createURIXML(doc, ""));
        phlebotomist.appendChild(getElement(doc, "name", phlebotomistObj.getName()));
        return phlebotomist;
    }

    private Element createPscXML(Document doc, Appointment appointmentObj) {
        PSC pscObj = appointmentObj.getPscid();
        Element psc = doc.createElement("psc");
        psc.setAttribute("id", pscObj.getId());

        psc.appendChild(createURIXML(doc, businessLayer.getContext() + "Services/PSCs/" + pscObj.getId()));
        psc.appendChild(getElement(doc, "name", pscObj.getName()));
        return psc;
    }

    private Element createAllLabTestsXML(Document doc, Appointment appointmentObj) {
        List<AppointmentLabTest> allLabTestsList = appointmentObj.getAppointmentLabTestCollection();
        Element allLabTests = doc.createElement("allLabTests");
        if (allLabTestsList != null) {
            for (AppointmentLabTest labTest : allLabTestsList) {
                allLabTests.appendChild(createAppointmentLabTestXML(doc, labTest));
            }
        }
        return allLabTests;
    }

    private Element createAppointmentLabTestXML(Document doc, AppointmentLabTest labTest) {
        Element appointmentLabTest = doc.createElement("appointmentLabTest");
        appointmentLabTest.setAttribute("appointmentId", labTest.getAppointment().getId());
        appointmentLabTest.setAttribute("dxcode", labTest.getDiagnosis().getCode());
        appointmentLabTest.setAttribute("labTestId", labTest.getLabTest().getId());

        appointmentLabTest.appendChild(createURIXML(doc, ""));
        return appointmentLabTest;
    }

    private String changeDateFormat(java.sql.Date date, String to) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(to);
        return DATE_FORMAT.format(date);
    }

    private String changeDateFormat(java.util.Date date, String to) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(to);
        return DATE_FORMAT.format(date);
    }

    private Node getElement(Document doc, String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        return el;
    }

    private String getAppointmentWithIDWithoutBody(String appointNumber) {
        businessLayer = new BusinessLayer();
        List<Object> appointments = businessLayer.getData("Appointment", "id='" + appointNumber + "'");
        if (appointments.isEmpty())
            return getDefaultAppointmentUnavailable();

        return getAppointmentURIXML(appointments);
    }

    private String asString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    // IResponseFormat methods

//    public String getAppointmentUnavailabilityError(Document output, Element element) {
//        element.appendChild(getElement(output, "error", "ERROR:Appointment is not available"));
//        return asString(output);
//    }

    public String getPSCWithID(String pscID) {
        businessLayer = new BusinessLayer();
        List<Object> PSCs = businessLayer.getData("PSC", "id='" + pscID + "'");
        if (PSCs.isEmpty())
            return getDefaultPSCEmpty();

        return createPSCs(PSCs);
    }

    public String getAppointmentWithID(String appointNumber) {
        businessLayer = new BusinessLayer();
        List<Object> appointments = businessLayer.getData("Appointment", "id='" + appointNumber + "'");
        if (appointments.isEmpty())
            return getDefaultAppointmentUnavailable();

        return createAppointments(appointments);
    }

    public String getDefaultAppointmentUnavailable() {
        return "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" +
                "<AppointmentList>" +
                "<error>ERROR:Appointment is not available</error>" +
                "</AppointmentList>";
    }

    public String createAppointments(List<Object> appointments) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document doc = getAppointmentsXML(appointments, dbFactory);
            return asString(doc);
        } catch (ParserConfigurationException e) {
            return getDefaultAppointmentUnavailable();
        }
    }

    public String getDefaultPSCEmpty() {
        return "<?xml version='1.0' encoding='UTF-8' standalone='no'?><PSCList><error>ERROR:PSC is not available</error></PSCList>";
    }

    public String createPSCs(List<Object> PSCs) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document doc = getPSCsXML(PSCs, dbFactory);
            return asString(doc);
        } catch (ParserConfigurationException e) {
            return getDefaultPSCEmpty();
        }
    }

    @Override
    public String getInitializedOutput() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element appointmentList = doc.createElement("AppointmentList");
            doc.appendChild(appointmentList);

            Element intro = doc.createElement("intro");
            Text introText = doc.createTextNode("Welcome to the LAMS Appointment Service");
            intro.appendChild(introText);
            appointmentList.appendChild(intro);

            Element wadl = doc.createElement("wadl");
            Text wadlText = doc.createTextNode(businessLayer.getContext() + "application.wadl");
            wadl.appendChild(wadlText);
            appointmentList.appendChild(wadl);

            return asString(doc);
        } catch (ParserConfigurationException e) {
            return "<AppointmentsList>Failed to load database</AppointmentsList>";
        }
    }

    @Override
    public String addAppointment(String xml, String newAppointmentID) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document output;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return getDefaultAppointmentUnavailable();
        }
        output = dBuilder.newDocument();

        Element appointmentList = output.createElement("AppointmentList");
        output.appendChild(appointmentList);

        Document input;
        InputSource is = new InputSource(new StringReader(xml));
        try {
            input = dBuilder.parse(is);
        } catch (SAXException | IOException e) {
            return getDefaultAppointmentUnavailable();
        }
        input.getDocumentElement().normalize();
        Element appointment = input.getDocumentElement();

        String patientId = appointment.getElementsByTagName("patientId").item(0).getTextContent();
        Patient patient = businessLayer.getPatient(patientId);
        if (patient == null) {
            return getDefaultAppointmentUnavailable();
        }

        String phlebotomistId = appointment.getElementsByTagName("phlebotomistId").item(0).getTextContent();
        Phlebotomist phlebotomist = businessLayer.getPhlebotomist(phlebotomistId);
        if (phlebotomist == null) {
            return getDefaultAppointmentUnavailable();
        }

        String physicianId = appointment.getElementsByTagName("physicianId").item(0).getTextContent();
        Physician physician = businessLayer.getPhysician(physicianId);
        if (physician == null) {
            return getDefaultAppointmentUnavailable();
        }

        String pscId = appointment.getElementsByTagName("pscId").item(0).getTextContent();
        PSC psc = businessLayer.getPSC(pscId);
        if (psc == null) {
            return getDefaultAppointmentUnavailable();
        }

        Element labTests = (Element) appointment.getElementsByTagName("labTests").item(0);
        NodeList nodes = labTests.getElementsByTagName("test");
        List<AppointmentLabTest> tests = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element node = (Element) nodes.item(i);
            String labTestId = node.getAttribute("id");
            LabTest labTest = businessLayer.getLabTest(labTestId);
            if (labTest == null) {
                return getDefaultAppointmentUnavailable();
            }

            String dxcode = node.getAttribute("dxcode");
            Diagnosis diagnosis = businessLayer.getDiagnosis(dxcode);
            if (diagnosis == null) {
                return getDefaultAppointmentUnavailable();
            }

            AppointmentLabTest test = new AppointmentLabTest(newAppointmentID, labTestId, dxcode);
            test.setDiagnosis(diagnosis);
            test.setLabTest(labTest);
            tests.add(test);
        }

        String t = appointment.getElementsByTagName("time").item(0).getTextContent();
        Time time;
        try {
            time = Time.valueOf(t + ":00");
            if (!businessLayer.isTimeValid(time)) {
                return getDefaultAppointmentUnavailable();
            }
        } catch (Exception e) {
            return getDefaultAppointmentUnavailable();
        }

        String d = appointment.getElementsByTagName("date").item(0).getTextContent();
        Date date;
        try {
            date = Date.valueOf(d);
        } catch (Exception e) {
            return getDefaultAppointmentUnavailable();
        }

        if (businessLayer.isAppointmentAvailable(patient, phlebotomist, psc, time, date)) {
            Appointment newAppt = new Appointment(newAppointmentID, date, time);
            newAppt.setAppointmentLabTestCollection(tests);
            newAppt.setPatientid(patient);
            newAppt.setPhlebid(phlebotomist);
            newAppt.setPscid(psc);

            for (AppointmentLabTest test : tests) {
                test.setAppointment(newAppt);
            }

            if (businessLayer.addData(newAppt)) {
                return getAppointmentWithIDWithoutBody(newAppointmentID);
            }
        }

        return getDefaultAppointmentUnavailable();
    }
}
