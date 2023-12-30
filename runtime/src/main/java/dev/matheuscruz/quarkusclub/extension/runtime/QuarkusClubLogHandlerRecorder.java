package dev.matheuscruz.quarkusclub.extension.runtime;

import java.util.Optional;
import java.util.logging.Handler;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class QuarkusClubLogHandlerRecorder {

    public RuntimeValue<Optional<Handler>> logHandler() {

        return new RuntimeValue<>(Optional.of(new QuarkusClubLogHandler()));

    }
}
