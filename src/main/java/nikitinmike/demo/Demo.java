package nikitinmike.demo;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Class.forName;

@RestController
@AllArgsConstructor
public class Demo {

//    @RequestMapping("/")
    private String demo() throws Exception {
        String catalog = getPage("http://www.stihi.ru/poems/list.html?day=20&month=02&year=2018&topic=all");
//        System.out.println();
//        return getLinks(catalog).toString();
        return stihiStrip(getPage("http://www.stihi.ru/2016/10/28/1984")); // "http://www.stihi.ru/2012/02/20/7572"
//        "http://www.stihi.ru/poems/list.html?topic=all"
    }
    
    @RequestMapping("/")
    private List<String> getLinks(
            @RequestParam(defaultValue = "http://www.stihi.ru/poems/list.html?topic=all") String html
    ) throws Exception {
//        System.out.println(html);
        String root="http://www.stihi.ru";
        Matcher m = Pattern.compile("<a href=\"" +"(.+?)"+"\">").matcher(getPage(html)); // <div class="copyright">
        List<String> ls=new ArrayList();
        while (m.find())
            if (!Pattern.compile(root).matcher(m.group(1)).find()) ls.add(root+m.group(1));
            else ls.add(m.group(1));
        return ls;
    }

    private String getPage(String s) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(s).openConnection();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"windows-1251"));
        StringBuilder result = new StringBuilder();
        for(String line;(line=rd.readLine())!=null;) result.append(line); // System.out.println(line)
        rd.close();
//        System.out.println(result);
        return result.toString();
    }

    private String stihiStrip(String html){
        Matcher m = Pattern.compile("<div class=\"text\">" +"(.+?)"+"</div>").matcher(html.toString()); // <div class="copyright">
        if (m.find( )) return Pattern.compile("&nbsp;|&quot;")
            .matcher(Pattern.compile("<br>").matcher(m.group(1)).replaceAll("\n"))
            .replaceAll(" ");
        return "";
    }

}
