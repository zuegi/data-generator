/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.wesr.data.random.datagenerator;

import ch.wesr.data.random.datagenerator.config.JSONConfigReader;
import ch.wesr.data.random.datagenerator.config.SimulationConfig;
import ch.wesr.data.random.datagenerator.config.WorkflowConfig;
import ch.wesr.data.random.datagenerator.log.EventLogger;
import ch.wesr.data.random.datagenerator.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrewserff
 */
public class SimulationRunner {

    private static final Logger log = LoggerFactory.getLogger(SimulationRunner.class);
    private SimulationConfig simulationConfig;
    private List<Workflow> workflows;
    private List<EventGenerator> eventGenerators;
    private List<Thread> eventGenThreads;
    private boolean running;
    private List<EventLogger> eventLoggers;

    public SimulationRunner(SimulationConfig simulationConfig, List<Workflow> workflows, List<EventLogger> loggers) {
        this.simulationConfig = simulationConfig;
        this.workflows = workflows;
        this.eventLoggers = loggers;
        eventGenerators = new ArrayList<EventGenerator>();
        eventGenThreads = new ArrayList<Thread>();
        
        setupSimulation();
    }

    private void setupSimulation() {
        running = false;
        for (Workflow workflow: this.getWorkflows()) {
//            try {
//                Workflow w = JSONConfigReader.readConfig(this.getClass().getClassLoader().getResourceAsStream(workflowConfig.getWorkflowFilename()), Workflow.class);
//                final EventGenerator gen = new EventGenerator(w, workflowConfig.getWorkflowName(), eventLoggers);
                final EventGenerator gen = new EventGenerator(workflow, "gaga", eventLoggers);
//                log.info("Adding EventGenerator for [ " + workflowConfig.getWorkflowName()+ "," + workflowConfig.getWorkflowFilename()+ " ]");
                eventGenerators.add(gen);
                eventGenThreads.add(new Thread(gen));
//            } catch (IOException ex) {
//                log.error("Error reading config: " + "gaga", ex);
//            }
        }
    }

    public void startSimulation() {
        log.info("Starting Simulation");
               
        if (eventGenThreads.size() > 0) {
            for (Thread t : eventGenThreads) {
                t.start();
            }
            running = true;
        }
    }

    public void stopSimulation() {
        log.info("Stopping Simulation");
        for (Thread t : eventGenThreads) {
            t.interrupt();
        }
        for (EventLogger l : eventLoggers) {
            l.shutdown();
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }


    public List<Workflow> getWorkflows() {
        return workflows;
    }

}
