package net.paissad.tools.reqcoco.webapp;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReqApplicationConstants {

    private static final Logger LOGGER                    = LoggerFactory.getLogger(ReqApplicationConstants.class);

    public static final String  APPLICATION_NAME          = "ReqCoCo";

    public static final String  APPLICATION_DESCRIPTION   = "Requirement Code Coverage";

    public static final String  APPLICATION_URL           = "https://paissad.github.io/reqcoco";

    public static final String  APPLICATION_CONTACT_NAME  = "reqcoco";

    public static final String  APPLICATION_CONTACT_EMAIL = "reqcoco@gmail.com";

    public static final String  TERM_OF_SERVICE_URL       = "";

    public static final String  LICENSE                   = "GNU Lesser General Public License v3.0";

    public static final String  LICENSE_URL               = "https://github.com/paissad/reqcoco/blob/master/LICENSE";

    private ReqApplicationConstants() {
    }

    public static String getAppVersion() {
        try {
            final Properties pomProperties = new Properties();
            pomProperties.load(ReqApplicationConstants.class.getResourceAsStream("/META-INF/maven/net.paissad.tools.reqcoco/reqcoco-webapp/pom.properties"));
            return pomProperties.getProperty("version");
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve the version of the web application : {}", e.getMessage());
            return "_UNKNOWN_";
        }
    }

}
