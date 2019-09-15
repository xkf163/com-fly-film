package com.fly.scanner.service.impl;

import com.fly.dao.MediaRepository;
import com.fly.entity.Media;
import com.fly.entity.QMedia;
import com.fly.scanner.entity.Scanner;
import com.fly.scanner.service.ScannerService;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 15:57 2017/10/31
 */
@Service
public class ScannerServiceImpl implements ScannerService {

    private static final String[] folderKeys  = new String[] {"720P","1080P","1080I","DTS","CMCT","TLF","CHD","HDS","HDSKY","FRDS","EPIC","HDAREA","ATOMS","TRUEHD","X264","X265","DVDRIP"};
    private static final String[] filePostfix = new String[]{"MKV","TS","AVI"};

    @Autowired
    MediaRepository mediaRepository;

    @PersistenceContext
    EntityManager entityManager;

    private List<Media> oldMediaEntriesList ;
    private List<Media> newMediaEntriesList ;
    private List<Media> addFailedMediaEntriesList ;
    private Map<String,Media> oldMediaEntriesMap ;


    /**
     * 扫描影音文件夹
     * 入口
     * @param fileDir
     * @return
     */
    @Override
    @Transactional
    @Modifying
    public Object[] scanMedia2DB(File fileDir , Scanner scanner) {

        oldMediaEntriesList = new ArrayList<>();
        newMediaEntriesList = new ArrayList<>();
        addFailedMediaEntriesList = new ArrayList<>();

        oldMediaEntriesMap = new HashMap<>();
        /*
         * step1:获取选择的路径：如j:\201702\1.txt 或者 j:\;拆分出盘符及文件夹
         */
        String rootFullPath = fileDir.getAbsolutePath();
        String rootDisk = rootFullPath.substring(0,rootFullPath.indexOf(":"));
        String rootFolder =  rootFullPath.substring(rootFullPath.indexOf(":")+1,rootFullPath.length());
        System.out.println(rootDisk +","+ rootFolder);
        /*
         * step2: 根据这两个关键字去搜索数据库现有的条目，并把deleted删除标记位赋值为1，即认为是删除的
         */
        QMedia media = QMedia.media;
        Predicate predicate = media.diskNo.eq(rootDisk).and(media.fullPath.like(rootFolder+"%")).and(media.deleted.eq(0));
        oldMediaEntriesList = (List<Media>) mediaRepository.findAll(predicate);

        //查询语句动态准备
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(predicate);
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        oldMediaEntriesList = queryFactory.selectFrom(media)
                                .where(criteria.toArray(new Predicate[criteria.size()]))
                                .fetch();

        for (Media media1 : oldMediaEntriesList){
            oldMediaEntriesMap.put(media1.getFullPath(),media1);
        }

        gatherMedia2DBing(fileDir, scanner);

         /*
          *  step3: 剩余oldmediaList里的数据都是失效，deleted标记1
         */
        System.out.println("oldMediaEntries: "+oldMediaEntriesList.size());
        int size =  oldMediaEntriesList.size();
        for (int i = 0; i < size; i++) {
            Media media1  = oldMediaEntriesList.get(i);
            media1.setDeleted(1);
            media1.setUpdateDate(new Date());
            mediaRepository.save(media1);
//            entityManager.persist(media1);
//            if (i % 50 == 0 || i==(size-1)) { // 每1000条数据执行一次，或者最后不足1000条时执行
//                entityManager.flush();
//                entityManager.clear();
//            }
        }


        /*
         * step4:新加的newmedialist里保存到数据库中
         */
        System.out.println("newMediaEntries: "+newMediaEntriesList.size());
        size =  newMediaEntriesList.size();
        for (int i = 0; i < size; i++) {
            Media media1  = newMediaEntriesList.get(i);
            mediaRepository.save(media1);
//            entityManager.persist(media1);
//            if (i % 50 == 0 || i==(size-1)) { // 每1000条数据执行一次，或者最后不足1000条时执行
//                entityManager.flush();
//                entityManager.clear();
//            }
        }


        return new Object[]{oldMediaEntriesList,newMediaEntriesList,addFailedMediaEntriesList};
    }


    /**
     * 收集medias主方法
     * @param fileDir
     * @return
     */
    public void gatherMedia2DBing(File fileDir,Scanner scanner) {

        File[] mediaFiles = fileDir.listFiles();
        System.out.println(fileDir+" 该目录文件数量:"+mediaFiles.length);
        for(File f:mediaFiles){
            if( f.isDirectory() && !f.isHidden()){
                if(folderFilter(f.getName())){
                    System.out.println("是多媒体文件夹："+f.getName());
                    deal(f, scanner,true);
                }else{
                    System.out.println("不是多媒体文件夹："+f.getName());
                    gatherMedia2DBing(f,scanner);
                }
            }else if(f.isFile()){
                if( fileFilter(f.getName()) && !f.isHidden() ){
                    System.out.println("        是文件："+f.getName());
                    deal(f, scanner,false);
                }
            }
        }

    }


