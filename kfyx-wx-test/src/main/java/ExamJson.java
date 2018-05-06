import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * chrome打开开发者工具，模块手机打开以下网址
 * http://www.kfyx.cn/wx/test/
 * https://www.kfyx.cn/plugin.php?id=tpgao_m:m
 * 复制html到test/textName.txt
 */
public class ExamJson {

    public static void main(String... args) throws IOException {

        String[] abcdefg={"A","B","C","D","E","F","G"};

        String testName="[试卷]S17Z348康复医学中级2017年专业知识模拟试题";
        String dir = "D:/微云同步助手/674114624/kfyx/【康复医学中级348】2017年模拟试题(4套)/";
        File outputAsk = new File(dir +testName+"-题目.txt");
        if(outputAsk.exists()){
            outputAsk.delete();
        }
        File outputAnswer = new File(dir +testName+"-答案.txt");
        if(outputAnswer.exists()){
            outputAnswer.delete();
        }
        File outputAskAnswer = new File(dir +testName+"-题目+答案.txt");
        if(outputAskAnswer.exists()){
            outputAskAnswer.delete();
        }
        File outputAnki = new File(dir +testName+"-anki.txt");
        if(outputAnki.exists()){
            outputAnki.delete();
        }

        String encoding = "gbk";
        String fileToString = FileUtils.readFileToString(new File(dir + testName + ".txt"), encoding);
        fileToString=fileToString.replaceAll("．",".");
        List<HashMap> mapList = JSON.parseArray(fileToString, HashMap.class);
        for (int i = 0; i < mapList.size(); i++) {
            HashMap hashMap = mapList.get(i);

            String askAndAnswer="";
            String anki="";

            String ask = hashMap.get("ask").toString();
            FileUtils.write(outputAsk, ask +"\r\n", encoding,true);
            askAndAnswer+=ask +"\r\n";
            anki+=ask+"<br>";
            for (int j = 0; j < 7; j++) {
                Object o = hashMap.get("option" + (j + 1));
                if(o!=null && !o.toString().isEmpty()){
                    FileUtils.write(outputAsk,abcdefg[j]+":"+o.toString()+"\r\n", encoding,true);
                    askAndAnswer+=abcdefg[j]+":"+o.toString()+"\r\n";
                    anki+=abcdefg[j]+":"+o.toString()+"<br>";
                }
            }
            FileUtils.write(outputAsk, "\r\n", encoding,true);

            String[] split = ask.split("\\.");
            Object answer = hashMap.get("answer");
            FileUtils.write(outputAnswer,split[0]+":"+answer, encoding,true);
            askAndAnswer+="答案:"+answer;
            anki+="\t答案:"+answer;
            Object answer_parser = hashMap.get("answer_parser");
            if(answer_parser!=null&&!answer_parser.toString().isEmpty()&&!answer_parser.toString().contains("暂无解析")){
                FileUtils.write(outputAnswer,"。\r\n解释："+answer_parser, encoding,true);
                askAndAnswer+="。\r\n解释："+answer_parser;
                anki+="。<br>解释："+answer_parser;
            }
            FileUtils.write(outputAnswer,"\r\n", encoding,true);
            askAndAnswer+="\r\n\r\n";
            anki+="\r\n";

            FileUtils.write(outputAskAnswer,askAndAnswer, encoding,true);

            FileUtils.write(outputAnki,anki, "utf-8",true);
        }
    }
}
