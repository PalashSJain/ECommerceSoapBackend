package service;

import java.util.Set;
import javax.ws.rs.core.Application;
import business.*;

@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application{

    @Override
    public Set<Class<?>> getClasses(){
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses(){
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.add(business.BusinessLayer.class);
        resources.add(service.DBSingleton.class);
        resources.add(service.LAMSService.class);
        return resources;
    }

}