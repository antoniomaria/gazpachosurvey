package net.sf.gazpachoquest.questionnaires.bootstrap;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class ConfigurationFactory {

    private Properties properties;

    public void listen(@Observes
    StartUpEvent event) {
        properties = event.getProperties();
    }

    @InjectedConfiguration
    @Produces
    public String injectConfiguration(InjectionPoint ip) throws IllegalStateException {
        InjectedConfiguration param = ip.getAnnotated().getAnnotation(InjectedConfiguration.class);
        String resourceKey = param.key().getKey();
        return properties.getProperty(resourceKey);
    }
}
