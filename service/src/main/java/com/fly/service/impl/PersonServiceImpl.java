package impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:34 2017/10/30
 */
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepository personRepository;


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Person extractFilmSecondFromCrawler(Page page) {

        Person p = new Person();
        //姓名
        page.putField("name", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        p.setName(page.getResultItems().get("name").toString());
        //豆瓣编号
        p.setDoubanNo(page.getUrl().regex("https://movie\\.douban\\.com/celebrity/(\\d+)/").toString());

        //人物脚本信息
        page.putField("nameExtend", page.getHtml().xpath("//div[@id='content']/h1/text()"));
        page.putField("introduce", page.getHtml().xpath("//div[@id='intro']//div[@class='bd']//span[@class='all hidden']/text()"));
        if(page.getResultItems().get("introduce").toString() == null){
            page.putField("introduce", page.getHtml().xpath("//*[@id=\"intro\"]/div[@class='bd']/text()"));
        }
        //人物简介字符串集合
        Selectable selectableInfo =  page.getHtml().xpath("//div[@class='article']//div[@class='info']//li");
        page.putField("info", selectableInfo);

        String gender = StringUtils.join( selectableInfo.regex("<li> <span>性别</span>: (.*) </li>").all().toArray(),",");
        String birthday = StringUtils.join( selectableInfo.regex("<li> <span>出生日期</span>: (.*) </li>").all().toArray(),",");
        String birthplace = StringUtils.join( selectableInfo.regex("<li> <span>出生地</span>: (.*) </li>").all().toArray(),",");
        String profession = StringUtils.join( selectableInfo.regex("<li> <span>职业</span>: (.*) </li>").all().toArray(),",");
        String imdbNo = StringUtils.join( selectableInfo.regex("<li> <span>imdb编号</span>: (.*) </li>").regex("<a href=\"http://www.imdb.com/name/nm\\d+\" target=\"_blank\">(nm\\d+)</a>").all().toArray(),",");

        p.setNameExtend(page.getResultItems().get("nameExtend").toString());
        p.setIntroduce(page.getResultItems().get("introduce").toString());
        p.setInfo(page.getResultItems().get("info").toString());

        p.setBirthDay(birthday);
        p.setJob(profession);
        p.setGender(gender);
        p.setImdbNo(imdbNo);
        p.setBirthPlace(birthplace);

        return p;
    }

    @Override
    public Person extractFilmFirstFromCrawler(Page page) {
        Person p = new Person();
        page.putField("name", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        p.setName(page.getResultItems().get("name").toString());
        p.setDoubanNo(page.getUrl().regex("https://movie\\.douban\\.com/celebrity/(\\d+)/").toString());
        return p;
    }

    @Override
    public Person findByNameAndDoubanNO(Person p) {
        QPerson qPerson = QPerson.person;
        Predicate predicate = qPerson.name.eq(p.getName()).and(qPerson.doubanNo.eq(p.getDoubanNo()));
        return personRepository.findOne(predicate);
    }

    @Override
    public void save(Person person) {
        personRepository.save(person);
    }

    @Override
    public boolean needCrawler(Person person) {

        Person p = findByNameAndDoubanNO(person);
        if(null == p){
            return true;
        }
        return false;
    }

    @Override
    public Person findByDoubanNo(String doubanNo) {
        return personRepository.findByDoubanNo(doubanNo);
    }


    @Override
    public List<String> listPersonsDouBanNo() {
        QPerson qPerson = QPerson.person;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        List<String> listDouBanNo = jpaQueryFactory.select(qPerson.doubanNo)
                .from(qPerson)
                .fetch();
        return listDouBanNo;
    }


    @Override
    public Person findById(Long id) {
        return personRepository.findOne(id);
    }
}
