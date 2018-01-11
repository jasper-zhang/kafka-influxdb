Kafka InfluxDB Metrics Reporter
==============================

Install On Broker
------------

1. Build the `kafka-influxdb-*.jar` jar using `mvn package` or download it from the releases.
2. Add `kafka-influxdb-*.jar` to the `libs/` directory of your kafka broker installation
3. Some dependencies also need be added to `libs/`, they includes: `converter-moshi-*.jar`, `influxdb-java-*.jar`, `logging-interceptor-*.jar`, `moshi-*.jar`,`okhttp-*.jar`, `okio-*.jar`,`retrofit-*.jar`
4. Configure the broker (see the configuration section below)
5. Restart the broker

You can also download from the release, unzip the package and add them to the `libs/`, instead of do the 1,2,3 above.

Configuration
------------

Edit the `server.properties` file of your installation, activate the reporter by setting:

    kafka.metrics.reporters=com.github.jasper.kafka.KafkaInfluxDBMetricsReporter
    kafka.influxdb.metrics.reporter.enabled=true

Here is a list of default properties used:
    
    kafka.metrics.polling.interval.secs=10
    kafka.influxdb.metrics.address=http://localhost:8086
    kafka.influxdb.metrics.database=kafka
    kafka.influxdb.metrics.retentionPolicy=autogen
    kafka.influxdb.metrics.username=root
    kafka.influxdb.metrics.password=root
    kafka.influxdb.metrics.consistency=all
    kafka.influxdb.metrics.tags=hostname:brokerhostname

The `kafka.influxdb.metrics.tags` must format as `key1:value1,key2:value2,...`.
    