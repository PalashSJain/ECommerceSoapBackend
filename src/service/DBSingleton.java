package service;

import components.data.*;

public class DBSingleton {

   public IComponentsData db; 

   private static DBSingleton instance = null;
   private DBSingleton() {
      // Exists only to defeat instantiation.
      db = new DB();
   }
   public static DBSingleton getInstance() {
      if(instance == null) {
         instance = new DBSingleton();
      }
      return instance;
   }
   
   //needed for after adding new appointment and getting appointment from 
   //labtest if getting 500 error /null pointer error for labtest.getAppointment() or
   //or other items still in cache but not being retrieved.
   public static DBSingleton resetConnection() {
   		instance = new DBSingleton();
   		return instance;
   }
}