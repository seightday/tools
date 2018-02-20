import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * chrome打开开发者工具，模块手机打开以下网址
 * http://www.kfyx.cn/wx/test/
 * https://www.kfyx.cn/plugin.php?id=tpgao_m:m&mod=exam&item=80&formhash=f3defafc
 * 复制html到test/textName.txt
 */
public class ExamJson {

    public static void main(String... args) throws IOException {

        String[] abcdefg={"A","B","C","D","E","F","G"};

        String testName="[试卷][209康复治疗技术初级师]2017年专业实践能力模拟试题";
        File output = new File("C:/Users/Administrator/Desktop/output/"+testName+".txt");
        File outputAnswer = new File("C:/Users/Administrator/Desktop/output/"+testName+"-答案.txt");

        String encoding = "gbk";
        String fileToString = FileUtils.readFileToString(new File("C:/Users/Administrator/Desktop/test/" + testName + ".txt"), encoding);
        fileToString=fileToString.replaceAll("．",".");
        List<HashMap> mapList = JSON.parseArray(fileToString, HashMap.class);
        for (int i = 0; i < mapList.size(); i++) {
            HashMap hashMap = mapList.get(i);
            String ask = hashMap.get("ask").toString();
            FileUtils.write(output, ask +"\r\n", encoding,true);
            for (int j = 0; j < 7; j++) {
                Object o = hashMap.get("option" + (j + 1));
                if(o!=null && !o.toString().isEmpty()){
                    FileUtils.write(output,abcdefg[j]+":"+o.toString()+"\r\n", encoding,true);
                }
            }
            String[] split = ask.split("\\.");
            Object answer = hashMap.get("answer");
            FileUtils.write(outputAnswer,split[0]+":"+answer, encoding,true);
            Object answer_parser = hashMap.get("answer_parser");
            if(answer_parser!=null&&!answer_parser.toString().isEmpty()&&!answer_parser.toString().contains("暂无解析")){
                FileUtils.write(outputAnswer,"。解释："+answer_parser, encoding,true);
            }
            FileUtils.write(outputAnswer,"\r\n", encoding,true);
        }
    }
}
