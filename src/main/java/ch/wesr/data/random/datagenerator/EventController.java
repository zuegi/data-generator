package ch.wesr.data.random.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    @Autowired
    EventGenerator eventGenerator;

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @GetMapping("/start")
    @ResponseBody
    public String start() {
        logger.debug("Starting EventGenerator");
        eventGenerator.setSomeCondition(true);
        return "started\n";
    }

    @GetMapping("/stop")
    @ResponseBody
    public String stop() {
        eventGenerator.setSomeCondition(false);
        return "stopped\n";
    }

    @GetMapping
    @ResponseBody
    public String status() {
        return eventGenerator.isSomeCondition() +"\n";
    }
}
