import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender

import static ch.qos.logback.classic.Level.INFO

appender("Console", ConsoleAppender) {
  target = "System.out"
  encoder(PatternLayoutEncoder) {
    pattern = "%d{ HH:mm:ss SSS} %p [%t]:%m%n"
  }
  filter(ThresholdFilter) {
    level = DEBUG
  }
}
appender("File", RollingFileAppender) {
  file = "d:/logs/notification.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss SSS} %p [%t]:%m%n"
  }
  filter(ThresholdFilter) {
    level = DEBUG
  }
}
root(DEBUG, ["Console", "File"])