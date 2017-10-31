package com.seightday;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/3/2.
 */
//@Component
@Slf4j
public class DownloadRoute extends RouteBuilder {

    @Value("${ftp.server.info}")
    private String sftpServer;
    @Value("${ftp.local.dir}")
    private String downloadLocation;

    @Override
    public void configure() throws Exception {
        from( sftpServer ).to(  downloadLocation ).log(LoggingLevel.DEBUG, log, "Downloaded file ${file:name} complete.");
    }

}