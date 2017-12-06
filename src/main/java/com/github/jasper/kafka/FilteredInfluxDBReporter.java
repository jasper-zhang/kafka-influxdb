package com.github.jasper.kafka;

import com.yammer.metrics.core.*;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;

import static com.github.jasper.kafka.Dimension.*;

public class FilteredInfluxDBReporter extends InfluxDBReporter {

    private final EnumSet<Dimension> dimensions;
    private static final Logger LOGGER = LoggerFactory.getLogger(FilteredInfluxDBReporter.class);


    /**
     * Creates a new {@link InfluxDBReporter}.
     *
     * @param metricsRegistry the metrics registry
     * @param predicate       filters metrics to be reported
     * @param dimensions      enum of enabled dimensions to include
     * @param clock           a {@link Clock} instance
     * @throws IOException if there is an error connecting to the Graphite server
     */
    public FilteredInfluxDBReporter(MetricsRegistry metricsRegistry, String address, String database, String retentionPolicy, String username, String password, String consistency, String tags, MetricPredicate predicate, EnumSet<Dimension> dimensions, Clock clock) throws IOException {
        super(metricsRegistry, address, database,retentionPolicy,username, password, consistency, tags, predicate, clock);
        this.dimensions = dimensions;
        LOGGER.debug("The following Metrics Dimensions will be sent {}", dimensions);
    }

    @Override
    public void processMeter(MetricName metricName, Metered meter, Context context) throws Exception {

        Point.Builder pointbuilder = super.buildMetricsPoint(metricName, context);
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
