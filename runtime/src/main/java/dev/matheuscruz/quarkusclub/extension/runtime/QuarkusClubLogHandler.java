package dev.matheuscruz.quarkusclub.extension.runtime;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class QuarkusClubLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        String message = String.format(record.getMessage(), record.getParameters());
        System.out.println("[Quarkus Club] " + message);
    }

    @Override
    public void flush() {
        
    }

    @Override
    public void close() throws SecurityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

}