package com.fly.service.impl;

import com.fly.dao.FilmRepository;
import com.fly.dao.MediaRepository;
import com.fly.entity.*;
import com.fly.pojo.Relevance;
import com.fly.service.PersonService;
import com.fly.service.RelevanceService;
import com.fly.service.StarService;
import com.querydsl.core.types.Predicate;
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
    StarService starService;

    @Autowired
    PersonService personService;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 为Media关联Film
     */
    @Override
    @Transactional
    public void relevantFilmForMedia(Relevance relevance) {

        List<Media> filmNotFindMediaList = new ArrayList<>();
        List<Media> needUpdateMediaList = new ArrayList<>();
        List<Star> needUpdateStarList = new ArrayList<>(); //需要更新的star
        List<Star> starSavedList = new ArrayList<>();
        //Map<String,String> starNeedSaveMap = new HashMap<>(); //filmId 和 star doubanNo

        String relevantAll = relevance.getRelevantAll();
        //提取所有符合条件的media条目
        QMedia qMedia = QMedia.media;
        List<Media> mediaList;
        if("1".equals(relevantAll)){
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(0));
        }else{
            //只处理未关联film的Media
            mediaList = (List<Media>) mediaRepository.findAll(qMedia.deleted.eq(0).and(qMedia.film.isNull()));
        }

        List<String> starDouBanNoList = starService.findAllDouBanNo();

        System.out.println(mediaList.size());

        for(Media media : mediaList){
            //1)为Media关联Film，并加入更新List
            Film film = findConnectedFilmForMedia(media);

            media.setFilm(film);
            media.setUpdateDate(new Date());

            if(film == null){
                filmNotFindMediaList.add(media);
                continue;
            }
            needUpdateMediaList.add(media);
            //2）为当前匹配到的Film中的导演和演员Person转化为Star（或更新Star）
            String filmId = String.valueOf(media.getId());
            String directorsDoubanNo = film.getDirectors().trim();
            String actorsDoubanNo = film.getActors().trim();
            String writerDoubanNo = film.getScreenWriter().trim();
            //director doubanid array
            String[] ddno_array=null,adno_array=null,sdno_array=null;
            if(directorsDoubanNo!=null)
                ddno_array = directorsDoubanNo.split(",");
            if(actorsDoubanNo!=null)
                adno_array = actorsDoubanNo.split(",");
            if(writerDoubanNo!=null)
                sdno_array = writerDoubanNo.split(",");

            //Film中的导演doubanNo
            for(String douBanNo : ddno_array){
                //找star表，看是否存在，不存在则新建，存在即asdirect加上此filmid（先判断有无此filmid）
                if(starDouBanNoList.contains(douBanNo)){
                    Star star = starService.findByDouBanNo(douBanNo);
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
                        }
                    }else {
                        //asdirector空的情况
                        star.setAsDirector(filmId);
                        star.setAsDirectorNumber(1);
                    }
                    star.setUpdateDate(new Date());
                    //star是地址引用，故若已添加，不需再次添加
                    if (!needUpdateStarList.contains(star) ) {
                        needUpdateStarList.add(star);
                    }

                }else{

                    //starNeedSaveMap.put(douBanNo,filmId);

                    //根据Person信息创建new star
                    Person person =personService.findByDouBanNo(douBanNo);
                    if(person == null){
                        System.out.println("-----------Person is not find-----------"+douBanNo);
                        continue;
                    }
                    Star star = new Star();
                    star.setCreateDate(new Date());
                    star.setDouBanNo(douBanNo);
                    star.setAsDirectorNumber(1);
                    star.setAsDirector(filmId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);

                    //新建star保存
                    starService.save(star);

                    starSavedList.add(star);
                    starDouBanNoList = starService.findAllDouBanNo();

                }

            }

            //as主演
            for(String douBanNo : adno_array){

                if(starDouBanNoList.contains(douBanNo)){
                    Star star =  starService.findByDouBanNo(douBanNo);
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

                        }
                    }  else {
                        //asactor空的情况
                        star.setAsActor(filmId);
                        star.setAsActorNumber(1);

                    }
                    star.setUpdateDate(new Date());
                    //star是地址引用，故若已添加，不需再次添加
                    if (!needUpdateStarList.contains(star) ) {
                        needUpdateStarList.add(star);
                    }

                }else{
                    //new
                    Person person = personService.findByDouBanNo(douBanNo);
                    if(person==null){
                        System.out.println("-----------Person is not find-----------"+douBanNo);
                        continue;
                    }
                    //new
                    Star star = new Star();
                    star.setCreateDate(new Date());
                    star.setDouBanNo(douBanNo);
                    star.setAsActorNumber(1);
                    star.setAsActor(filmId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);

                    //新建star保存
                    starService.save(star);
                    starSavedList.add(star);
                    starDouBanNoList = starService.findAllDouBanNo();

                }
            }

            //as编剧
            for(String douBanNo : sdno_array){

                if(starDouBanNoList.contains(douBanNo)){
                    Star star =  starService.findByDouBanNo(douBanNo);
                    //判断当前filmid是否已存在当前star的asdirect字段中
                    //不存在add进去，并更新number
                    String[] asAArray = null;
                    if(star.getAsWriter()!=null){
                        asAArray= star.getAsWriter().split(",");
                        if(asAArray!=null && !Arrays.asList(asAArray).contains(filmId)) {
                            String[] asAArrayNew = new String[asAArray.length + 1];
                            System.arraycopy(asAArray, 0, asAArrayNew, 0, asAArray.length);//将a数组内容复制新数组b
                            asAArrayNew[asAArrayNew.length - 1] = filmId;
                            star.setAsWriter(StringUtils.join(asAArrayNew, ","));
                            star.setAsWriterNumber(asAArrayNew.length);

                        }
                    }  else {
                        //asactor空的情况
                        star.setAsWriter(filmId);
                        star.setAsWriterNumber(1);

                    }
                    star.setUpdateDate(new Date());
                    //star是地址引用，故若已添加，不需再次添加
                    if (!needUpdateStarList.contains(star) ) {
                        needUpdateStarList.add(star);
                    }

                }else{
                    //new
                    Person person = personService.findByDouBanNo(douBanNo);
                    if(person==null){
                        System.out.println("-----------Person is not find-----------"+douBanNo);
                        continue;
                    }
                    //new
                    Star star = new Star();
                    star.setCreateDate(new Date());
                    star.setDouBanNo(douBanNo);
                    star.setAsWriterNumber(1);
                    star.setAsWriter(filmId);
                    star.setName(person.getName());
                    star.setNameExtend(person.getNameExtend());
                    star.setPerson(person);

                    //新建star保存
                    starService.save(star);
                    starSavedList.add(star);
                    starDouBanNoList = starService.findAllDouBanNo();

                }
            }



        }


        //3 批量更新
        int size  = needUpdateMediaList.size();
        System.out.println("------needUpdateMediaList:::"+size);
        for (int i=0; i<size; i++){
            Media media = needUpdateMediaList.get(i);
            entityManager.merge(media);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = needUpdateStarList.size();
        System.out.println("-------needUpdateStarList:::"+size);
        for (int i=0; i<size; i++){
            Star star = needUpdateStarList.get(i);
            entityManager.merge(star);
            if(i % 50 == 0 || i==size-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
        size  = filmNotFindMediaList.size();
        System.out.println("------filmNotFindMediaList:::"+size);

        size  = starSavedList.size();
        System.out.println("------starSavedList:::"+size);

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
        predicateArray[1] = (qFilm.subjectOther.trim().contains(media.getNameChn().trim()).or(qFilm.subjectOther.trim().contains(media.getNameEng().trim())).or(qFilm.subjectMain.contains(media.getNameChn().trim()))).and(qFilm.year.eq(media.getYear()));

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
            films = (List<Film>) filmRepository.findAll(predicateArray[1]);
            if(films.size()==1){
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

                    if(nameEng!=null && film.getSubjectMain().indexOf(nameEng)>0 ){
                        f = film;
                        break;
                    }

                }
                return f;
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


}
