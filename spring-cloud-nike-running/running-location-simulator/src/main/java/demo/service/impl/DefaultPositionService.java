package demo.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import demo.model.CurrentPosition;
import demo.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DefaultPositionService implements PositionService {

    // After service registration & discovery
    // change #1: DI RestTemplate
    @Autowired
    private RestTemplate restTemplate;

//    // change #2: comment out here
//    @Value("${com.cj.running.location.distribution}")
//    private String runningLocationDistribution;

    // hystrix Change #1: Add @HystrixCommand annotation here to specify the hystrix fallback function
    // Note that the fallback function must have the identical parameters, i.e. same number of parameters in same type.
    @HystrixCommand(fallbackMethod = "processPositionInfoFallback")
    @Override
    public void processPositionInfo(long id, CurrentPosition currentPosition, boolean sendPositionsToDistributionService) {
        // change #3: use application name. Previously we were using http://localhost:9006
        String runningLocationDistribution = "http://running-location-distribution";
        if (sendPositionsToDistributionService) {
            log.info(String.format("Thread %d Simulator is calling distribution REST API", Thread.currentThread().getId()));
            this.restTemplate.postForLocation(runningLocationDistribution + "/api/locations", currentPosition);
        }
    }

    // hystrix Change #2: Add a hystrix fallback function.
    // This function will be triggered when above processPositionInfo function encounters some problem.
    public void processPositionInfoFallback(long id, CurrentPosition currentPosition, boolean sendPositionsToDistributionService) {
        log.error("Hystrix Fallback Method. Unable to send message for distribution.");
    }
}
