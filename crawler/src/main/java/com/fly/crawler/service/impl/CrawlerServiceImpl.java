package com.fly.crawler.service.impl;

import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;


@Service
public class CrawlerServiceImpl implements CrawlerService {

    @Override
    public void running() {

    }

    @Override
    public Film extractFilm(Page page) {
        Film f = new Film();
        //影片页
        //1）片名
        page.putField("subject", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        f.setSubject(page.getResultItems().get("subject").toString().trim());


        Selectable selectableInfo = page.getHtml().xpath("//div[@id='info']");
        //2）导演
        f.setDirectors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:directedBy']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //3）演员
        f.setActors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:starring']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));

        //4）豆瓣编号
        f.setDoubanNo(page.getUrl().regex("/subject/(\\d+)/").toString());
        //5、6）豆瓣评分及评分人数
        Selectable selectableRating = page.getHtml().xpath("//div[@typeof='v:Rating']");
        PlainText object = (PlainText) selectableRating.xpath("//strong/text()");
        if (null != object && !"".equals(object.getFirstSourceText())) {
            f.setDoubanRating(Float.parseFloat(selectableRating.xpath("//strong/text()").toString()));
            f.setDoubanSum(Long.parseLong(selectableRating.xpath("//span[@property='v:votes']/text()").toString()));
        }
        //7)集数<span class="pl">集数:</span><br>
        String episodeNumber = selectableInfo.regex("<span class=\"pl\">集数:</span> (\\d+)\n"+
                " <br>").toString();
        if (null != episodeNumber && !"".equals(episodeNumber)) {
            f.setEpisodeNumber(episodeNumber);
        }
        //8）年代
        page.putField("year", page.getHtml().xpath("//div[@id='content']/h1//span[@class='year']/text()").regex("\\((.*)\\)"));
        if(page.getResultItems().get("year").toString()!=null)
            f.setYear(Short.parseShort(page.getResultItems().get("year").toString()));





        //校验数据是否符合抓取要求，非电影类（电视剧）不抓取
        //从网页中提取filmObject，只是部分字段，用于判断是否需要保存此object。

        return f;
    }
}
