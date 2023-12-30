package dev.matheuscruz.quarkusclub.extension.runtime;

import jakarta.inject.Singleton;

@Singleton
public class SimpleMessager {

    private final QuarkusClubConfig config;

    public SimpleMessager(QuarkusClubConfig config) {
        this.config = config;
    }

    public void send() {
        System.out.println(this.config.prefix + "Sending message using SimpleMessager");
    }

}
