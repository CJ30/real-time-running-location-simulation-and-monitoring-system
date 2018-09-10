package demo.rest;

import demo.model.GpsSimulatorRequest;
import demo.model.SimulatorInitLocations;
import demo.service.GpsSimulatorFactoryService;
import demo.service.PathService;
import demo.task.LocationSimulator;
import demo.task.LocationSimulatorInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class LocationSimulatorRestApi {

    @Autowired
    // here we need to create a PathService
    private PathService pathService;

    @Autowired
    private GpsSimulatorFactoryService gpsSimulatorFactoryService;

    @Autowired
    //here we need to add a Spring Bean in SimulationServiceApplication class for AsyncTaskExecutor.
    private AsyncTaskExecutor taskExecutor;

    private Map<Long, LocationSimulatorInstance> taskFutures = new HashMap<>();

    @RequestMapping("/simulation")
    public List<LocationSimulatorInstance> simulation() {
        // 1. Load SimulatorInitLocations
        final SimulatorInitLocations fixture = this.pathService.loadSimulatorInitLocations();

        final List<LocationSimulatorInstance> instances = new ArrayList<>();

        for (GpsSimulatorRequest gpsSimulatorRequest : fixture.getGpsSimulatorRequests()) {
            // 2. Transform domain model simulator request to a class that can be executed by taskExecutor
            final LocationSimulator locationSimulator = gpsSimulatorFactoryService.prepareGpsSimulator(gpsSimulatorRequest);
            // 3. taskExecutor.submit(simulator);
            // 4. simulation starts
            final Future<?> future = taskExecutor.submit(locationSimulator);
            final LocationSimulatorInstance instance = new LocationSimulatorInstance(locationSimulator.getId(), locationSimulator, future);
            taskFutures.put(locationSimulator.getId(), instance);
            instances.add(instance);
        }

        return instances;
    }

    @RequestMapping("/cancel")
    public int cancel() {
        int numberOfCancelledTasks = 0;
        for (Map.Entry<Long, LocationSimulatorInstance> entry : taskFutures.entrySet()){
            LocationSimulatorInstance instance = entry.getValue();
            instance.getLocationSimulator().cancel();
            boolean wasCancelled = instance.getLocationSimulatorTask().cancel(true);
            if (wasCancelled) {
                numberOfCancelledTasks++;
            }
        }
        taskFutures.clear();
        return numberOfCancelledTasks;
    }
}