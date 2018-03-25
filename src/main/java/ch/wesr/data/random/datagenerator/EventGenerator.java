package ch.wesr.data.random.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * https://stackoverflow.com/questions/39737013/spring-boot-best-way-to-start-a-background-thread-on-deployment
 */

@Component
public class EventGenerator implements DisposableBean, Runnable {

    private Thread thread;

    private static final Logger logger = LoggerFactory.getLogger(EventGenerator.class);

    private volatile boolean someCondition;

    EventGenerator(){
        this.thread = new Thread(this);
    }

    @Override
    public void run(){
        while(someCondition){

            try {
                logger.debug("got it");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy(){
        someCondition = false;
    }

    public boolean isSomeCondition() {
        return someCondition;
    }

    public void setSomeCondition(boolean someCondition) {
        this.someCondition = someCondition;
    }
}
