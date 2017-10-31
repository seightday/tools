package com.seightday;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/3.
 */
//@Component("donwloadFilter")
@Slf4j
public class DownloadFilter<T> implements GenericFileFilter<T> {

    @Value("${local.dir}")
    private String localDir;
    @Value("${days}")
    private int days;
    @Override
    public boolean accept(GenericFile<T> file) {
        long lastModified = file.getLastModified();
        Date date = new Date(lastModified);
        log.info("date is {}",date);

        long currentTimeMillis = System.currentTimeMillis();
        long l = currentTimeMillis - lastModified;
        long i = days * 24 * 60 * 60 * 1000L;
        if(l > i){
            log.info(days+"天外");
            return false;
        }

        if(!file.getFileName().endsWith("gz")){
            log.info("非gz文件");
            return false;
        }

        String p=localDir+"/"+file.getFileName();
        boolean existed= new File(p).exists();
        log.info("file path is {}, existed is {}",p,existed);
        return !existed;
    }
}
