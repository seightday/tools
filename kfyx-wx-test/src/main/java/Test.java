import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * chrome打开开发者工具，模块手机打开以下网址
 * http://www.kfyx.cn/wx/test/
 * https://www.kfyx.cn/plugin.php?id=tpgao_m:m&mod=exam&item=80&formhash=f3defafc
 * 复制html到test/textName.txt
 */
public class Test {

    public static void main(String... args) throws IOException {

        String testName="[试卷][107康复治疗技术初级士]2017年专业知识真题";
        File output = new File("C:/Users/Administrator/Desktop/output/"+testName+".txt");
        Document document = Jsoup.parse(new File("C:/Users/Administrator/Desktop/test/"+testName+".txt"), "utf-8");
        Element nobox = document.getElementById("nobox");
        Elements tds = nobox.getElementsByTag("td");
        System.out.println(tds);
        tds.forEach(td->{
            String subject = td.attr("subject");
            String data = td.attr("data");
            try {
                FileUtils.write(output,subject,"utf-8",true);
                FileUtils.write(output,"\r\n","utf-8",true);
                FileUtils.write(output,data,"utf-8",true);
                FileUtils.write(output,"\r\n","utf-8",true);
                FileUtils.write(output,"\r\n","utf-8",true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
