package ch.wesr.data.random.datagenerator;

import ch.wesr.data.random.datagenerator.config.SimulationConfig;
import ch.wesr.data.random.datagenerator.config.WorkflowConfig;
import ch.wesr.data.random.datagenerator.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DateGeneratorController {

    @Autowired
    private DataGenerator dataGenerator;

    private static final Logger logger = LoggerFactory.getLogger(DateGeneratorController.class);

    @RequestMapping(value = "/setSimConfig", method = RequestMethod.POST)
    @ResponseBody
    public String setSimConfig(@RequestBody SimulationConfig simulationConfig) {
        logger.debug("Starting DataGenerator: " +simulationConfig);
        dataGenerator.setSimConfig(simulationConfig);
//        dataGenerator.setSimulationConfig(simulationConfig);
        return  this.dataGenerator.getClass().getSimpleName() +" simConfig recieved\n";
    }

    @RequestMapping(value = "/setWorkflowConfig", method = RequestMethod.POST)
    @ResponseBody
    public String setWorkflowConfig(@RequestBody List<Workflow> workflows) {
        logger.debug("Starting DataGenerator: " +workflows);
        dataGenerator.setWorkflowConfig(workflows);
        return  this.dataGenerator.getClass().getSimpleName() +" List of workflowConfig recieved\n";
    }

    @GetMapping("/start")
    @ResponseBody
    public String start() {
        logger.debug("Starting DataGenerator");
        this.dataGenerator.startGeneratingData();
        this.dataGenerator.startSimulation();
        return this.dataGenerator.getClass().getSimpleName() +" stopped\n";
    }

    @GetMapping("/stop")
    @ResponseBody
    public String stop() {
        logger.debug("Stopping EventGenerator");
        this.dataGenerator.stopSimulation();
        return this.dataGenerator.getClass().getSimpleName() +" stopped\n";
    }

    @GetMapping
    @ResponseBody
    public String status() {
        return dataGenerator +"\n";
    }
}
