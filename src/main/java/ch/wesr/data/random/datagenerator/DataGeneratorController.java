package ch.wesr.data.random.datagenerator;

import ch.wesr.data.random.datagenerator.config.SimulationConfig;
import ch.wesr.data.random.datagenerator.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DataGeneratorController {

    public static final int LOCKED = 423; // Die angeforderte Ressource ist zurzeit gesperrt.[8]
    public static final String SET_SIM_CONFIG = "/setSimConfig";
    public static final String GET_SIM_CONFIG = "/getSimConfig";
    public static final String CLEAR_SIM_CONFIG = "/clearSimConfig";
    public static final String SET_WORKFLOW_CONFIG = "/setWorkflowConfig";
    public static final String START = "/start";
    public static final String STOP = "/stop";
    public static final String STATUS = "/status";

    @Autowired
    private DataGenerator dataGenerator;

    private static final Logger logger = LoggerFactory.getLogger(DataGeneratorController.class);

    @PostMapping(SET_SIM_CONFIG)
    @ResponseBody
    public ResponseEntity<String> setSimConfig(@RequestBody SimulationConfig simulationConfig) {
        // because we only want ony simulation config otherwise clear it
        if(dataGenerator.hasSimConfig() ) {
            return ResponseEntity.status(LOCKED).body("There is already a Simulation Config with the name [ " +dataGenerator.getSimConfig().getSimulationConfigName() + " ] in place. Probably you should clear the configuration");
        } else {
            dataGenerator.setSimConfig(simulationConfig);
            return ResponseEntity.ok(this.dataGenerator.getClass().getSimpleName() +" simConfig recieved\n");
        }
    }

    @GetMapping(GET_SIM_CONFIG)
    @ResponseBody
    public ResponseEntity<SimulationConfig> getSimConfig() {
        SimulationConfig simConfig = dataGenerator.getSimConfig();
        return ResponseEntity.ok(simConfig);
    }


    @PostMapping(CLEAR_SIM_CONFIG)
    @ResponseBody
    public ResponseEntity<String> clearSimConfig() {
        if(dataGenerator.isRunning()) {
            return ResponseEntity.ok("There is a Simulation [ " +this.dataGenerator.getSimConfig().getSimulationConfigName() + " ] running. Please stop it first");
        } else {
            dataGenerator.clearSimulationConfig();
            return ResponseEntity.ok("Simulation Config for [ " +this.dataGenerator.getSimConfig().getSimulationConfigName() + " ] has been cleared\n");
        }
    }


    @PostMapping(SET_WORKFLOW_CONFIG)
    @ResponseBody
    public ResponseEntity<String> setWorkflowConfig(@RequestBody List<Workflow> workflows) {
        logger.debug("Starting DataGenerator: " +workflows);
        dataGenerator.setWorkflowConfig(workflows);
        return ResponseEntity.ok(this.dataGenerator.getClass().getSimpleName() +" List of workflowConfig recieved\n");
    }

    @GetMapping(START)
    @ResponseBody
    public ResponseEntity<String> start() {
        logger.debug("Starting DataGenerator");
        this.dataGenerator.startSimulation();
        return ResponseEntity.ok(this.dataGenerator.getSimConfig().getSimulationConfigName() +" started\n");
    }

    @GetMapping(STOP)
    @ResponseBody
    public ResponseEntity<String> stop() {
        logger.debug("Stopping EventGenerator");
        this.dataGenerator.stopSimulation();
        return ResponseEntity.ok(this.dataGenerator.getSimConfig().getSimulationConfigName() +" stopped\n");
    }

    @GetMapping(STATUS)
    @ResponseBody
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Simulation [ " +dataGenerator.getSimConfig().getSimulationConfigName() +" ] is running [ " +dataGenerator.isRunning() +" ]\n");
    }
}