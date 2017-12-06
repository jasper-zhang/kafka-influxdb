package com.github.jasper.kafka;

import java.util.EnumSet;
import java.util.Properties;

/**
 *
 */
public enum Dimension {    //use name itself as suffix
  count("count"),
  meanRate("meanRate"),
  rate1m("1MinuteRate"),
  rate5m("5MinuteRate"),
  rate15m("15MinuteRate"),
  min("min"),
  max("max"),
  mean("mean"),
  stddev("stddev"),
  sum("sum"),
  median("median"),
  p75("p75"),
  p95("p95"),
  p98("p98"),
  p99("p99"),
  p999("p999");

  final String displayName;

  public String getDisplayName() {
    return displayName;
  }

  Dimension(String defaultValue) {
    this.displayName = defaultValue;
  }

  public static EnumSet<Dimension> fromProperties(Properties p, String prefix) {
    EnumSet<Dimension> df = EnumSet.allOf(Dimension.class);
    for (Dimension k : Dimension.values()) {
      String key = prefix + k.toString();
      if (p.containsKey(key)) {
        Boolean value = Boolean.parseBoolean(p.getProperty(key));
        if (!value) {
          df.remove(k);
        }
      }
    }
    return df;
  }

}