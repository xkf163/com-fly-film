package com.fly.service;

import com.fly.entity.TreeNode;

import java.util.Set;

public interface TreeService {

    //作废
    TreeNode getTreeGroupByGatherDate();

    //收集年月node下的子nodes
    Set<TreeNode> getSonTreeGroupByGatherDate();

    //废弃
    TreeNode getTreeGroupByMovieReleaseYear();

    //发行年代node下的子nodes
    Set<TreeNode> getSonTreeGroupByMovieReleaseYear();


    //影人作品
    Set<TreeNode> getSonTreeGroupByStar();


    //操作平台
    Set<TreeNode> getTreeForAdmin();



}
