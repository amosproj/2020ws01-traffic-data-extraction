package com.amos.p1.backend.data;
import com.amos.p1.backend.database.MyRepo;

import javax.persistence.*;
import java.time.LocalDateTime ;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NamedQuery(
        name="geRequestFromTime",
        query="SELECT r FROM Request r WHERE r.requestTime = :requestTime"
)
@NamedQuery(
        name="geRequestFromCityName",
        query="SELECT r FROM Request r WHERE r.cityName = :cityName"
)
@NamedQuery(
        name="geRequestFromCityNameAndTime",
        query="SELECT r FROM Request r WHERE r.cityName = :cityName AND r.requestTime = :requestTime"
)

@Entity
public class Request {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime requestTime;
    String cityName;
    @Transient
    List<Incident> incidents =null ;
    @Transient
    List<EvaluationCandidate> reEvaluatedCandidateList =null;
    @Transient
    Boolean incidentSavedInDb= false ;
    @Transient
    Boolean evaluationCandidateSavedInDb= false ;


    public Request() {
        super();
    }
    // parses Incidents to string ids of incidents
    public void setIncidents(List<Incident> incidents){

        this.incidents = incidents;

    }

    // parses string ids of incidents to list of Incidents
    public List<Incident> getIncidents(){
       if (incidentSavedInDb == true) {

           List<Incident> incidentAsList;
           incidentAsList = MyRepo.getEntityManager().createNamedQuery("getFromRequestId")
                   .setParameter("requestId", getId())
                   .getResultList();

           System.out.println(getId());
           // update if anything gets deleted from incidents
           setIncidents(incidentAsList);
           return incidentAsList;

       }
           return incidents;


    }


    public Boolean getIncidentsSavedInDb() {return incidentSavedInDb; }
    public void setIncidentsSavedInDb(Boolean incidentSavedInDb) {this.incidentSavedInDb = incidentSavedInDb; }

    @Basic
    @Column(name = "id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;  }
    @Basic
    @Column(name = "requestTime",columnDefinition="DATETIME")
    public LocalDateTime getRequestTime() { return requestTime;}
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    @Basic
    @Column(name = "cityName", nullable = true)
    public String getCityName() {        return cityName;    }
    public void setCityName(String cityName) { this.cityName = cityName; }


    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", requestTime=" + requestTime +
                ", incidents=" + getIncidents() +
                ", incidentSavedInDb=" + incidentSavedInDb +
                '}';
    }

    public void setEvaluatedCandidates(List<EvaluationCandidate> reEvaluatedCandidateList) {

        this.reEvaluatedCandidateList=reEvaluatedCandidateList;
    }



    public void setEvaluationCandidateSavedInDb(Boolean evaluationCandidateSavedInDb) {
        this.evaluationCandidateSavedInDb = evaluationCandidateSavedInDb;
    }

    public List<EvaluationCandidate> getEvaluationCandidate(){
        if (evaluationCandidateSavedInDb ==false) return reEvaluatedCandidateList;
        List<EvaluationCandidate> evaluationCandidateAsList;
        evaluationCandidateAsList = MyRepo.getEntityManager().createNamedQuery("getEvaluationCandidateFromRequestId")
                .setParameter("requestId", getId())
                .getResultList();


        return evaluationCandidateAsList;
    }
}
