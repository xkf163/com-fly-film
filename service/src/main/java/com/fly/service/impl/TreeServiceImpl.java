package com.fly.service.impl;


import com.fly.entity.QMedia;
import com.fly.entity.TreeNode;
import com.fly.service.TreeService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TreeServiceImpl implements TreeService {

    @PersistenceContext
    EntityManager entityManager;

    //废弃
    @Override
    public TreeNode getTreeGroupByGatherDate() {
        //树1：收集年月------------s------------------
        TreeNode pTreeNode,treeNode = null;
        Set<TreeNode> treeNodeSet =  new LinkedHashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        QMedia media = QMedia.media;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Date> gatherDateList = queryFactory
                .selectFrom(media)
                .select(media.gatherDate)
                .fetch();
        //去重
        List<Date> gatherDateListNew = new ArrayList<>(new HashSet<Date>(gatherDateList));
        Set<String> gatherDateSet = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(Integer.parseInt(o1) < Integer.parseInt(o2)){
                    return 1;
                }else if(Integer.parseInt(o1) == Integer.parseInt(o2)) {
                    return 0;
                }
                return -1;
            }
        });

        for (Date date : gatherDateListNew){
            gatherDateSet.add(sdf.format(date));
        }

        //准备展开后的树
        Iterator<String> iterator = gatherDateSet.iterator();
        while (iterator.hasNext()){
            String yearMonth = iterator.next();
            treeNode = new TreeNode(yearMonth, "/pages/table_mediaVO?toolbarType=toolbar_queryWithoutDate&dataUrl=/mediavo/gatherdate/"+yearMonth,"/mediavo/gatherdate/"+yearMonth);
            treeNodeSet.add(treeNode);
        }
        pTreeNode = new TreeNode("010000","收集年月", "closed", "/pages/table_mediaVO?dataUrl=/mediavo/gatherdates", "/mediavo/gatherdates" , treeNodeSet);
        //pTreeNode.setIconCls("icon-wenjianjia-colse");
        return pTreeNode;
    }


    @Override
    public Set<TreeNode> getSonTreeGroupByGatherDate() {
        //树1：收集年月------------s------------------
        TreeNode treeNode;
        Set<TreeNode> treeNodeSet =  new LinkedHashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        QMedia media = QMedia.media;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Date> gatherDateList = queryFactory
                .selectFrom(media)
                .select(media.gatherDate)
                .fetch();
        //去重
        List<Date> gatherDateListNew = new ArrayList<>(new HashSet<Date>(gatherDateList));
        Set<String> gatherDateSet = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(Integer.parseInt(o1) < Integer.parseInt(o2)){
                    return 1;
                }else if(Integer.parseInt(o1) == Integer.parseInt(o2)) {
                    return 0;
                }
                return -1;
            }
        });

        for (Date date : gatherDateListNew){
            gatherDateSet.add(sdf.format(date));
        }

        //准备展开后的树
        Iterator<String> iterator = gatherDateSet.iterator();
        while (iterator.hasNext()){
            String yearMonth = iterator.next();
            treeNode = new TreeNode(yearMonth, "/pages/table_mediaVO?toolbarType=toolbar_queryWithoutDate&dataUrl=/mediavo/gatherdate/"+yearMonth,"/mediavo/gatherdate/"+yearMonth);
            treeNodeSet.add(treeNode);
        }
        return treeNodeSet;
    }


    @Override
    public Set<TreeNode> getSonTreeGroupByMovieReleaseYear() {
        TreeNode treeNode;
        Set<TreeNode> treeNodeSet;
        QMedia media = QMedia.media;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        //树2：影片年代------------s------------------
        List<Short>  list = queryFactory
                .selectFrom(media)
                .select(media.year)
                .distinct()
                .orderBy(media.year.desc())
                .fetch();
        treeNodeSet = new LinkedHashSet<>();
        for(Short year : list){
            treeNode = new TreeNode(String.valueOf(year), "/pages/table_mediaVO?dataUrl=/mediavo/years/"+String.valueOf(year),"/mediavo/years/"+String.valueOf(year));
            treeNodeSet.add(treeNode);
        }

        return treeNodeSet;
    }


    //废弃
    @Override
    public TreeNode getTreeGroupByMovieReleaseYear() {
        TreeNode pTreeNode,treeNode = null;
        Set<TreeNode> treeNodeSet =  new LinkedHashSet<>();
        QMedia media = QMedia.media;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        //树2：影片年代------------s------------------
        List<Short>  list = queryFactory
                .selectFrom(media)
                .select(media.year)
                .distinct()
                .orderBy(media.year.desc())
                .fetch();
        treeNodeSet = new LinkedHashSet<>();
        for(Short year : list){
            treeNode = new TreeNode(String.valueOf(year), "/pages/table_mediaVO?dataUrl=/mediavo/years/"+String.valueOf(year),"/mediavo/years/"+String.valueOf(year));
            treeNodeSet.add(treeNode);
        }
        pTreeNode = new TreeNode("020000","影片年代", "closed", "/pages/table_mediaVO?dataUrl=/mediavo/years", "/mediavo/years", treeNodeSet);

        return pTreeNode;
    }


    @Override
    public Set<TreeNode> getSonTreeGroupByStar() {
        Set<TreeNode> treeNodeSet =  new LinkedHashSet<>();
        TreeNode treeNode = new TreeNode();
        treeNode = new TreeNode();
        treeNode.setText("导演列表");
        treeNode.setDataUrl("/person/directors");
        treeNode.setHtmlUrl("/pages/table_personVO?toolbarType=toolbar_person&dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        treeNode = new TreeNode();
        treeNode.setText("演员列表");
        treeNode.setDataUrl("/person/actors");
        treeNode.setHtmlUrl("/pages/table_personVO?toolbarType=toolbar_person&dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        return treeNodeSet;
    }

    @Override
    public Set<TreeNode> getTreeForAdmin() {
        Set<TreeNode> treeNodeSet =  new LinkedHashSet<>();
        TreeNode treeNode = new TreeNode();
        treeNode.setText("已有关联");
        treeNode.setDataUrl("/filmvo/years/");
        treeNode.setHtmlUrl("/pages/table_filmVO?dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        treeNode = new TreeNode();
        treeNode.setText("未有关联");
        treeNode.setDataUrl("/mediavo/unrelation/");
        treeNode.setHtmlUrl("/pages/table_mediaVO?dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        treeNode = new TreeNode();
        treeNode.setText("重复下载");
        treeNode.setDataUrl("/mediavo/repetitive/");
        treeNode.setHtmlUrl("/pages/table_mediaVO?toolbarType=toolbar_buttonWithqueryWithoutDate&dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        treeNode = new TreeNode();
        treeNode.setText("增删改查");
        treeNode.setDataUrl("/mediavo/gatherdates");
        treeNode.setHtmlUrl("/pages/table_mediaVO?toolbarType=toolbar_add&dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        treeNode = new TreeNode();
        treeNode.setText("回收站");
        treeNode.setDataUrl("/mediavo/deleted/1");
        treeNode.setHtmlUrl("/pages/table_mediaVO?toolbarType=toolbar_recycle&dataUrl="+treeNode.getDataUrl());
        treeNodeSet.add(treeNode);

        return treeNodeSet;
    }
}
