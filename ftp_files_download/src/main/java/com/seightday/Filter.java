package com.seightday;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/3.
 */
@Slf4j
public class Filter<T> implements GenericFileFilter<T> {

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    private String localDir;
    private int days;

    public Filter(){
    }

    public Filter(String localDir,int days){
        this.localDir=localDir;
        this.days=days;
    }

    /**
     * @see org.apache.camel.component.file.GenericFileFilter#accept(org.apache.camel.component.file.GenericFile<T> )
     */
    @Override
    public boolean accept(GenericFile<T> file) {
//        String fileName = file.getFileName();
//        String[] names=new String[]{"0221","0222"};
//        for (String name:names
//             ) {
//            if(fileName.contains(name)){
//                return true;
//            }
//        }
//        return false;


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