    public void deal(File f,Scanner scanner,boolean folder) {

        String fullPathAll = f.getAbsolutePath();
        String fullPath = fullPathAll.substring(fullPathAll.indexOf(":")+1,fullPathAll.length());
        String diskNo = fullPathAll.substring(0,fullPathAll.indexOf(":"));

        if (oldMediaEntriesMap.containsKey(fullPath)){
            //说明已存在，oldmediaenties 去除该mediaobj，newmediaenties 无需add
            oldMediaEntriesList.remove(oldMediaEntriesMap.get(fullPath));
        }else{
            Media m = new Media();

            FileTime fileTime= null;
            Calendar cd = Calendar.getInstance();
            try {
                fileTime = Files.readAttributes(Paths.get(fullPathAll), BasicFileAttributes.class).creationTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
            m.setFullPath(fullPath);
            m.setDiskNo(diskNo);
            m.setName(fullPath.lastIndexOf("\\")>0?fullPath.substring(fullPath.lastIndexOf("\\")+1,fullPath.length()):fullPath.substring(1,fullPath.length()));

            m.setMediaSize(calcFolderSize(f));
            m.setMediaSizeGB(calc(calcFolderSize(f)));
            //盘名
            String diskName = scanner.getDiskName();
            m.setDiskName(diskName);

            String pcName = scanner.getPcName();
            m.setPcName(pcName);

            //f的最后修改时间
            cd.setTimeInMillis(f.lastModified());
            m.setModifiedDate(cd.getTime());
            //f的创建（下载）资源时间
            cd.setTimeInMillis(fileTime.toMillis());
            m.setGatherDate(cd.getTime());

            m.setCreateDate(new Date());

            m.setWhetherFolder(folder?1:0);
            m.setWhetherAlive(1);
            m.setWhetherTransfer(1);
            m.setDeleted(0);


//          mediaName="2001太空漫游.1968.A.Space.Odyssey.720p.MNHD-FRDS";
            //正则表达式提取年代
            Boolean finded  = false;
            String year = "1900";
            String mediaName = m.getName();
            Pattern pattern = Pattern.compile("\\.([1-2][9|0][0-9]{2})(\\.[B|H])");
            Matcher matcher = pattern.matcher(mediaName);
            if (matcher.find()){
                year = matcher.group(1);
                finded = true;
            }else{
                pattern = Pattern.compile("\\.([1-2][9|0][0-9]{2})(\\.)");
                matcher = pattern.matcher(mediaName);
                if (matcher.find()){finded = true;year = matcher.group(1);}
            }

            m.setYear(Short.parseShort(year));
            m.setNameChn(mediaName.substring(0,mediaName.indexOf(".")));
            if(finded && mediaName.lastIndexOf(year)-1>mediaName.indexOf(".")+1)
                m.setNameEng(mediaName.substring(mediaName.indexOf(".")+1,mediaName.lastIndexOf(year)-1).replaceAll("\\."," "));

            if(m.getNameChn()==null || m.getNameEng()==null || m.getYear()==null){
                addFailedMediaEntriesList.add(m);
                return;
            }

            newMediaEntriesList.add(m);
        }
    }



    /*
* 判断文件是否是多媒体文件，根据后缀来匹配
*/
    public boolean fileFilter(String fileName){
        boolean flag = false;
        String[] fileNameArr = fileName.toUpperCase().split("\\.");
        String CurPostfix = fileNameArr[fileNameArr.length-1];
        for(String k:filePostfix){
            if	(CurPostfix.indexOf(k)>-1) {
                flag = true;
                break;
            }
        }
        return flag;

    }

    /**
     * 判断文件夹是否是存放多媒体文件夹
     */
    public boolean folderFilter(String folderName){
        boolean flag = false;
        folderName=folderName.toUpperCase();
        for(String k:folderKeys){
            if	(folderName.indexOf(k)>0) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 计算文件（夹）大小，以字节为单位
     */
    public long calcFolderSize(File file){
        if(file.exists()){
            if(file.isDirectory()){
                long tol = 0;
                File[] fs = file.listFiles();
                for(File f:fs){
                    tol+=calcFolderSize(f);
                }
                return tol;
            }else{
                return	file.length();
            }
        }
        return 0;
    }

    /**
     * 转化字节大小为GB单位
     * @param size
     * @return
     */
    public Float calc(long size) {

        //KB
        size = size / 1024;
        //MB
        size = size / 1024;


            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return Float.parseFloat(((size / 100)) + "." + String.valueOf((size % 100)) );

    }


}
