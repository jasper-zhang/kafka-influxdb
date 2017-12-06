package com.github.jasper.kafka;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Clock;
import com.yammer.metrics.core.MetricPredicate;
import java.util.EnumSet;

import kafka.metrics.KafkaMetricsConfig;
import kafka.metrics.KafkaMetricsReporter;
import kafka.utils.VerifiableProperties;

public class KafkaInfluxDBMetricsReporter implements KafkaMetricsReporter, KafkaInfluxDBMetricsReporterMBean {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaInfluxDBMetricsReporter.class);

    private static final String INFLUXDB_DEFAULT_ADDRESS = "localhost";
    private static final String INFLUXDB_DEFAULT_USERNAME = "root";
    private static final String INFLUXDB_DEFAULT_PASSWORD = "root";
    private static final String INFLUXDB_DEFAULT_CONSISTENCY = "all";
    private static final String INFLUXDB_DEFAULT_DATABASE = "kafka";
    private static final String INFLUXDB_DEFAULT_RETENTIONPOLICY = "autogen";
    private static final String INFLUXDB_DEFAULT_TAGS = "hostname:"+HostUtils.getHostName();


    private boolean initialized = false;
    private boolean running = false;
    private FilteredInfluxDBReporter reporter = null;
    private String influxDBAddress = INFLUXDB_DEFAULT_ADDRESS;
    private String influxDBUsername = INFLUXDB_DEFAULT_USERNAME;
    private String influxDBPassword = INFLUXDB_DEFAULT_PASSWORD;
    private String influxDBConsistency = INFLUXDB_DEFAULT_CONSISTENCY;
    private String influxDBDatabase = INFLUXDB_DEFAULT_DATABASE;
    private String influxDBRetentionPolicy = INFLUXDB_DEFAULT_RETENTIONPOLICY;
    private String influxDBTags = INFLUXDB_DEFAULT_TAGS;


    private MetricPredicate metricPredicate = new FilterMetricPredicate();
    private EnumSet<Dimension> metricDimensions;

    @Override
    public String getMBeanName() {
        return "kafka:type=" + KafkaInfluxDBMetricsReporter.class.getName();
    }

    @Override
    public synchronized void startReporter(long pollingPeriodSecs) {
        if (initialized && !running) {
            reporter.start(pollingPeriodSecs, TimeUnit.SECONDS);
            running = true;
            LOG.info("Started Kafka Graphite metrics reporter with polling period {} seconds", pollingPeriodSecs);
        }
    }

    @Override
    public synchronized void stopReporter() {
        if (initialized && running) {
            reporter.shutdown();
            running = false;
            LOG.info("Stopped Kafka InfluxDB metrics reporter");
            reporter = buildInfluxDBReporter();
        }
    }

    @Override
    public synchronized void init(VerifiableProperties props) {
        if (!initialized) {
            KafkaMetricsConfig metricsConfig = new KafkaMetricsConfig(props);
            influxDBAddress = props.getString("kafka.influxdb.metrics.address", INFLUXDB_DEFAULT_ADDRESS);
            influxDBUsername = props.getString("kafka.influxdb.metrics.username", INFLUXDB_DEFAULT_USERNAME);
            influxDBPassword = props.getString("kafka.influxdb.metrics.password", INFLUXDB_DEFAULT_PASSWORD);
            influxDBConsistency = props.getString("kafka.influxdb.metrics.consistency", INFLUXDB_DEFAULT_CONSISTENCY);
            influxDBDatabase = props.getString("kafka.influxdb.metrics.database", INFLUXDB_DEFAULT_DATABASE);
            influxDBRetentionPolicy = props.getString("kafka.influxdb.metrics.retentionPolicy", INFLUXDB_DEFAULT_RETENTIONPOLICY);
            influxDBTags = props.getString("kafka.influxdb.metrics.tags", INFLUXDB_DEFAULT_TAGS);
            String excludeRegex = props.getString("kafka.influxdb.metrics.exclude.regex", null);
            metricDimensions = Dimension.fromProperties(props.props(), "kafka.influxdb.dimension.enabled.");
    
            LOG.debug("Initialize InfluxDBReporter [{},{},{}]", influxDBAddress, influxDBDatabase, influxDBRetentionPolicy);

            if (excludeRegex != null) {
                LOG.debug("Using regex [{}] for InfluxDBReporter", excludeRegex);
                metricPredicate = new FilterMetricPredicate(excludeRegex);
            }
            reporter = buildInfluxDBReporter();

            if (props.getBoolean("kafka.influxdb.metrics.reporter.enabled", false)) {
                initialized = true;
                startReporter(metricsConfig.pollingIntervalSecs());
                LOG.debug("InfluxDBReporter started.");
            }
        }
    }


    private FilteredInfluxDBReporter buildInfluxDBReporter() {
        FilteredInfluxDBReporter influxDBReporter = null;
        try {
            influxDBReporter = new FilteredInfluxDBReporter(
                    Metrics.defaultRegistry(),
                    influxDBAddress,
                    influxDBDatabase,
                    influxDBRetentionPolicy,
                    influxDBUsername,
                    influxDBPassword,
                    influxDBConsistency,
                    influxDBTags,
                    metricPredicate,
                    metricDimensions,
                    Clock.defaultClock()
            );
        } catch (IOException e) {
            LOG.error("Unable to initialize InfluxDBReporter", e);
        }
        return influxDBReporter;
    }
}
