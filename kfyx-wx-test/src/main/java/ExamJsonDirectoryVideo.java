import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * chrome打开开发者工具，模块手机打开以下网址
 * http://www.kfyx.cn/wx/test/
 * https://www.kfyx.cn/plugin.php?id=tpgao_m:m
 * 复制html到test/textName.txt
 */
public class ExamJsonDirectoryVideo {

    public static void main(String... args) throws Exception {

        String parentDir="C:/Users/Administrator/Desktop/kfyx/【视频版2017年真题】限时免费(8套)";
        Collection<File> files = FileUtils.listFiles(new File(parentDir), new String[]{"txt"}, true);
        System.out.println("files length "+files.size());

        files.forEach( file -> {

            System.out.println(file.getName());

            if(exclude(file)){
                System.out.println("exclude");
                return;
            }

            try {
                dealTest(file.getName().replace(".txt",""),file.getParent()+"\\");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    public static boolean exclude(File file){
        String name = file.getName();
        String[] keys=new String[]{"anki","答案","题目"};
        return Arrays.asList(keys).stream().anyMatch(name::contains);
    }

    public static void dealTest(String testName,String dir) throws Exception {
        System.out.println("testName is "+testName+", dir is "+dir);

        new File(dir+testName).mkdir();


//        String testName="[试卷]S17Z348康复医学中级2017年专业知识模拟试题";
//        String dir = "D:/微云同步助手/674114624/kfyx/【康复医学中级348】2017年模拟试题(4套)/";

        String encoding = "gbk";
        String fileToString = FileUtils.readFileToString(new File(dir + testName + ".txt"), encoding);
        fileToString=fileToString.replaceAll("．",".");
        List<HashMap> mapList = JSON.parseArray(fileToString, HashMap.class);
        for (int i = 0; i < mapList.size(); i++) {
            HashMap hashMap = mapList.get(i);


            String ask = hashMap.get("ask").toString();
            String video = hashMap.get("video").toString();

            Connection.Response response;
            try {
                response = Jsoup.connect(video).timeout(10*60*1000).ignoreContentType(true).maxBodySize(100 * 1024 * 1024)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            File videoFile = new File(dir + testName + "\\" + ask + ".mp4");
            if(videoFile.exists()){
                continue;
            }
            FileUtils.writeByteArrayToFile(videoFile,response.bodyAsBytes());
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
