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

    @RequestMapping("/demo")
    private String demo() throws Exception {
//        "http://www.stihi.ru/poems/list.html?topic=all"
        String catalog = getPage("http://www.stihi.ru/poems/list.html?day=20&month=02&year=2018&topic=all");
        System.out.println(stihiStrip(getPage("http://www.stihi.ru/2016/10/28/1984")));
//        return getLinks(catalog).toString();
        return catalog;
    }

    @RequestMapping("/")
    private List<String> getLinks( // "http://www.stihi.ru/poems/list.html?topic=all"
            @RequestParam(defaultValue = "http://www.stihi.ru/poems/list.html?type=selected") String html
    ) throws Exception {
//        System.out.println(html);
        String root="http://www.stihi.ru";
        Matcher m = Pattern.compile("<a href=(.+?)>").matcher(getPage(html));
        List<String> ls=new ArrayList();
        List<String> stihi=new ArrayList();
        while (m.find())
            if (!Pattern.compile(root).matcher(m.group(1)).find())
                if (Pattern.compile("poemlink").matcher(m.group(1)).find()) // "authorlink" // poemlink
                    stihi.add("http://localhost:8080/stihi?url=" +root+
                        Pattern.compile("\"").matcher(
                            Pattern.compile(" class=\".+\"").matcher(m.group(1)).replaceFirst("")
                        ).replaceAll("")
                    );
                else ls.add(m.group(1));
        return stihi;
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

    @RequestMapping("/stihi")
    private String stihiStrip(@RequestParam String url) throws Exception { // <div class="copyright">
        Matcher m = Pattern.compile("<div class=\"text\">(.+?)</div>").matcher(getPage(url));
        if (m.find()) return Pattern.compile("&nbsp;|&quot;")
            .matcher(Pattern.compile("<br>").matcher(m.group(1)).replaceAll("\n"))
            .replaceAll(" ");
        return "";
    }

}
