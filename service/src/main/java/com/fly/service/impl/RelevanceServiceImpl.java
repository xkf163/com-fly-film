package com.fly.service.impl;

import com.fly.common.base.pojo.ResultBean;
import com.fly.dao.FilmRepository;
import com.fly.dao.MediaRepository;
import com.fly.dao.SeriesRepository;
import com.fly.dao.StarRepository;
import com.fly.entity.*;
import com.fly.pojo.Relevance;
import com.fly.service.PersonService;
import com.fly.service.RelevanceService;
import com.fly.service.StarService;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class RelevanceServiceImpl implements RelevanceService {


    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    StarRepository starRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    StarService starService;

    @Autowired
    PersonService personService;

    @PersistenceContext
    EntityManager entityManager;




    static String runningLog = "";

    @Override
    public String runningRecord(){
        String ret = runningLog;
        runningLog = "";
        return ret;
    }


    @Transactional
    public void initStarPropWithQueryDsl()
    {
        //querydsl查询实体
        QStar qStar = QStar.star;

        //00000000)初始化star中的 几个字段值 asActor asActorNumber asDirector asDirectorNumber asWriter asWriterNumber Person
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Long a = jpaQueryFactory.update(qStar).set(qStar.asActor,"").set(qStar.asActorNumber,0).set(qStar.asDirector,"").set(qStar.asDirectorNumber,0).set(qStar.asWriter,"").set(qStar.asWriterNumber,0)
                .setNull(qStar.person)
                .where(qStar.deleted.ne(1))
                .execute();
        System.out.println(a);
        //return a;
    }

    @Transactional
    public void initMediaPropWithQueryDsl()
    {
        //querydsl查询实体
        QMedia qMedia = QMedia.media;
        //00000000)
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Long a = jpaQueryFactory.update(qMedia).setNull(qMedia.Director).setNull(qMedia.Actor).setNull(qMedia.Writer)
                .setNull(qMedia.film)
                .where(qMedia.deleted.ne(1))
                .execute();
        System.out.println(a);
        //return a;
    }

    /**
     * 为Media关联Film
     */
    @Override
    @Transactional
    public ResultBean<String> relevantFilmForMedia(Relevance relevance) {

        long startTime=System.currentTimeMillis();   //获取开始时间

        String personDoubanUrl , personDoubanUrlPre = "https://movie.douban.com/celebrity/";
        List<String> personNotFindDoubanUrlList = new ArrayList<>();
        List<Media> filmNotFindMediaList = new ArrayList<>();
        List<Media> needUpdateMediaList = new ArrayList<>();
        List<Star> needUpdateStarList = new ArrayList<>(); //需要更新的star

        List<Star> savedStarList = new ArrayList<>();
        //Map<String,String> starNeedSaveMap = new HashMap<>(); //filmId 和 star doubanNo

        class Tool {

             void CatchStar(Star star,Person person,Media media,String douBanNo, String fieldType) {
                 String mediaId = String.valueOf(media.getId());
                //2) person存在，创建或更新star
                if (star != null) {
                    star.setPerson(person);
                    //判断当前filmid是否已存在当前star的asdirect字段中
                    //不存在add进去，并更新number
                    String[] oldArray;
                    String oldString = star.getAsDirector();
                    if (oldString!= null && !StringUtils.isEmpty(oldString)) {
                        oldArray = oldString.split(",");
                        if (oldArray != null && !Arrays.asList(oldArray).contains(mediaId)) {
                            String[] newArray = new String[oldArray.length + 1];
                            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);//将a数组内容复制新数组b
                            newArray[newArray.length - 1] = mediaId;

                            if ("d".equals(fieldType)) {
                                star.setAsDirector(StringUtils.join(newArray, ","));
                                star.setAsDirectorNumber(newArray.length);
                            } else if("a".equals(fieldType)){
                                star.setAsActor(StringUtils.join(newArray, ","));
                                star.setAsActorNumber(newArray.length);
                            }else if("w".equals(fieldType)){
                                star.setAsWriter(StringUtils.join(newArray, ","));
                                star.setAsWriterNumber(newArray.length);
                            }

                            //star是地址引用，故若已添加，不需再次添加
                            star.setUpdateDate(new Date());
                            if (!needUpdateStarList.contains(star)) {
                                needUpdateStarList.add(star);
                            }

                        }
                    } else {
                        if ("d".equals(fieldType)) {
                            star.setAsDirector(mediaId);
                            star.setAsDirectorNumber(1);
                        } else if("a".equals(fieldType)){
                            star.setAsActor(mediaId);
                            star.setAsActorNumber(1);
                        }else if("w".equals(fieldType)){
                            star.setAsWriter(mediaId);
                            star.setAsWriterNumber(1);
                        }

                        //star是地址引用，故若已添加，不需再次添加
                        star.setUpdateDate(new Date());
                        if (!needUpdateStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }

                    }

                    //starService.save(star);

                } else {
                    //根据Person信息创建new star
                    star = new Star();
                    star.setCreateDate(new Date());
                    star.setDouBanNo(douBanNo);
                    star.setAsDirectorNumber(1);
                    star.setAsDirector(mediaId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);
                    star.setUpdateDate(new Date());
                    starService.save(star);

                    //新建star保存
                    savedStarList.add(star);

                }


                //210223ADD Media存放StarIds
                String starId = String.valueOf(star.getId());
                String StarString = null;
                 if ("d".equals(fieldType)) {
                     StarString = media.getDirector();
                 } else if("a".equals(fieldType)){
                     StarString = media.getActor();
                 }else if("w".equals(fieldType)){
                     StarString = media.getWriter();
                 }
                if (StarString != null && !StringUtils.isEmpty(StarString)) {
                    String[] tmpArray= null;
                    tmpArray = StarString.split(",");
//                    if ("d".equals(fieldType)) {
//                        tmpArray = media.getDirector().split(",");
//                    } else if("a".equals(fieldType)){
//                        tmpArray = media.getActor().split(",");
//                    }else if("w".equals(fieldType)){
//                        tmpArray = media.getWriter().split(",");
//                    }

                    if (tmpArray!= null && !Arrays.asList(tmpArray).contains(starId)) {
                        String[] tmpArrayNew = new String[tmpArray.length + 1];
                        System.arraycopy(tmpArray, 0, tmpArrayNew, 0, tmpArray.length);//将a数组内容复制新数组b
                        tmpArrayNew[tmpArrayNew.length - 1] = starId;
                        if ("d".equals(fieldType)) {
                            media.setDirector(StringUtils.join(tmpArrayNew, ","));
                        } else if("a".equals(fieldType)){
                            media.setActor(StringUtils.join(tmpArrayNew, ","));
                        }else if("w".equals(fieldType)){
                            media.setWriter(StringUtils.join(tmpArrayNew, ","));
                        }

                        //media是地址引用，故若已添加，不需再次添加
                        media.setUpdateDate(new Date());
                        if (!needUpdateMediaList.contains(media)) {
                            needUpdateMediaList.add(media);
                        }
                    }
                } else {
                    if ("d".equals(fieldType)) {
                        media.setDirector(starId);
                    } else if("a".equals(fieldType)){
                        media.setActor(starId);
                    }else if("w".equals(fieldType)){
                        media.setWriter(starId);
                    }
                    //media是地址引用，故若已添加，不需再次添加
                    media.setUpdateDate(new Date());
                    if (!needUpdateMediaList.contains(media)) {
                        needUpdateMediaList.add(media);
                    }
                }
                //--end



            }


        }





        //前台传递的参数，是否遍历全库media
        String relevantAll = relevance.getRelevantAll();
        //提取所有符合条件的media条目
        QMedia qMedia = QMedia.media;
        List<Media> mediaList;
        if("1".equals(relevantAll)){
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.ne(1));
            this.initStarPropWithQueryDsl();
            this.initMediaPropWithQueryDsl();
//            if (this.initStarPropWithQueryDsl()>0){
//                System.out.println("清空star表字段数据");
//            }
//            if (this.initMediaPropWithQueryDsl()>0){
//                System.out.println("清空media表字段数据");
//            }
            entityManager.flush();
        }else{
            //只处理未关联film的Media
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.ne(1).and(qMedia.film.isNull()));
        }

        //mediaList  = new ArrayList<>();

        //数据库中已存在的person编号
        //List<String> starDouBanNoAllList = starService.findAllDouBanNo();
        Film film,oldFilm;
        int ind = 1;
        for(Media media : mediaList){

            runningLog  = ind+"、"+media.getName();
            System.out.println("-----"+ind+"----------------"+media.getNameChn());
            ind++;

            //1)为Media关联Film，并加入更新List
            oldFilm = media.getFilm();
            film = findConnectedFilmForMedia(media);
            if (oldFilm != film){
                media.setFilm(film);
                media.setUpdateDate(new Date());
                needUpdateMediaList.add(media);
            }
            if(film == null){
                filmNotFindMediaList.add(media);
                continue;
            }

            //2）为当前匹配到的Film中的导演和演员Person转化为Star（或更新Star）


            String directorsDoubanNo = film.getDirectors();
            String actorsDoubanNo = film.getActors();
            String writerDoubanNo = film.getScreenWriter();

            String[] ddno_array=null,adno_array=null,sdno_array=null;

            if (directorsDoubanNo != null && !StringUtils.isEmpty(directorsDoubanNo))
                ddno_array = directorsDoubanNo.split(",");
            if (actorsDoubanNo != null && !StringUtils.isEmpty(actorsDoubanNo) )
                adno_array = actorsDoubanNo.split(",");
            if (writerDoubanNo != null && !StringUtils.isEmpty(writerDoubanNo) )
                sdno_array = writerDoubanNo.split(",");

            Person person = null;
            Star star = null;
            //Film中的导演doubanNo
            if (ddno_array != null) {
                for (String douBanNo : ddno_array) {
                    //找star表，看是否存在，不存在则新建，存在即asdirect加上此filmid（先判断有无此filmid）
                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (needUpdateStarList.contains(star))  {
                        //System.out.println(needUpdateStarList.indexOf(star));
                        star = needUpdateStarList.get(needUpdateStarList.indexOf(star));
                    }
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }
                    new Tool().CatchStar(star,person,media,douBanNo,"d");
                }
            }

            //as主演
            if (adno_array != null) {
                for (String douBanNo : adno_array) {
                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (needUpdateStarList.contains(star))  {
                        //System.out.println(needUpdateStarList.indexOf(star));
                        star = needUpdateStarList.get(needUpdateStarList.indexOf(star));
                    }
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }
                    new Tool().CatchStar(star,person,media,douBanNo,"a");
                }
            }

            //as编剧
            if (sdno_array != null) {
                for (String douBanNo : sdno_array) {
                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (needUpdateStarList.contains(star))  {
                        //System.out.println(needUpdateStarList.indexOf(star));
                        star = needUpdateStarList.get(needUpdateStarList.indexOf(star));
                    }
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }
                    new Tool().CatchStar(star,person,media,douBanNo,"w");
                }
            }



        }


        System.out.println("----------mediaAllList:::"+mediaList.size());

        //3 批量更新
        int size  = needUpdateMediaList.size();
        System.out.println("----------needUpdateMediaList:::"+size);
        for (int i=0; i<size; i++){
            Media media = needUpdateMediaList.get(i);
            entityManager.merge(media);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = needUpdateStarList.size();
        System.out.println("----------needUpdateStarList:::"+size);
        for (int i=0; i<size; i++){
            Star star = needUpdateStarList.get(i);
            entityManager.merge(star);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = filmNotFindMediaList.size();
        System.out.println("----------filmNotFindMediaList:::"+size);

        size  = savedStarList.size();
        System.out.println("----------starSavedList:::"+size);

        size  = personNotFindDoubanUrlList.size();
        System.out.println("----------personNotFindStarList:::"+size);


        QStar star = QStar.star;
        ind = 1;
        mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(1));
        for(Media media : mediaList){
            String mediaId = String.valueOf(media.getId());
            runningLog  ="已删除的Media" +ind+"、"+media.getName();
            System.out.println("-----"+ind+"------deleted-------"+media.getNameChn());

            ind++;

            //2）删除的media从series里去除
            QSeries series = QSeries.series;
            List<Series> seriesList = (List<Series>) seriesRepository.findAll(series.asMedias.contains(mediaId));
            for (Series series1 : seriesList){
                String asOld = series1.getAsMedias();
                String[] asOldArray = asOld.split(",");
                List<String> asOldList = new ArrayList<String>(Arrays.asList(asOldArray));
                if (asOldList.contains(mediaId)) {
                    asOldList.remove(mediaId);  // ok
                    series1.setAsMediaNumber(asOldList.size());
                    series1.setAsMedias(StringUtils.join(asOldList.toArray(), ","));
                    series1.setUpdateDate(new Date());
                    seriesRepository.save(series1);
                }
            }

        }



        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");


        runningLog =  "";

        String[] doubanListArray = personNotFindDoubanUrlList.toArray(new String[personNotFindDoubanUrlList.size()]);
        String doubanListString = StringUtils.join(doubanListArray,"\n");
        return new ResultBean<>(doubanListString);




    }







        /**
         * 为Media关联Film
         */
    public ResultBean<String> relevantFilmForMediaBAK(Relevance relevance) {

        long startTime=System.currentTimeMillis();   //获取开始时间

        String personDoubanUrl , personDoubanUrlPre = "https://movie.douban.com/celebrity/";
        List<String> personNotFindDoubanUrlList = new ArrayList<>();
        List<Media> filmNotFindMediaList = new ArrayList<>();
        List<Media> needUpdateMediaList = new ArrayList<>();
        List<Star> needUpdateStarList = new ArrayList<>(); //需要更新的star

        List<Star> starSavedList = new ArrayList<>();
        //Map<String,String> starNeedSaveMap = new HashMap<>(); //filmId 和 star doubanNo

        //前台传递的参数，是否遍历全库media
        String relevantAll = relevance.getRelevantAll();


        //提取所有符合条件的media条目
        QMedia qMedia = QMedia.media;
        List<Media> mediaList;
        if("1".equals(relevantAll)){
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.ne(1));
//            if (this.initStarPropWithQueryDsl()>0){
//                System.out.println("清空star表字段");
//            }
        }else{
            //只处理未关联film的Media
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.ne(1).and(qMedia.film.isNull()));
        }



        //数据库中已存在的person编号
        //List<String> starDouBanNoAllList = starService.findAllDouBanNo();
        Film film,oldFilm;
        int ind = 1;
        for(Media media : mediaList){

            runningLog  = ind+"、"+media.getName();
            System.out.println("-----"+ind+"----------------"+media.getNameChn());
            ind++;

            //1)为Media关联Film，并加入更新List
            oldFilm = media.getFilm();
            film = findConnectedFilmForMedia(media);
            if (oldFilm != film){
                media.setFilm(film);
                media.setUpdateDate(new Date());
                needUpdateMediaList.add(media);
            }
            if(film == null){
                filmNotFindMediaList.add(media);
                continue;
            }



            //2）为当前匹配到的Film中的导演和演员Person转化为Star（或更新Star）
            String mediaId = String.valueOf(media.getId());

            String directorsDoubanNo = film.getDirectors();
            String actorsDoubanNo = film.getActors();
            String writerDoubanNo = film.getScreenWriter();







            String[] ddno_array=null,adno_array=null,sdno_array=null;

            if (directorsDoubanNo != null && !StringUtils.isEmpty(directorsDoubanNo))
                ddno_array = directorsDoubanNo.split(",");
            if (actorsDoubanNo != null && !StringUtils.isEmpty(actorsDoubanNo) )
                adno_array = actorsDoubanNo.split(",");
            if (writerDoubanNo != null && !StringUtils.isEmpty(writerDoubanNo) )
                sdno_array = writerDoubanNo.split(",");


            Person person = null;
            Star star = null;
            //Film中的导演doubanNo
            if (ddno_array != null) {
                for (String douBanNo : ddno_array) {
                    //找star表，看是否存在，不存在则新建，存在即asdirect加上此filmid（先判断有无此filmid）
                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }

                    //star = CatchStar(star,person,douBanNo,"d",mediaId);

                    //2) person存在，创建或更新star
                    //if (starDouBanNoAllList.contains(douBanNo)) {
                    if (star != null) {
                        star.setPerson(person);
                        //判断当前filmid是否已存在当前star的asdirect字段中
                        //不存在add进去，并更新number
                        String[] asDArray;
                        if (star.getAsDirector() != null && !StringUtils.isEmpty(star.getAsDirector())) {
                            asDArray = star.getAsDirector().split(",");
                            if (asDArray != null && !Arrays.asList(asDArray).contains(mediaId)) {
                                String[] asDArrayNew = new String[asDArray.length + 1];
                                System.arraycopy(asDArray, 0, asDArrayNew, 0, asDArray.length);//将a数组内容复制新数组b
                                asDArrayNew[asDArrayNew.length - 1] = mediaId;
                                star.setAsDirector(StringUtils.join(asDArrayNew, ","));
                                star.setAsDirectorNumber(asDArrayNew.length);
                            }
                        } else {
                            //asdirector空的情况
                            star.setAsDirector(mediaId);
                            star.setAsDirectorNumber(1);
                        }
                        star.setUpdateDate(new Date());
                        //starService.save(star);

                        //star是地址引用，故若已添加，不需再次添加
                        if (!needUpdateStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }

                    } else {
                        //根据Person信息创建new star
                        star = new Star();
                        star.setCreateDate(new Date());
                        star.setDouBanNo(douBanNo);
                        star.setAsDirectorNumber(1);
                        star.setAsDirector(mediaId);
                        star.setName(person.getName());
                        star.setNameExtend(person.getNameExtend());
                        star.setPerson(person);
                        starService.save(star);
                        //保存的队列
                        starSavedList.add(star);
                        //加入，防止重复加入
                        //starDouBanNoAllList.add(douBanNo);
                    }

                    //210223ADD Media存放StarIds
                    String starId = String.valueOf(star.getId());
                    if (media.getDirector() != null && !StringUtils.isEmpty(media.getDirector())) {
                        String[] asDArray;
                        asDArray = media.getDirector().split(",");
                        if (asDArray != null && !Arrays.asList(asDArray).contains(starId)) {
                            String[] asDArrayNew = new String[asDArray.length + 1];
                            System.arraycopy(asDArray, 0, asDArrayNew, 0, asDArray.length);//将a数组内容复制新数组b
                            asDArrayNew[asDArrayNew.length - 1] = mediaId;
                            media.setDirector(StringUtils.join(asDArrayNew, ","));
                        }
                    } else {
                        //Director空的情况
                        media.setDirector(starId);
                    }
                    //media是地址引用，故若已添加，不需再次添加
                    if (!needUpdateMediaList.contains(media)) {
                        needUpdateMediaList.add(media);
                    }


                }
            }




            //as主演
            if (adno_array != null) {
                for (String douBanNo : adno_array) {

                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }


                    if (star != null) {
                        star.setPerson(person);
                        //判断当前filmid是否已存在当前star的asdirect字段中
                        //不存在add进去，并更新number
                        String[] asAArray = null;
                        if (star.getAsActor() != null  && !StringUtils.isEmpty(star.getAsActor())) {
                            asAArray = star.getAsActor().split(",");
                            if (asAArray != null && !Arrays.asList(asAArray).contains(mediaId)) {
                                String[] asAArrayNew = new String[asAArray.length + 1];
                                System.arraycopy(asAArray, 0, asAArrayNew, 0, asAArray.length);//将a数组内容复制新数组b
                                asAArrayNew[asAArrayNew.length - 1] = mediaId;
                                star.setAsActor(StringUtils.join(asAArrayNew, ","));
                                star.setAsActorNumber(asAArrayNew.length);

                            }
                        } else {
                            //asactor空的情况
                            star.setAsActor(mediaId);
                            star.setAsActorNumber(1);

                        }
                        star.setUpdateDate(new Date());
                        //starService.save(star);

                        //star是地址引用，故若已添加，不需再次添加
                        if (!needUpdateStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }

                    } else {

                        star = new Star();
                        star.setCreateDate(new Date());
                        star.setDouBanNo(douBanNo);
                        star.setAsActorNumber(1);
                        star.setAsActor(mediaId);
                        star.setName(person.getName());
                        star.setNameExtend(person.getNameExtend());
                        star.setPerson(person);

                        //新建star保存
                        starService.save(star);

                        starSavedList.add(star);

//                        //加入，防止重复加入
//                        starDouBanNoAllList.add(douBanNo);

                    }

                    //210223ADD Media存放StarIds
                    String starId = String.valueOf(star.getId());
                    if (media.getActor() != null && !StringUtils.isEmpty(media.getActor())) {
                        String[] asDArray;
                        asDArray = media.getActor().split(",");
                        if (asDArray != null && !Arrays.asList(asDArray).contains(starId)) {
                            String[] asDArrayNew = new String[asDArray.length + 1];
                            System.arraycopy(asDArray, 0, asDArrayNew, 0, asDArray.length);//将a数组内容复制新数组b
                            asDArrayNew[asDArrayNew.length - 1] = mediaId;
                            media.setActor(StringUtils.join(asDArrayNew, ","));
                        }
                    } else {
                        //Director空的情况
                        media.setActor(starId);
                    }
                    //media是地址引用，故若已添加，不需再次添加
                    if (!needUpdateMediaList.contains(media)) {
                        needUpdateMediaList.add(media);
                    }


                }
            }
            //as编剧
            if (sdno_array != null) {
                for (String douBanNo : sdno_array) {

                    //1)重新关联person，如果person被删，则同时删除已存在的star
                    person = personService.findByDouBanNo(douBanNo);
                    star = starService.findByDouBanNo(douBanNo);
                    if (person == null){
                        //加入到未找到person队列
                        personDoubanUrl = personDoubanUrlPre+ douBanNo +"/";
                        if (!personNotFindDoubanUrlList.contains(personDoubanUrl)){
                            personNotFindDoubanUrlList.add(personDoubanUrl);
                        }
                        if (star != null){
                            starRepository.delete(star);
                        }
                        continue;
                    }


                    if (star != null) {
                        star.setPerson(person);
                        //判断当前filmid是否已存在当前star的asdirect字段中
                        //不存在add进去，并更新number
                        String[] asAArray = null;
                        if (star.getAsWriter() != null && !StringUtils.isEmpty(star.getAsWriter())) {
                            asAArray = star.getAsWriter().split(",");
                            if (asAArray != null && !Arrays.asList(asAArray).contains(mediaId)) {
                                String[] asAArrayNew = new String[asAArray.length + 1];
                                System.arraycopy(asAArray, 0, asAArrayNew, 0, asAArray.length);//将a数组内容复制新数组b
                                asAArrayNew[asAArrayNew.length - 1] = mediaId;
                                star.setAsWriter(StringUtils.join(asAArrayNew, ","));
                                star.setAsWriterNumber(asAArrayNew.length);

                            }
                        } else {
                            //asactor空的情况
                            star.setAsWriter(mediaId);
                            star.setAsWriterNumber(1);

                        }
                        star.setUpdateDate(new Date());
                        //starService.save(star);

                        //star是地址引用，故若已添加，不需再次添加
                        if (!needUpdateStarList.contains(star)) {
                            needUpdateStarList.add(star);
                        }

                    } else {
                        //new
                        star = new Star();
                        star.setCreateDate(new Date());
                        star.setDouBanNo(douBanNo);
                        star.setAsWriterNumber(1);
                        star.setAsWriter(mediaId);
                        star.setName(person.getName());
                        star.setNameExtend(person.getNameExtend());
                        star.setPerson(person);

                        //新建star保存
                        starService.save(star);


                        starSavedList.add(star);

                        //加入，防止重复加入
                        //starDouBanNoAllList.add(douBanNo);

                    }


                    //210223ADD Media存放StarIds
                    String starId = String.valueOf(star.getId());
                    if (media.getWriter() != null && !StringUtils.isEmpty(media.getWriter())) {
                        String[] asDArray;
                        asDArray = media.getWriter().split(",");
                        if (asDArray != null && !Arrays.asList(asDArray).contains(starId)) {
                            String[] asDArrayNew = new String[asDArray.length + 1];
                            System.arraycopy(asDArray, 0, asDArrayNew, 0, asDArray.length);//将a数组内容复制新数组b
                            asDArrayNew[asDArrayNew.length - 1] = mediaId;
                            media.setWriter(StringUtils.join(asDArrayNew, ","));
                        }
                    } else {
                        //Director空的情况
                        media.setWriter(starId);
                    }
                    //media是地址引用，故若已添加，不需再次添加
                    if (!needUpdateMediaList.contains(media)) {
                        needUpdateMediaList.add(media);
                    }


                }

            }



        }


        System.out.println("----------mediaAllList:::"+mediaList.size());

        //3 批量更新
        int size  = needUpdateMediaList.size();
        System.out.println("----------needUpdateMediaList:::"+size);
        for (int i=0; i<size; i++){
            Media media = needUpdateMediaList.get(i);
            entityManager.merge(media);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = needUpdateStarList.size();
        System.out.println("----------needUpdateStarList:::"+size);
        for (int i=0; i<size; i++){
            Star star = needUpdateStarList.get(i);
            entityManager.merge(star);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = filmNotFindMediaList.size();
        System.out.println("----------filmNotFindMediaList:::"+size);

        size  = starSavedList.size();
        System.out.println("----------starSavedList:::"+size);

        size  = personNotFindDoubanUrlList.size();
        System.out.println("----------personNotFindStarList:::"+size);


        QStar star = QStar.star;
        ind = 1;
        mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(1));
        for(Media media : mediaList){
            String mediaId = String.valueOf(media.getId());
            runningLog  ="已删除的Media" +ind+"、"+media.getName();
            System.out.println("-----"+ind+"------deleted-------"+media.getNameChn());

            ind++;

          //2）删除的media从series里去除
            QSeries series = QSeries.series;
            List<Series> seriesList = (List<Series>) seriesRepository.findAll(series.asMedias.contains(mediaId));
            for (Series series1 : seriesList){
                String asOld = series1.getAsMedias();
                String[] asOldArray = asOld.split(",");
                List<String> asOldList = new ArrayList<String>(Arrays.asList(asOldArray));
                if (asOldList.contains(mediaId)) {
                    asOldList.remove(mediaId);  // ok
                    series1.setAsMediaNumber(asOldList.size());
                    series1.setAsMedias(StringUtils.join(asOldList.toArray(), ","));
                    series1.setUpdateDate(new Date());
                    seriesRepository.save(series1);
                }
            }

        }



        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");


       runningLog =  "";

       String[] doubanListArray = personNotFindDoubanUrlList.toArray(new String[personNotFindDoubanUrlList.size()]);
       String doubanListString = StringUtils.join(doubanListArray,"\n");
       return new ResultBean<>(doubanListString);




    }


    /**
     * 根据media条目信息去film表找一一对应的film，找不到返回null
     * @param media
     * @return
     */
    Film findConnectedFilmForMedia(Media media){

        /**1）namechn和year完全匹配
         * 1.1）size=1 over
         * 1.2）size>1 nameEng contain subjectMain or subjectOther over
         * 1.3）none nameEng contain subjectMain and nameCHN contain subjectOther
         * 1.3.1）size=1 over
         * 1.3.2) over
         *
         */
        QFilm qFilm = QFilm.film;
        Predicate[] predicateArray = new Predicate[2];
        predicateArray[0] = qFilm.subject.trim().eq(media.getNameChn().trim()).and(qFilm.year.eq(media.getYear()));
        predicateArray[1] = (qFilm.subjectOther.trim().contains(media.getNameChn().trim()).or(qFilm.subjectOther.trim().contains(media.getNameEng().trim())).or(qFilm.subject.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));

        List<Film> films = (List<Film>) filmRepository.findAll(predicateArray[0]);
        if(films.size()== 1){
            return films.get(0);
        }else if(films.size()>1){
            Film f = null;
            for(Film film : films){
                String nameEng = media.getNameEng();
                String nameChn = media.getNameChn();
                if (nameChn!=null && film.getSubject().contains(nameChn)){
                    f =  film;
                    break;
                }

                if(nameEng!=null && (film.getSubjectMain().indexOf(nameEng)>0 || film.getSubjectOther().indexOf(nameEng)>0)){
                    f = film;
                    break;
                }

            }
            return f;
        }else {
            Film f = null;
            films = (List<Film>) filmRepository.findAll(predicateArray[1]);
            if(films.size()==1){
                return films.get(0);
            }else if(films.size()>1){
                for(Film film : films){
                    String nameEng = media.getNameEng();
                    String nameChn = media.getNameChn();
                    if (nameChn!=null && film.getSubject().contains(nameChn)){
                        f =  film;
                        break;
                    }

                    if(nameEng!=null && film.getSubjectMain().indexOf(nameEng)>0 ){
                        f = film;
                        break;
                    }

                }
                return f;
            }

            return f;
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


    }


    private class Tool {
    }
}
