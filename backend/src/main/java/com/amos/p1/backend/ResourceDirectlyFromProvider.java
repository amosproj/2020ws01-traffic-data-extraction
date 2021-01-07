package com.amos.p1.backend;

import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.service.IncidentAggregator;
import com.amos.p1.backend.service.IncidentAggregatorDirectlyFromProvider;
import com.amos.p1.backend.service.IncidentAggregatorFromDatabase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://0.0.0.0:8080")
@RequestMapping("directlyFromProvider")
public class ResourceDirectlyFromProvider {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/incidents",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<Incident>> getIncidentsByCity(@RequestParam("city") String city){

        IncidentAggregator incidentAggregator = new IncidentAggregatorDirectlyFromProvider();

        return ResponseEntity.ok(incidentAggregator.getFromCity(city));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/incidentsWithTypes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<Incident>> getIncidentsByCityAndType(@RequestParam("city") String city, @RequestParam("types") String types){

        IncidentAggregator incidentAggregator = new IncidentAggregatorDirectlyFromProvider();

        return ResponseEntity.ok(incidentAggregator.getFromCityAndTypes(city, parseTypes(types)));
    }

    private List<String> parseTypes(String types) {
        return Arrays.asList(types.split(","));
    }

}
