package com.amos.p1.backend.database;

import com.amos.p1.backend.data.CityInformation;
import com.amos.p1.backend.data.EvaluationCandidate;
import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.data.Request;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyRepo {

    private static final MyRepo instance = new MyRepo();
    private EntityManager em;
    private EntityManagerFactory emf;
    private EntityManager emTest;
    private EntityManagerFactory emfTest;
    private boolean useTestDatabase = false;

    private MyRepo() {

        final String elasticIp = getHostAdress();
        String url = "jdbc:mysql://" + elasticIp + ":3306/testdb3?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin&createDatabaseIfNotExist=true";

        try {
            intialiseDB(url);
        } catch (SQLException | FileNotFoundException throwables) {
            throwables.printStackTrace();
        }

        Map<String, Object> persistenceMap = new HashMap<>();
        persistenceMap.put("javax.persistence.jdbc.url", url);
        emf = Persistence.createEntityManagerFactory("MyRepo", persistenceMap);
        em = emf.createEntityManager();
    }

    /**
     * Return the ip adress of the host. Example: 192.168.0.183
     */
    private String getHostAdress() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String hostAddress = socket.getLocalAddress().getHostAddress();
            System.out.println(hostAddress);
            return hostAddress;
        } catch (SocketException | UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isUseTestDatabase() {  return instance.useTestDatabase;}
    public static void setUseTestDatabase(boolean useTestDatabase) { instance.useTestDatabase = useTestDatabase; }

    public  void intialiseDB(String url) throws SQLException, FileNotFoundException {

        final String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //intialise schema in datadb
        Connection connection = DriverManager.getConnection(url , "root", "root");

        ScriptRunner scriptRunner = new ScriptRunner(connection);
        FileReader fileReader = new FileReader("src/main/resources/schema.sql");
        scriptRunner.runScript(new BufferedReader(fileReader));
    };
    public static EntityManager getEntityManager(){
        if (instance.useTestDatabase == true) return instance.emTest;
        return instance.em;
    }

    public static EntityManagerFactory getEntityManagerFactory(){
        if (instance.useTestDatabase == true) return instance.emfTest;
        return instance.emf;
    }



    public static void dropAll(){
        String  URl = "jdbc:mysql://remotemysql.com:3306/lIkqLjf1AL";
        String   id = "lIkqLjf1AL";
        String   password = "yddtBbLwx1";

         String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        if (instance.useTestDatabase == true)
        {
             URl = "jdbc:mysql://localhost:3306/testdb3?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";
             id = "root";
              password = "root";

        }
        Connection con2 = null;
        try {
            con2 = DriverManager.getConnection(URl ,id,password);
            ScriptRunner scriptRunner = new ScriptRunner(con2);
            FileReader fileReader = new FileReader("src/main/resources/schema.sql");
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(new BufferedReader(fileReader));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



    public static void insertIncident(List<Incident> incidents) {


        for(Incident incident : incidents) {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(incident);
            getEntityManager().getTransaction().commit();
        }


    }
    public static void insertCityInformation(CityInformation cityInformation) {


            getEntityManager().getTransaction().begin();
            getEntityManager().persist(cityInformation);
            getEntityManager().getTransaction().commit();



    }

    public static List<CityInformation> getAllCityInformation(){



        return getEntityManager().createNamedQuery("getAllCityInformation").getResultList();
    }

    public static void deleteCityInformation(Long id ){

        getEntityManager().getTransaction().begin();
        List<CityInformation> cityInformations = getEntityManager().createNamedQuery("getCityInformationFromId")
                .setParameter("id", id)
                .getResultList();

        System.out.println(cityInformations);

        getEntityManager().remove(cityInformations.get(0));
        getEntityManager().getTransaction().commit();
    }


    public static void insertEvaluationCandidate(List<EvaluationCandidate> evaluationCandidates) {


        for(EvaluationCandidate evaluationCandidate : evaluationCandidates) {
            evaluationCandidate.setEvaluationCandidateSavedInDb(true);
            getEntityManager().getTransaction().begin();

            getEntityManager().persist(evaluationCandidate);

            getEntityManager().getTransaction().commit();
        }


    }
    public static List<Incident> getIncidents(Long id) {
        List<Incident> resultList = MyRepo.getEntityManager()
                .createNamedQuery("getFromids")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }


    public static void insertRequest(Request request){
        //TODO implement it. Request is the main table. Also incidents saving
        List<Incident> incidents =request.getIncidents();

        getEntityManager().getTransaction().begin();
        getEntityManager().persist(request);
        getEntityManager().getTransaction().commit();
        if(incidents==null)return ;
        for (Incident incident:incidents ) { incident.setRequestId(request.getId());
          //  incident.setEntryTime(request.getRequestTime());
      //      incident.setCity(request.getCityName());
        }


        insertIncident(incidents);
        request.setIncidentsSavedInDb(true);

        List<EvaluationCandidate> evaluationCandidates =request.getEvaluationCandidate();
        if(evaluationCandidates==null)return ;
        for (EvaluationCandidate EvaluationCandidate :evaluationCandidates) {
            EvaluationCandidate.setRequestId(request.getId());
            EvaluationCandidate.setTomTomIncidentId(EvaluationCandidate.getTomTomIncident().getId());
            EvaluationCandidate.setHereIncidentId(EvaluationCandidate.getHereIncident().getId());
        }


        MyRepo.insertEvaluationCandidate(evaluationCandidates);
        request.setEvaluationCandidateSavedInDb(true);
    }

    public static Request getRequest(LocalDateTime requestTime){

        List<Request> requests =  getEntityManager().createNamedQuery("geRequestFromTime")
                .setParameter("requestTime" ,requestTime )
                .getResultList();

        Request request =requests.get(0) ;
        request.setIncidentsSavedInDb(true);
        if (request.getEvaluationCandidate()==null||request.getEvaluationCandidate().size()==0)
            return requests.get(0);
        request.setIncidentsSavedInDb(true);
        for(EvaluationCandidate evaluationCandidate : request.getEvaluationCandidate()) {
            evaluationCandidate.setEvaluationCandidateSavedInDb(true);}
        request.setEvaluationCandidateSavedInDb(true);

        return request;
    }

    public static Request geRequestFromCityNameAndTime(String cityName,LocalDateTime requestTime){

        List<Request> requests =  MyRepo.getEntityManager().createNamedQuery("geRequestFromCityNameAndTime")
                .setParameter("requestTime", requestTime )
                .setParameter("cityName",  cityName)
                .getResultList();

        Request request =requests.get(0) ;
        request.setIncidentsSavedInDb(true);
        if (request.getEvaluationCandidate()==null||request.getEvaluationCandidate().size()==0)
            return requests.get(0);
        request.setIncidentsSavedInDb(true);

        for(EvaluationCandidate evaluationCandidate : request.getEvaluationCandidate()) {
            evaluationCandidate.setEvaluationCandidateSavedInDb(true);}
        request.setEvaluationCandidateSavedInDb(true);

        return request;
    }

    public static Request geRequestFromCityName(String cityName){

        List<Request> requests =  MyRepo.getEntityManager().createNamedQuery("geRequestFromCityName")
                .setParameter("cityName",  cityName)
                .getResultList();

        Request request =requests.get(0) ;
        request.setIncidentsSavedInDb(true);
        if (request.getEvaluationCandidate()==null||request.getEvaluationCandidate().size()==0)
            return requests.get(0);
        request.setIncidentsSavedInDb(true);

        for(EvaluationCandidate evaluationCandidate : request.getEvaluationCandidate()) {
            evaluationCandidate.setEvaluationCandidateSavedInDb(true);}
        request.setEvaluationCandidateSavedInDb(true);

        return request;
    }
    public static List<EvaluationCandidate> geEvaluationCandidateFromRequestId(Long requestId){

        List<EvaluationCandidate> evaluationCandidates =  MyRepo.getEntityManager().createNamedQuery("getEvaluationCandidateFromRequestId")
                .setParameter("requestId",  requestId)
                .getResultList();



        for(EvaluationCandidate evaluationCandidate : evaluationCandidates) {
            evaluationCandidate.setEvaluationCandidateSavedInDb(true);}

        return evaluationCandidates;
    }




}
