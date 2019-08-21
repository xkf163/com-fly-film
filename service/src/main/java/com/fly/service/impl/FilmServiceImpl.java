package impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by xukangfeng on 2017/10/28 13:00
 */
@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    StarService starService;

    @Autowired
    PersonService personService;

    @Autowired
    FilmRepository filmRepository;


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Film extractFilmFirstFromCrawler(Page page) {
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

        return f;
    }

    @Override
    public Film extractFilmSecondFromCrawler(Page page,Film f) {

        //影片页
        //片名
        //page.putField("subject", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        //f.setSubject(page.getResultItems().get("subject").toString().trim());

        //豆瓣编号
        //f.setDoubanNo(page.getUrl().regex("/subject/(\\d+)/").toString());

        //影片简介
        Selectable selectableInfo = page.getHtml().xpath("//div[@id='info']");
        page.putField("info", selectableInfo);
        f.setInfo(page.getResultItems().get("info").toString());
        //imdb编号
        String imdbNo = selectableInfo.regex("<a href=\"http://www.imdb.com/title/tt\\d+\" target=\"_blank\" rel=\"nofollow\">(tt\\d+)</a>").toString();
        f.setImdbNo(imdbNo);
        //
        page.putField("subjectMain", page.getHtml().xpath("//div[@id='content']/h1//span[@property='v:itemreviewed']/text()"));
        f.setSubjectMain(page.getResultItems().get("subjectMain").toString().trim());
//        //年代
//        page.putField("year", page.getHtml().xpath("//div[@id='content']/h1//span[@class='year']/text()").regex("\\((.*)\\)"));
//        f.setYear(Short.parseShort(page.getResultItems().get("year").toString()));
//      page.putField("imdb", page.getHtml().xpath("//div[@id='info']").regex("<a href=\"http://www.imdb.com/title/tt\\d+\" target=\"_blank\" rel=\"nofollow\">(tt\\d+)</a>"));
        page.putField("introduce", page.getHtml().xpath("//div[@class='related-info']//div[@class='indent']//span[@property='v:summary']/text()"));
        f.setIntroduce(page.getResultItems().get("introduce").toString());
        //豆瓣评分及评分人数
//        Selectable selectableRating = page.getHtml().xpath("//div[@typeof='v:Rating']");
//        PlainText object = (PlainText) selectableRating.xpath("//strong/text()");
//        if (null != object && !"".equals(object.getFirstSourceText())) {
//            f.setDoubanRating(Float.parseFloat(selectableRating.xpath("//strong/text()").toString()));
//            f.setDoubanSum(Long.parseLong(selectableRating.xpath("//span[@property='v:votes']/text()").toString()));
//        }
        //导演
        //f.setDirectors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:directedBy']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //演员
        //f.setActors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:starring']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //影片类别
        f.setGenre(StringUtils.join(selectableInfo.xpath("//span[@property='v:genre']/text()").all().toArray(), ","));
        //发行日期
        f.setInitialReleaseDate(StringUtils.join(selectableInfo.xpath("//span[@property='v:initialReleaseDate']/text()").all().toArray(), ","));
        //影片时长
        f.setRuntime(StringUtils.join(selectableInfo.xpath("//span[@property='v:runtime']/@content").all().toArray(), ","));
        //影片所属国家/地区
        String country_temp = selectableInfo.regex("<span class=\"pl\">制片国家/地区:</span> (.*)\n" +
                " <br>").toString();
        if (null != country_temp && !"".equals(country_temp)) {
            String country = country_temp.substring(0, country_temp.indexOf("\n"));
            f.setCountry(country);
        }
        //影片其他片名
        String subject_temp = selectableInfo.regex("<span class=\"pl\">又名:</span> (.*)\n" +
                " <br>").toString();
        if (null != subject_temp && !"".equals(subject_temp)) {
            if (subject_temp.indexOf("\n") > 0) {
                String subjectOther = subject_temp.substring(0, subject_temp.indexOf("\n"));
                f.setSubjectOther(subjectOther);
            } else {
                f.setSubjectOther(subject_temp);
            }
        }



        return f;
    }


    @Override
    public Film findBySubjectAndDoubanNo(Film film) {
        //判断数据库里是否存在
        QFilm qFilm = QFilm.film;
        Predicate predicate = qFilm.subject.eq(film.getSubject()).and(qFilm.doubanNo.eq(film.getDoubanNo()));
        return filmRepository.findOne(predicate);
    }

    @Override
    public void save(Film film) {
        filmRepository.save(film);
    }

    @Override
    public String[] needCrawler(Film f,String directrAllowEmpty,String actorAllowEmpty) {

        String[] retString = {"true","false"}; //第一个参数表示是否需要抓取，第二个表示是否是电视剧

        //几种不需要保存的条件：
        //1）导演和主演列表为空就skip,不保存
        //2）发现集数不为空，判断是电视剧，
        //3）暂无评分 也不保存
        //|| StringUtils.isBlank(f.getDirectors()) 去掉了，有些情况导演不知名，可能为空
        if (f.getDoubanRating()==null || f.getYear()==null){
            //skip this page
            System.out.println("--->!!!film "+f.getSubject()+" 豆瓣编号为空、年代为空");
            retString[0] = "false";
        }
        if("true".equals(retString[0])) {
            if ("0".equals(directrAllowEmpty) && "0".equals(actorAllowEmpty)) {
                if (StringUtils.isBlank(f.getActors()) && StringUtils.isBlank(f.getDirectors())) {
                    System.out.println("--->!!!film " + f.getSubject() + " 导演和演员都为空");
                    retString[0] = "false";
                }
            } else if ("1".equals(directrAllowEmpty) && "1".equals(actorAllowEmpty)) {
                //都可以为空，什么都不做
            } else if ("1".equals(directrAllowEmpty) || "1".equals(actorAllowEmpty)) {
                if (StringUtils.isBlank(f.getActors()) || StringUtils.isBlank(f.getDirectors())) {
                    System.out.println("--->!!!film " + f.getSubject() + " 导演或演员为空");
                    retString[0] = "false";
                }
            }
        }
        //集数为1如圣斗士剧场版 ，故
        if(f.getEpisodeNumber()!=null && !"1".equals(f.getEpisodeNumber().trim())){
            retString[0] = "false";
            retString[1] = "true";
            System.out.println("--->!!!film "+f.getSubject()+" 是电视剧（集数"+f.getEpisodeNumber()+"）");
        }
        //4)判断数据库里是否存在
        if("true".equals(retString[0])) {
            Film film = findBySubjectAndDoubanNo(f);
            if (null != film) {
                System.out.println("--->!!!film " + f.getSubject() + " 已经存在于数据库中");
                retString[0] = "false";
            }
        }
        return retString;
    }




    /**
     * 提取并转换爬虫爬取的第一手数据
     * 转换成FilmVOPersonVO
     */
    @Autowired
    MediaRepository mediaRepository;


    @Override
    @Transactional
    public Object[] connectFilmForMedia(String onlyNone){

        List<Media> notFindMediaList = new ArrayList<>();
        List<Media> needUpdateMediaList = new ArrayList<>();
        List<Star> needUpdateStarList = new ArrayList<>();
        List<Star> needSaveStarList = new ArrayList<>();

        Map<String,Star> starHashMapInit = new HashMap<>();
        Map<String,Star> starHashMapFinal;
        List<Star> starsList = starService.findAll();
        //遍历库中stars，组成map，，后续根据doubanid来判断库中是否已存在
        for (Star star : starsList){
            starHashMapInit.put(star.getDouBanNo().trim(),star);
        }
        starHashMapFinal=new HashMap<>(starHashMapInit);

        //提取所有符合条件的media条目
        QMedia qMedia = QMedia.media;
        List<Media> mediaList;
        if(Boolean.valueOf(onlyNone)){
            //只处理没有关联film的Medias
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(0).and(qMedia.film.isNull()));
        }else{
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(0));
        }

        for(Media media : mediaList){
            Film film = findConnectedFilmForMedia(media);
            if(film == null){
                notFindMediaList.add(media);
                continue;
            }
            //-----当前正在处理的filmId
            String filmId = String.valueOf(film.getId());
            //step2)保存或更新当前film中的stars
            String directorsDoubanNo = film.getDirectors().trim();
            String actorsDoubanNo = film.getActors().trim();
            //director doubanid array
            String[] ddno_array=null,adno_array=null;
            if(directorsDoubanNo!=null)
                ddno_array = directorsDoubanNo.split(",");
            if(actorsDoubanNo!=null)
                adno_array = actorsDoubanNo.split(",");

            //as导演
            for(String ddno : ddno_array){
                //找star表，看是否存在，不存在则新建，存在即asdirect加上此filmid（先判断有无此filmid）
                if(starHashMapFinal.containsKey(ddno)){
                    Star star = starHashMapFinal.get(ddno);
                    //判断当前filmid是否已存在当前star的asdirect字段中
                    //不存在add进去，并更新number
                    String[] asDArray;
                    if(star.getAsDirector()!=null){
                        asDArray = star.getAsDirector().split(",");
                        if(asDArray!=null && !Arrays.asList(asDArray).contains(filmId)) {
                            String[] asDArrayNew = new String[asDArray.length + 1];
                            System.arraycopy(asDArray, 0, asDArrayNew, 0, asDArray.length);//将a数组内容复制新数组b
                            asDArrayNew[asDArrayNew.length - 1] = filmId;
                            star.setAsDirector(StringUtils.join(asDArrayNew, ","));
                            star.setAsDirectorNumber(asDArrayNew.length);

                            //star是地址引用，故若已添加，不需再次添加
                            if (!needUpdateStarList.contains(star) && !needSaveStarList.contains(star)) {
                                needUpdateStarList.add(star);
                            }
                        }
                    }else {
                        //asdirector空的情况
                        star.setAsDirector(filmId);
                        star.setAsDirectorNumber(1);

                        //star是地址引用，故若已添加，不需再次添加
                        if (!needUpdateStarList.contains(star) && !needSaveStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }
                    }

                }else{
                    //new star
                    Person person =personService.findByDoubanNo(ddno);
                    if(person==null){
                        continue;
                    }
                    Star star = new Star();
                    star.setDouBanNo(ddno);
                    star.setAsDirectorNumber(1);
                    star.setAsDirector(filmId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);

                    //新建保存
                    needSaveStarList.add(star);

                    //加入到starlist，防止重复生成star数据
                    starHashMapFinal.put(ddno,star);
                }

            }
            //as主演
            for(String adno : adno_array){
                if (adno.equals("1031931"))
                    System.out.println("amierhan");
                if(starHashMapFinal.containsKey(adno)){
                    Star star = starHashMapFinal.get(adno);
                    //判断当前filmid是否已存在当前star的asdirect字段中
                    //不存在add进去，并更新number
                    String[] asAArray = null;
                    if(star.getAsActor()!=null){
                        asAArray= star.getAsActor().split(",");
                        if(asAArray!=null && !Arrays.asList(asAArray).contains(filmId)) {
                            String[] asAArrayNew = new String[asAArray.length + 1];
                            System.arraycopy(asAArray, 0, asAArrayNew, 0, asAArray.length);//将a数组内容复制新数组b
                            asAArrayNew[asAArrayNew.length - 1] = filmId;
                            star.setAsActor(StringUtils.join(asAArrayNew, ","));
                            star.setAsActorNumber(asAArrayNew.length);

                            //star是地址引用，故若已添加，不需再次添加
                            if (!needUpdateStarList.contains(star) && !needSaveStarList.contains(star)) {
                                needUpdateStarList.add(star);
                            }
    //                        //此star一开始就没有，已经在needsaveStarlist中，否则会重复添加重复生成
    //                        if(starHashMapInit.containsKey(star.getDouBanNo())){
    //                            needUpdateStarList.add(star);
    //                        }
                        }
                    }  else {
                        //asactor空的情况
                        star.setAsActor(filmId);
                        star.setAsActorNumber(1);
                        //star是地址引用，故若已添加，不需再次添加
                        if (!needUpdateStarList.contains(star) && !needSaveStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }
                    }

                }else{
                    //new
                    Person person = personService.findByDoubanNo(adno);
                    if(person==null){
                        continue;
                    }
                    //new
                    Star star = new Star();
                    star.setDouBanNo(adno);
                    star.setAsActorNumber(1);
                    star.setAsActor(filmId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);

                    //new star
                    needSaveStarList.add(star);
                    //加入到starlist，防止重复生成star数据
                    starHashMapFinal.put(adno,star);

                }
            }

            //step1)update media obj
                media.setFilm(film);
                media.setUpdateDate(new Date());
                needUpdateMediaList.add(media);
        }


        /*
         批量保存
        List<Media> needUpdateMediaList = new ArrayList<>();
        List<Star> needUpdateStarList = new ArrayList<>();
        List<Star> needSaveStarList = new ArrayList<>();
         */
        int size  = needUpdateMediaList.size();
        System.out.println("needUpdateMediaList:::"+size);
        for (int i=0; i<size; i++){
            Media media = needUpdateMediaList.get(i);
            entityManager.merge(media);
            if(i % 50 == 0 || i==size-1){
                    entityManager.flush();
                    entityManager.clear();
            }
        }
        size  = needUpdateStarList.size();
        System.out.println("needUpdateStarList:::"+size);
        for (int i=0; i<size; i++){
            Star star = needUpdateStarList.get(i);
            entityManager.merge(star);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = needSaveStarList.size();
        System.out.println("needSaveStarList:::"+size);
        for (int i=0; i<size; i++){
            Star star = needSaveStarList.get(i);
            entityManager.persist(star);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }

        return new Object[]{needUpdateMediaList,notFindMediaList,needUpdateStarList,needSaveStarList};
    }


    /**
     * 根据media条目信息去film表找一一对应的filmobj，找不到返回null
     * @param media
     * @return
     */
    Film findConnectedFilmForMedia(Media media){
        if(media.getId()==644){
            System.out.println("aaa");
        }
        if(media.getFilm()!=null){
            return media.getFilm();
        }
        /**1）namechn和year完全匹配
         * 1.1）size=1 over
         * 1.2）size>1 nameEng contain subjectMain or subjectOther over
         * 1.3）none nameEng contain subjectMain and nameCHN contain subjectOther
         * 1.3.1）size=1 over
         * 1.3.2) over
         *
         */
        QFilm qFilm = QFilm.film;
        Predicate[] predicateArray = new Predicate[6];
        predicateArray[0] = qFilm.subject.trim().eq(media.getNameChn().trim()).and(qFilm.year.eq(media.getYear()));
        predicateArray[1] = (qFilm.subjectMain.trim().contains(media.getNameEng().trim()).or(qFilm.subjectOther.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));
        List<Film> films = (List<Film>) filmRepository.findAll(predicateArray[0]);
        if(films.size()== 1){
            return films.get(0);
        }else if(films.size()>1){
            for(Film film : films){
                String nameEng = media.getNameEng();
                if(nameEng!=null & (film.getSubjectMain().indexOf(nameEng)>0 | film.getSubjectOther().indexOf(nameEng)>0)){
                    return film;
                }
            }
        }else {
            films = (List<Film>) filmRepository.findAll(predicateArray[1]);
            if(films.size()==1){
                return films.get(0);
            }
        }


        //        //1: subject精确匹配
//        predicateArray[0]=qFilm.subject.trim().eq(media.getNameChn().trim()).and(qFilm.year.eq(media.getYear()));
//        //2:
//        predicateArray[1]=qFilm.subject.trim().eq(media.getNameChn().trim()).and(qFilm.subjectMain.trim().contains(media.getNameEng().trim())).and(qFilm.year.eq(media.getYear()));
//        //3:
//        predicateArray[2]=qFilm.subject.trim().eq(media.getNameChn().trim()).and(qFilm.subjectOther.trim().contains(media.getNameEng().trim())).and(qFilm.year.eq(media.getYear()));
//        //predicateArray[3]=qFilm.subject.trim().notEqualsIgnoreCase(mediaVO.getNameChn().trim()).and(qFilm.subjectMain.trim().contains(mediaVO.getNameChn().trim())).and(qFilm.subjectMain.trim().contains(mediaVO.getNameEng().trim())).and(qFilm.year.eq(mediaVO.getYear()));
//        //3: 0
//        predicateArray[3]=qFilm.subject.trim().notEqualsIgnoreCase(media.getNameChn().trim()).and(qFilm.subjectMain.trim().contains(media.getNameEng().trim()).and(qFilm.subjectOther.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));
//        //2: 2>1
//        predicateArray[4]=qFilm.subject.trim().notEqualsIgnoreCase(media.getNameChn().trim()).and(qFilm.subjectOther.trim().contains(media.getNameEng().trim()).and(qFilm.subjectOther.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));
//
//        predicateArray[5]=qFilm.subject.trim().notEqualsIgnoreCase(media.getNameChn().trim()).and(qFilm.subjectOther.trim().notEqualsIgnoreCase(media.getNameEng().trim()).and(qFilm.subjectOther.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));
//
//        for (int j = 0; j<predicateArray.length; j++){
//            List<Film> filmList = (List<Film>) filmRepository.findAll(predicateArray[j]);
//            if(filmList.size() == 1){
//                return filmList.get(0);
//            }
//        }

        return null;

    }


    @Autowired
    StarRepository starRepository;

    /**
     * 查找影人的作品清单
     * @param starId
     * @param type 2:参演作品，1就是参导作品
     * @return
     */
    @Override
    public  List<Film> listFilmsByStarId(String starId, String type){
//        personId ="9112";

        Star star = starRepository.findOne(Long.parseLong(starId));
        String films = star.getAsDirector();
        if("2".equals(type)){
            films = star.getAsActor();
        }
        if(films==null){
            return new ArrayList<Film>();
        }

        String[] filmArray = films.split(",");

        QFilm qFilm = QFilm.film;
        Predicate predicate1 = qFilm.id.stringValue().in(Arrays.asList(filmArray));
        return (List<Film>) filmRepository.findAll(predicate1,new Sort("year"));
    }

    /**
     * 返回所有数据库中已存在的film 的 doubanNo
     */

    @Override
    public List<String> listFilmsDouBanNo() {

        QFilm qFilm = QFilm.film;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        List<String> listDouBanNo = jpaQueryFactory.select(qFilm.doubanNo)
                .from(qFilm)
                .fetch();
        return listDouBanNo;

    }

    @Override
    public List<String> listFilmsNameUnconnect() {
        final QMedia qMedia = QMedia.media;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        List<String> listFilmsName = jpaQueryFactory.selectFrom(qMedia)
                .select(qMedia.nameChn)
                .where(qMedia.film.id.isNull())
                .fetch();
        return listFilmsName;
    }
}
