package com.github.jasper.kafka;

import java.util.NoSuchElementException;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricProcessor;
import com.yammer.metrics.core.Timer;

/**
 *  Tries to get the value of a {@code Gauge} and wraps any {@code NoSuchElementException}
 *  into an {@code InvalidGaugeException}.
 *
 *  Reason for this is https://issues.apache.org/jira/browse/KAFKA-1866
 */
public class ValidatingMetricProcessor implements MetricProcessor {

    @Override
    public void processMeter(MetricName name, Metered meter, Object context) throws Exception {

    }

    @Override
    public void processCounter(MetricName name, Counter counter, Object context) throws Exception {

    }

    @Override
    public void processHistogram(MetricName name, Histogram histogram, Object context) throws Exception {

    }

    @Override
    public void processTimer(MetricName name, Timer timer, Object context) throws Exception {

    }

    @Override
    public void processGauge(MetricName name, Gauge gauge, Object context) throws Exception {
        try {
            gauge.value();
        } catch (NoSuchElementException ex) {
            throw new InvalidGaugeException(String.format("%s.%s.%s", name.getGroup(), name.getType(), name.getName()), ex);
        }
    }
}
