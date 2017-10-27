B2C Accelerator with B2C Checkout, Liveedit Addons and ECP-> Data hub -> YaaS


For debug and log information, please create the logback.xml with example contain below, then add it into the datahub-tomcat/lib directory 

<configuration>

	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<!-- encoders are assigned the type
			 ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
		<encoder>
			<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
		</encoder>
	</appender>

    <logger name="com.hybris" level="info" />
    <logger name="com.hybris.datahub.y2yaassync" level="info" />
    <logger name="com.hybris.ycts" level="info" />
    <logger name="com.hybris" level="info" />
    <!-- for low level http interactions details set 
        io.netty.handler.logging.LoggingHandler to debug -->
    <logger name="io.netty.handler.logging.LoggingHandler" level="info" />
	<root level="OFF">
		<appender-ref ref="STDOUT" />
	</root>
</configuration>
