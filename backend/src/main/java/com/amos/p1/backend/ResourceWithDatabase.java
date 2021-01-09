package com.amos.p1.backend;

import com.amos.p1.backend.data.CityBoundingBox;
import com.amos.p1.backend.data.ComparisonEvaluationDTO;
import com.amos.p1.backend.data.EvaluationCandidate;
import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.service.CityBoundingBoxesService;
import com.amos.p1.backend.service.aggregator.Aggregator;
import com.amos.p1.backend.service.aggregator.AggregatorDirectlyFromProvider;
import com.amos.p1.backend.service.aggregator.AggregatorFromDatabase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://0.0.0.0:8080")
@RequestMapping("withDatabase")
public class ResourceWithDatabase {

    private final Aggregator aggregator = new AggregatorFromDatabase();

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/incidents",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<Incident>> getIncidents(@RequestParam("city") String city,
                                                       @RequestParam("timestamp") Optional<String> timestamp,
                                                       @RequestParam("types") Optional<String> types){

        Optional<LocalDateTime> timestampParsed = Optional.empty();
        Optional<List<String>> typesParsed = Optional.empty();

        if(timestamp.isPresent()){
            timestampParsed = Optional.of(parseTimeStamp(timestamp.get()));
        }

        if(types.isPresent()){
            typesParsed = Optional.of(parseTypes(types.get()));
        }

        List<Incident> incidents = aggregator.getIncidents(city, timestampParsed, typesParsed);
        return ResponseEntity.ok(incidents);
    }

    private LocalDateTime parseTimeStamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(timestamp, formatter);
    }

    private List<String> parseTypes(String types) {
        return Arrays.asList(types.split(","));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/timestamps",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<String>> getTimestampsByCity(@RequestParam("city") String city) {

        List<LocalDateTime> localDateTimes = aggregator.getTimestampsFromCity(city);
        List<String> timestamps = parseLocalDateTimes(localDateTimes);

        return ResponseEntity.ok(timestamps);
    }

    private List<String> parseLocalDateTimes(List<LocalDateTime> localDateTimes) {
        //Strings need to have this pattern: yyyy-MM-dd HH:mm.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<String> timestampsAsStrings = new ArrayList<String>();

        for (LocalDateTime time : localDateTimes) {
            timestampsAsStrings.add(time.format(formatter));
        }

        return timestampsAsStrings;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/cities",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<CityBoundingBox>> getAllCities() {

        CityBoundingBoxesService cityBoundingBoxesService = new CityBoundingBoxesService();
        List<CityBoundingBox> cities = cityBoundingBoxesService.getCityBoundingBoxes();

        return ResponseEntity.ok(cities);

    }

    private List<String> parseCityList(List<CityBoundingBox> cityBoundingBoxes) {
        //TODO: change to lambda
        List<String> cities = new ArrayList<>();
        for (CityBoundingBox cityBoundingBox : cityBoundingBoxes) {
            cities.add(cityBoundingBox.getCity());
        }

        return cities;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comparison",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<EvaluationCandidate>> getComparison(@RequestParam("city") String city,
                                                                   @RequestParam("timestamp") String timestamp) {


        throw new IllegalStateException("Needs to be implemented");
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comparisonEvaluationOverTime",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ComparisonEvaluationDTO>> getComparisonEvaluationOverTime(@RequestParam("city") String city) {
        throw new IllegalStateException("Needs to be implemented");
    }

}
