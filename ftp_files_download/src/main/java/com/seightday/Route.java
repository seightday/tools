package com.seightday;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

/**
 * Created by Administrator on 2017/3/2.
 */
@Slf4j
public class Route extends RouteBuilder {

    public String getSftpServer() {
        return sftpServer;
    }

    public void setSftpServer(String sftpServer) {
        this.sftpServer = sftpServer;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    private String sftpServer;
    private String downloadLocation;

    public Route(){
    }
//    public Route(String sftpServer,String downloadLocation){
//        this.sftpServer=sftpServer;
//        this.downloadLocation=downloadLocation;
//    }

    @Override
    public void configure() throws Exception {
        from( sftpServer ).to(  downloadLocation ).log(LoggingLevel.INFO, log, "Downloaded file ${file:name} complete to "+downloadLocation);
    }

}