package com.amos.p1.backend.database;

import com.amos.p1.backend.data.EvaluationCandidate;
import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.data.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class RequestTest {

    private static final Logger log = LoggerFactory.getLogger(RequestTest.class);

    private final static LocalDateTime LOCAL_DATE_TIME_DUMMY = LocalDateTime.of(2020, 10, 30, 16, 30);
    @BeforeAll
    public static void init() {

        log.info("setting Database properties");
        MyRepo.setUseTestDatabase(true);
    }

    @BeforeEach
    void setUp(){

        log.info("reintialising Database");
        MyRepo.dropAll();
    }



    @Test
    void testInsertRequestNoIncident() {
        Request request = getDummyRequestWithIncidents(null);
        MyRepo.insertRequest(request);

        Request requestFromDatabase = MyRepo.getRequest(LOCAL_DATE_TIME_DUMMY);
        assertThat(requestFromDatabase, is(notNullValue()));
    }

    @Test
    void testInsertRequestOneIncident(){
        Request request = getDummyRequestWithOneDummyIncident();
        MyRepo.insertRequest(request);

        Request requestFromDatabase = MyRepo.getRequest(LOCAL_DATE_TIME_DUMMY);
        assertThat(requestFromDatabase, is(notNullValue()));
    }

    @Test
    void testInsertRequest100Incidents(){

        List<Incident> incidents = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Incident incident = DummyIncident.createIncident();
            incident.setCity("City " + i);
            incidents.add(incident);
        }
        Request request = getDummyRequestWithIncidents(incidents);
        MyRepo.insertRequest(request);

        Request requestFromDatabase = MyRepo.getRequest(LOCAL_DATE_TIME_DUMMY);
        assertThat(requestFromDatabase, is(notNullValue()));
    }

    @Test
    void testNoIncidentInDatabaseAfterDeletingInRequestObject(){
        Request request = createDummyRequest();
        MyRepo.insertRequest(request);
        Long id = request.getIncidents().get(0).getId();

        List<Request> requests1= new ArrayList<>();
        requests1.add(request);
        MyRepo.deleteRequests(requests1);
        System.out.println(request);

        assertThat(MyRepo.getIncidents(id).size(),equalTo(0));
    }

    @Test
    void TestDeleteRequest(){
        Request request = createDummyRequest();
        MyRepo.insertRequest(request);

        List<Request> requestsList= new ArrayList<>();
        requestsList.add(request);
        MyRepo.deleteRequests(requestsList);
        System.out.println(request);

        // no requests -> no incidents, no eval candidates
        assertThat(MyRepo.geRequestFromCityName(request.getCityName()).size(),equalTo(0));

        // no city information
        assertThat(MyRepo.getAllCityInformation().stream().filter(inf -> inf.getCityName().equals(request.getCityName())).findFirst().isPresent(),equalTo(false));

    }

    private Request getDummyRequestWithIncidents(List<Incident> incidents) {
        Request request = new Request();
        request.setRequestTime(LOCAL_DATE_TIME_DUMMY);
        request.setIncidents(incidents);

        return  request;
    }

    private Request getDummyRequestWithOneDummyIncident() {
        Incident incident = DummyIncident.createIncident();
        List<Incident> incidents = new ArrayList<>();
        incidents.add(incident);

        return getDummyRequestWithIncidents(incidents);
    }
    @Test
    void testGetRequestFromCityName(){
        Request request = getDummyRequestWithOneDummyIncident();
        request.setCityName("Berlin");
        MyRepo.insertRequest(request);

        List<Request> RequestAsList = MyRepo.geRequestFromCityName("Berlin");


        assertThat(RequestAsList, hasSize(greaterThan(0)));
    }
    @Test
    void testGetInsertEvaluationCandidate(){

     Request request = createDummyRequest();
     MyRepo.insertRequest(request);

        List<EvaluationCandidate> actual = MyRepo.geEvaluationCandidateFromRequestId(request.getId());
        log.info("" + actual);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    Request createDummyRequest(){
        List<Incident> incidents = new ArrayList<Incident>();
        incidents.add(
                new Incident("222","baustelle","major","Traffic jam in Bergmannstra??e",
                        "Berlin", "Germany", "45.5", "67.4",
                        "Bergmannstra??e",  "46.5", "69.5",
                        "Bergmannstra??e",  1, "0",
                        LocalDateTime.of( 2020, 5, 1, 12, 30, 0),
                        LocalDateTime.of( 2020, 5, 1, 12, 30, 0),
                        "670000:690000,681234:691234",6.0,new Long(1)));
        incidents.add(
                new Incident("222","baustelle","major","Traffic jam in Bergmannstra??e",
                        "Berlin", "Germany", "45.5", "67.4",
                        "Bergmannstra??e",  "46.5", "69.5",
                        "Bergmannstra??e",  1, "1",
                        LocalDateTime.of( 2020, 5, 1, 12, 30, 0),
                        LocalDateTime.of( 2020, 5, 1, 12, 30, 0),
                        "670000:690000,681234:691234",6.0,new Long(1)));

        Request request = new Request();
        request.setRequestTime(LocalDateTime.of(
                2020, 5, 1,
                12, 30, 0));
        request.setCityName("Berlin");
        request.setIncidents(incidents);

        List<EvaluationCandidate> evaluationCandidates = new ArrayList<EvaluationCandidate>();
        EvaluationCandidate evaluationCandidate = new EvaluationCandidate ();
        evaluationCandidate.setTomTomIncident(incidents.get(0));
        evaluationCandidate.setHereIncident(incidents.get(1));
        evaluationCandidates.add(evaluationCandidate);
        request.setEvaluatedCandidates(evaluationCandidates);

        return request;
    }


}
