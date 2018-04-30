package service.helper;

public class ResponseFormatFactory {
    public static IResponseFormat getResponseFormatter(String format){
        if (format.equalsIgnoreCase("xml")) {
            return new XMLResponseFormat();
        }

        return null;
    }
}
