package nikitinmike.demo;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@AllArgsConstructor
public class Demo {

//    @RequestMapping("/demo")
    private String demo(
        @RequestParam(defaultValue = "http://www.stihi.ru/poems/list.html?topic=all") String url
    ) throws Exception {
        String catalog = getPage("http://www.stihi.ru/poems/list.html?topic=all");
//        String catalog = getPage("http://www.stihi.ru/poems/list.html?day=20&month=02&year=2018&topic=all");
//        System.out.println(stihiStrip(getPage("http://www.stihi.ru/2016/10/28/1984")));
//        return getLinks(catalog).toString();
        return catalog;
    }

    @RequestMapping("/")
    private List<String> getLinks(
//        @PathVariable String path,
//      "http://www.stihi.ru/poems/list.html?type=all" // selected
        @RequestParam(defaultValue = "http://www.stihi.ru/poems/list.html?topic=all" ) String url
//        @RequestParam(required=false) Integer year,
//        @RequestParam(required=false) Integer month,
//        @RequestParam(required=false) Integer day
        ) throws Exception {
//        System.out.println(path);
        System.out.println(url);
//        System.out.printf(" %d-%d-%d ",year,month,day);
//        if (year!=null&&month!=null&&day!=null) System.out.println(LocalDate.of(year,month,day));
        String root="http://www.stihi.ru",local="http://localhost:8080";
        Matcher m = Pattern.compile("<a href=(.+?)>").matcher(getPage(url));
        List<String> ls=new ArrayList(),stihi=new ArrayList();
        while (m.find())
            if (!Pattern.compile(root).matcher(m.group(1)).find())
                if (Pattern.compile("poemlink").matcher(m.group(1)).find())
                    stihi.add(local+"/stihi?url=" +root+
                        Pattern.compile("\"").matcher(
                            Pattern.compile(" class=\".+\"").matcher(m.group(1)).replaceFirst("")
                        ).replaceAll("")
                    );
                else ls.add(local+"/?url=" +root+
                    Pattern.compile("\"").matcher(
                        Pattern.compile("&").matcher(
                            Pattern.compile(" class=\".+\"").matcher(m.group(1)).replaceFirst("") // authorlink
                        ).replaceAll("%26") // "&amp;"
                    ).replaceAll("")
                );
        stihi.addAll(ls);
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
