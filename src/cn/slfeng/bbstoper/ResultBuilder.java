package cn.slfeng.bbstoper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ResultBuilder {
    private BBSToper main;

    @SneakyThrows
    public @Nullable List<ResultContainer> buildFromString(@NotNull String content) {
        List<ResultContainer> containers = new ArrayList<>();
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (DocumentException ex) {
            ex.printStackTrace();
            main.getLogger().info(content);
            main.getLogger().info("返回解析失败，可能是MCBBS宕机了或者是插件需要更新?");
            return null;
        }
        Element root = document.getRootElement();
        String pageSource = root.getTextTrim();
        org.jsoup.nodes.Document page = Jsoup.parse(pageSource);

        Elements rows = page.select("table[class=list]").get(0).select("tr");
        for (int i = 0; i <rows.size() ; i++) {
            if(i == 0) //忽略标题
                continue;
            org.jsoup.nodes.Element row = rows.get(i);
            Elements record = row.select("td");

            if(record.size() < 3){
                main.getLogger().info("Can't read the data, please contact the author: 2908803755, skipping");
                continue;
            }
            containers.add(ResultContainer.builder().user(record.get(0).text()).date(record.get(1).text()).action(record.get(2).text()).build());
        }
        return containers;
    }
}