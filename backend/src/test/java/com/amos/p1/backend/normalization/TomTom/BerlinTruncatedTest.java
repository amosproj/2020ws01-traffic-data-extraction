package com.amos.p1.backend.normalization.TomTom;

import com.amos.p1.backend.Helper;
import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.normalization.HereNormalization;
import com.amos.p1.backend.normalization.JsonToIncident;
import com.amos.p1.backend.normalization.TomTomNormalization;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BerlinTruncatedTest {

    private final List<Incident> incidentList;

    public BerlinTruncatedTest(){
        String json = Helper.getFileResourceAsString("normalization/TomTomData/BerlinTruncated.json");

        JsonToIncident jsonNormalizer = new TomTomNormalization();
        incidentList = jsonNormalizer.normalize(json);
    }

    @Test
    void testIncidentAmount(){
        assertEquals(4, incidentList.size());
    }

}
