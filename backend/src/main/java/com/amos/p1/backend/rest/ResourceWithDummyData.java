package com.amos.p1.backend.rest;

import com.amos.p1.backend.Helper;
import com.amos.p1.backend.data.ComparisonEvaluationDTO;
import com.amos.p1.backend.data.EvaluationCandidate;
import com.amos.p1.backend.data.Incident;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Restcontroller only for static demo purposes. Only use dummy strings for returning to client
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("demo")
public class ResourceWithDummyData {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/incidents",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<String> getIncidents(@RequestParam("city") String city,
                                                     @RequestParam("timestamp") Optional<String> timestamp,
                                                     @RequestParam("types") Optional<String> types){

        String incidents = Helper.getFileResourceAsString("rest-endpoint-dummy/berlin_incidents.json");

        return ResponseEntity.ok(incidents);

    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/timestamps",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<String>> getTimestampsByCity(@RequestParam("city") String city){

        List<String> timestamps = new ArrayList<>();
        timestamps.add("2020-12-19 12:00");
        timestamps.add("2020-12-19 13:00");

        return ResponseEntity.ok(timestamps);
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comparison",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<EvaluationCandidate>> getComparison(@RequestParam("city") String city,
                                                                   @RequestParam("timestamp") String timestamp) {
        List<EvaluationCandidate> listEvalCans = new ArrayList<>();

        Incident incidentTomTom = new Incident("222","baustelle","major",
                "Traffic jam in Bergmannstra??e",
                "Berlin", "Germany",
                "45.5", "67.4",
                "Bergmannstra??e",
                "46.5", "69.5",
                "Bergmannstra??e",
                1, "dummy",
                LocalDateTime.of(
                        2020, 5, 1,
                        12, 30, 0),
                LocalDateTime.of(
                        2020, 5, 1,
                        12, 30, 0),
                "670000:690000,681234:691234",6.0,new Long(1));

        Incident incidentHere = new Incident("123","baustelle","major",
                "Traffic jam in Bergmannstra??e",
                "Munich", "Germany",
                "45.5", "67.4",
                "Bergmannstra??e",
                "46.5", "69.5",
                "Bergmannstra??e",
                1, "dummy",
                LocalDateTime.of(
                        2020, 5, 1,
                        12, 30, 0),
                LocalDateTime.of(
                        2020, 5, 1,
                        12, 30, 0),
                "670000:690000,681234:691234",6.0,new Long(1));

        EvaluationCandidate evaluationCandidate = new EvaluationCandidate(incidentTomTom, incidentHere);
        EvaluationCandidate evaluationCandidate2 = new EvaluationCandidate(incidentTomTom, incidentHere);
        listEvalCans.add(evaluationCandidate);
        listEvalCans.add(evaluationCandidate2);

        return ResponseEntity.ok(listEvalCans);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comparisonEvaluationOverTime",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ComparisonEvaluationDTO>> getComparisonEvaluationOverTime(@RequestParam("city") String city) {

        List<ComparisonEvaluationDTO> listCEDTO = new ArrayList<>();

        ComparisonEvaluationDTO comparisonEvaluationDTO0 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO0.setDate(LocalDateTime.of(2021, 1, 1, 10,00));
        comparisonEvaluationDTO0.setHereIncidentsAmount(40);
        comparisonEvaluationDTO0.setTomTomIncidentsAmount(55);
        comparisonEvaluationDTO0.setSameIncidentAmount(20);
        listCEDTO.add(comparisonEvaluationDTO0);

        ComparisonEvaluationDTO comparisonEvaluationDTO1 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO1.setDate(LocalDateTime.of(2021, 1, 1, 1,00));
        comparisonEvaluationDTO1.setHereIncidentsAmount(30);
        comparisonEvaluationDTO1.setTomTomIncidentsAmount(56);
        comparisonEvaluationDTO1.setSameIncidentAmount(23);
        listCEDTO.add(comparisonEvaluationDTO1);

        ComparisonEvaluationDTO comparisonEvaluationDTO2 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO2.setDate(LocalDateTime.of(2021, 1, 1, 12,00));
        comparisonEvaluationDTO2.setHereIncidentsAmount(20);
        comparisonEvaluationDTO2.setTomTomIncidentsAmount(45);
        comparisonEvaluationDTO2.setSameIncidentAmount(13);
        listCEDTO.add(comparisonEvaluationDTO2);

        ComparisonEvaluationDTO comparisonEvaluationDTO3 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO3.setDate(LocalDateTime.of(2021, 1, 1, 13,00));
        comparisonEvaluationDTO3.setHereIncidentsAmount(40);
        comparisonEvaluationDTO3.setTomTomIncidentsAmount(55);
        comparisonEvaluationDTO3.setSameIncidentAmount(20);
        listCEDTO.add(comparisonEvaluationDTO3);

        ComparisonEvaluationDTO comparisonEvaluationDTO4 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO4.setDate(LocalDateTime.of(2021, 1, 1, 15,00));
        comparisonEvaluationDTO4.setHereIncidentsAmount(50);
        comparisonEvaluationDTO4.setTomTomIncidentsAmount(73);
        comparisonEvaluationDTO4.setSameIncidentAmount(18);
        listCEDTO.add(comparisonEvaluationDTO4);

        ComparisonEvaluationDTO comparisonEvaluationDTO5 = new ComparisonEvaluationDTO();
        comparisonEvaluationDTO5.setDate(LocalDateTime.of(2021, 1, 1, 16,00));
        comparisonEvaluationDTO5.setHereIncidentsAmount(77);
        comparisonEvaluationDTO5.setTomTomIncidentsAmount(43);
        comparisonEvaluationDTO5.setSameIncidentAmount(35);
        listCEDTO.add(comparisonEvaluationDTO5);

        return ResponseEntity.ok(listCEDTO);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/someDateEndpoint",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Date> getSomeDateEndpoint(@RequestParam("city") String cityName) {

        return ResponseEntity.ok(new Date(123));
    }

}