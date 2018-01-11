package com.github.jasper.kafka;

import kafka.utils.VerifiableProperties;

import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KafkaInfluxDBMetricsReporterTest {

    @Test
    public void initWithoutPropertiesSet() {
        KafkaInfluxDBMetricsReporter reporter = new KafkaInfluxDBMetricsReporter();
        reporter.init(new VerifiableProperties());
    }

    @Test
    public void initStartStopWithPropertiesSet() {
        KafkaInfluxDBMetricsReporter reporter = new KafkaInfluxDBMetricsReporter();
        Properties properties = new Properties();
        properties.setProperty("kafka.graphite.metrics.reporter.enabled", "true");

        reporter.init(new VerifiableProperties(properties));

        reporter.startReporter(1L);
        reporter.stopReporter();
    }


    @Test
    public void getMBeanName() {
        KafkaInfluxDBMetricsReporter reporter = new KafkaInfluxDBMetricsReporter();
        assertEquals("kafka:type=com.github.jasper.kafka.KafkaInfluxDBMetricsReporter", reporter.getMBeanName());

    }
}
