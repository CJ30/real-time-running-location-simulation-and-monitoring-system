package demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;

import java.io.IOException;

@EnableBinding(Sink.class)
@Slf4j
public class RunningLocationUpdaterSink {

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void updateLocation(String input) throws IOException {
        log.info("Location input in updater: " + input);
    }
}
