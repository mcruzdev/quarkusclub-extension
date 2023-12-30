package dev.matheuscruz.quarkusclub.extension.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "quarkusclub", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class QuarkusClubConfig {

    /**
     * Defines log prefix
     */
    @ConfigItem(name = "prefix", defaultValue = "quarkus-club-prefix: ")
    public String prefix;

}
