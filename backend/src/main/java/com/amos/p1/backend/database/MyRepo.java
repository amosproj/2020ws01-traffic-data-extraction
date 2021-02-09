package com.amos.p1.backend.database;

import com.amos.p1.backend.data.CityInformation;
import com.amos.p1.backend.data.EvaluationCandidate;
import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.data.Request;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyRepo {

    private static final Logger log = LoggerFactory.getLogger(MyRepo.class);

    private static final DatabaseConfig databaseConfig = new DatabaseConfig();
    private static final MyRepo instance = new MyRepo();
    private EntityManager em;
    private EntityManagerFactory emf;

    private String url;

    private MyRepo() {
        log.info("My Repo start");

        url = databaseConfig.getURL() + "/" + databaseConfig.getDatabaseName() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin&createDatabaseIfNotExist=true";
        log.info("Connect to db: " + url);

        intialiseDB(url);

        Map<String, Object> persistenceMap = new HashMap<>();
        persistenceMap.put("javax.persistence.jdbc.url", url);
        emf = Persistence.createEntityManagerFactory("MyRepo", persistenceMap);
        em = emf.createEntityManager();
    }

    public static void setUseTestDatabase(boolean useTestDatabase) {

    }

    public static void intialiseDB(String url) {

        final String jdbcDriver = databaseConfig.getJdbcDriver();
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            //intialise schema in datadb
            Connection connection = DriverManager.getConnection(url , "root", "root");

            ScriptRunner scriptRunner = new ScriptRunner(connection);

            ClassPathResource classPathResource = new ClassPathResource("schema.sql");
            InputStream schemaInputstream = classPathResource.getInputStream();
            Reader targetReader = new InputStreamReader(schemaInputstream);

            scriptRunner.runScript(new BufferedReader(targetReader));
            targetReader.close();
        }catch (Exception e){
            throw new IllegalStateException(e);
        }

    };
    public static EntityManager getEntityManager(){
        return instance.em;
    }

    public static EntityManagerFactory getEntityManagerFactory(){
        return instance.emf;
    }

    public static void dropAll(){
        intialiseDB(instance.url);
    }

    public static void insertIncident(List<Incident> incidents) {


        for(Incident incident : incidents) {
            try{
                getEntityManager().getTransaction().begin();
                getEntityManager().persist(incident);
                getEntityManager().getTransaction().commit();
            }catch (Exception e){
                log.info("Error with incident: " + incident);
            }
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

        log.info("" + cityInformations);

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

        try{
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
        }catch (Exception e){
            log.info("Error while inserting into db: ");
            e.printStackTrace();
        }

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
