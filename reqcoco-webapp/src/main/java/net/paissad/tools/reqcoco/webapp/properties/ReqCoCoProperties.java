package net.paissad.tools.reqcoco.webapp.properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = ReqCoCoProperties.REQCOCO_PREFIX, ignoreUnknownFields = false)
@Getter
@Setter
public class ReqCoCoProperties {

    public static final String REQCOCO_PREFIX       = "reqcoco";

    public static final int    REQCOCO_DEFAULT_PORT = 8531;

    private boolean            banner               = true;

    @Autowired
    private Server             server;

    @Component
    @Getter
    @Setter
    public static class Server {

        @Min(-1)
        @Max(65535)
        private int port = REQCOCO_DEFAULT_PORT;
    }

}
