package demo;

import demo.domain.SupplyLocation;
import demo.domain.SupplyLocationRepository;
import demo.rest.SupplyLocationRestController;
import demo.service.SupplyLocationService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupplyLocationRestControllerTest {

    private SupplyLocationRepository repository;
    private SupplyLocationService service;
    private SupplyLocationRestController controller;
    private List<SupplyLocation> inputLocations;

    @Before
    public void setupMock() {
        repository = mock(SupplyLocationRepository.class);
        service = mock(SupplyLocationService.class);
        // no need to mock controller as it is what we want to test.
        controller = new SupplyLocationRestController(repository, service);
        // add test data
        inputLocations = new ArrayList<>();
        inputLocations.add(generateSupplyLocations(3, 3, "503"));
        inputLocations.add(generateSupplyLocations(4, 4, "504"));
        inputLocations.add(generateSupplyLocations(5, 5, "505"));
    }
    @Test
    public void whenListFiltered_expectSavedList() {
        List<SupplyLocation> locations = new ArrayList<>();
        locations.add(generateSupplyLocations(3, 3, "503"));
        when(service.saveSupplyLocationsZipContains503(inputLocations)).thenReturn(locations);

        assertThat(controller.uploadFitleredLocations(inputLocations).size()).isEqualTo(1);
        assertThat(controller.uploadFitleredLocations(inputLocations).get(0).getZip()).isEqualTo("503");
    }
    private SupplyLocation generateSupplyLocations(double latitude, double longitude, String zip) {
        SupplyLocation location = new SupplyLocation(latitude, longitude);
        location.setZip(zip);
        return location;
    }
}
