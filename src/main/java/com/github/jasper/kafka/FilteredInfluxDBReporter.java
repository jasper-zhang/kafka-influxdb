package com.github.jasper.kafka;

import com.yammer.metrics.core.*;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static com.github.jasper.kafka.Dimension.*;

public class FilteredInfluxDBReporter extends InfluxDBReporter {

    private final EnumSet<Dimension> dimensions;
    private static final Logger LOGGER = LoggerFactory.getLogger(FilteredInfluxDBReporter.class);


    /**
     * Creates a new {@link InfluxDBReporter}.
     *
     * @param metricsRegistry the metrics registry
     * @param address the address of InfluxDB
     * @param database the database to store the metric
     * @param retentionPolicy  the retentionPolicy to store the metric
     * @param username  the username to write into InfluxDB
     * @param password  the password to write into InfluxDB
     * @param consistency  consistency of write into InfluxDB, available value: one, any, all, quorum
     * @param tags custom tags
     * @param dimensions      enum of enabled dimensions to include
     * @param clock           a {@link Clock} instance
     */
    public FilteredInfluxDBReporter(MetricsRegistry metricsRegistry, String address, String database, String retentionPolicy, String username, String password, String consistency, String tags, EnumSet<Dimension> dimensions, Clock clock, VirtualMachineMetrics vm) {
        super(metricsRegistry, address, database,retentionPolicy,username, password, consistency, tags, clock, vm);
        this.dimensions = dimensions;
        LOGGER.debug("The following Metrics Dimensions will be sent {}", dimensions);
    }

    @Override
    public void processMeter(MetricName metricName, Metered meter, Context context) throws Exception {

        Point.Builder pointbuilder = super.buildMetricsPointByMetricName(metricName, context);
        pointbuilder.tag("metric_type", "meter");
        pointbuilder.tag("eventType", meter.eventType());

        if (dimensions.contains(count))
        pointbuilder.addField("count", meter.count());
        if (dimensions.contains(meanRate))
        pointbuilder.addField("meanRate", meter.meanRate());
        if (dimensions.contains(rate1m))
        pointbuilder.addField("1MinuteRate", meter.oneMinuteRate());
        if (dimensions.contains(rate5m))
        pointbuilder.addField("5MinuteRate", meter.fiveMinuteRate());
        if (dimensions.contains(rate15m))
        pointbuilder.addField("15MinuteRate", meter.fifteenMinuteRate());

        addPoint(pointbuilder.build());
    }
}
