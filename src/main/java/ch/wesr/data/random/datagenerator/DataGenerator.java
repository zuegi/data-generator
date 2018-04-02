package ch.wesr.data.random.datagenerator;


import ch.wesr.data.random.datagenerator.config.SimulationConfig;
import ch.wesr.data.random.datagenerator.log.*;
import ch.wesr.data.random.datagenerator.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DataGenerator {

    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private SimulationRunner simRunner;

    private SimulationConfig simConfig;
    private List<Workflow> workflows;

    public DataGenerator() {
    }

    public void prepareEventLogger() {
        if (this.simConfig != null && this.workflows != null) {
            try {
                List<EventLogger> loggers = new ArrayList<>();
                for (Map<String, Object> elProps : simConfig.getProducers()) {
                    String elType = (String) elProps.get("type");
                    switch (elType) {
                        case "logger": {
                            log.info("Adding Log4JLogger Producer");
                            loggers.add(new Log4JLogger());
                            break;
                        }
                        case "file": {
                            log.info("Adding File Logger with properties: " + elProps);
                            loggers.add(new FileLogger(elProps));
                            break;
                        }
                        case "kafka": {
                            log.info("Adding Kafka Producer with properties: " + elProps);
                            loggers.add(new KafkaLogger(elProps));
                            break;
                        }
                        case "http-post": {
                            log.info("Adding HTTP Post Logger with properties: " + elProps);
                            try {
                                loggers.add(new HttpPostLogger(elProps));
                            } catch (NoSuchAlgorithmException ex) {
                                log.error("http-post Logger unable to initialize", ex);
                            }
                            break;
                        }
                    }
                }
                if (loggers.isEmpty()) {
                    throw new IllegalArgumentException("You must configure at least one Producer in the Simulation Config");
                }
                simRunner = new SimulationRunner(simConfig, workflows, loggers);
            } catch (IOException ex) {
                log.error("Error getting Simulation Config [ " + simConfig + " ]", ex);
            }
        } else {
            log.error("simConfig [ " + simConfig.toString() + " ] oder  workflowConfig [ " + workflows.toString() + " ] sind leer");
        }
    }


    public void startSimulation() {
        this.simRunner.startSimulation();
    }

    public void stopSimulation(){
        this.simRunner.stopSimulation();
    }

    public boolean isRunning() {
        return this.simRunner.isRunning();
    }
    
    public boolean setSimConfig(SimulationConfig simulationConfig) {
        if(this.simConfig == null) {
            this.simConfig = simulationConfig;
            this.prepareEventLogger();
            return true;
        } else {
            log.warn("Simulation Configuration alreday set as [ " +simulationConfig.getSimulationConfigName() +" ]. You probably should clear your configuration");
            return false;
        }
    }

    public void setWorkflowConfig(List<Workflow> workflows) {
        this.workflows = workflows;
    }

    public SimulationConfig getSimConfig() {
        return simConfig;
    }

    public boolean hasSimConfig() {
        if(simConfig == null) {
            return false;
        } else {
            return true;
        }
    }

    public void clearSimulationConfig() {
        this.simConfig = null;
    }
}
