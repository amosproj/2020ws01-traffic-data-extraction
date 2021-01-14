package com.amos.p1.backend.service;

import com.amos.p1.backend.data.Incident;
import com.amos.p1.backend.data.Request;
import com.amos.p1.backend.database.MyRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class ProviderIntervalRequest {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private ProviderNormalizer providerNormalizer;
    private boolean useDummy = false;

    public ProviderIntervalRequest(){
        providerNormalizer = new ProviderNormalizer(false);
    }

    public ProviderIntervalRequest(boolean useDummy) {
        this.useDummy = useDummy;
    }

    public void setProviderNormalizer(ProviderNormalizer providerNormalizer){
        this.providerNormalizer = providerNormalizer;
    }

    // Will be runned on startup
    // 1000 ms * 60 * 60 = 1 hour
    @Scheduled(fixedRate = 3600000)
    public void providerCronJob() {
        LocalDateTime now;

        if(useDummy){
            // 01.01.2020 00:00:00
            now = LocalDateTime.of(2020, 1,1,0,0,0);
        }else{
            now = LocalDateTime.now();
        }

        System.out.println("The time is now " + now);

        List<Request> requests = providerNormalizer.parseCurrentRequest();

        printLongestEdge(requests);

        for (Request request : requests) {
            request.setRequestTime(now);

            System.out.println("Save incidents into db. City: " + request.getCityName() +" Amount: " + request.getIncidents().size());
            MyRepo.insertRequest(request);
            System.out.println("Sucessfully saved");
        }

        System.out.println("Sucessfully saved everything");
    }

    private void printLongestEdge(List<Request> requests) {
        int size = 0;
        String longestEdge = "";
        for (Request request : requests) {
            for (Incident incident : request.getIncidents()) {

                String edges = incident.getEdges();
                if(size < edges.length()) {
                    longestEdge = edges;
                    size = edges.length();
                }
            }
        }
        System.out.println("Longes edge size: " + size);
        System.out.println("Longes edge " + longestEdge);
    }
}
