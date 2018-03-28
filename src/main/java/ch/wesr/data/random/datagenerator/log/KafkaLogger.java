/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.wesr.data.random.datagenerator.log;

import ch.wesr.data.random.datagenerator.util.JsonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author andrewserff
 */
public class KafkaLogger implements EventLogger {

    private static final Logger log = LoggerFactory.getLogger(KafkaLogger.class);
    public static final String BROKER_SERVER_PROP_NAME = "broker.server";
    public static final String BROKER_PORT_PROP_NAME = "broker.port";
    
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final boolean sync;
    private final boolean flatten;
    private final Properties props = new Properties();
    private JsonUtils jsonUtils;
    
    public KafkaLogger(Map<String, Object> props) {
        String brokerHost = (String) props.get(BROKER_SERVER_PROP_NAME);
        Integer brokerPort = (Integer) props.get(BROKER_PORT_PROP_NAME);
        
        this.props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerHost + ":" + brokerPort.toString());
        this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        
        producer = new KafkaProducer<>(this.props);
        
        this.topic = (String) props.get("topic");
        if (props.get("sync") != null) {
            this.sync = (Boolean) props.get("sync");
        } else {
            this.sync = false;
        }
        
        if (props.get("flatten") != null) {
            this.flatten = (Boolean) props.get("flatten");
        } else {
            this.flatten = false;
        }
        
        this.jsonUtils = new JsonUtils();
    }

    @Override
    public void logEvent(String event, Map<String, Object> producerConfig) {
        logEvent(event);
    }
    
    private void logEvent(String event) {
        boolean sync = false;
        
        String output = event;
        if (flatten) {
            try {
                output = jsonUtils.flattenJson(event);
            } catch (IOException ex) {
                log.error("Error flattening json. Unable to send event [ " + event + " ]", ex);
                return;
            }
        }
        
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, output);
        if (sync) {
            try {
                producer.send(producerRecord).get();
            } catch (InterruptedException | ExecutionException ex) {
                //got interrupted while waiting
                log.warn("Thread interrupted while waiting for synchronous response from producer", ex);
            }
        } else {
            log.debug("Sending event to Kafka: [ " + output + " ]");
            producer.send(producerRecord);
        }
    }

    @Override
    public void shutdown() {
        producer.close();
    }

}
