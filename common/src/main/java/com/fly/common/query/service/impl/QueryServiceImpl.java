package com.fly.common.query.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.service.QueryService;
import com.fly.common.query.util.QueryUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("queryService")
public class QueryServiceImpl implements QueryService {

    @Override
    public Map<String, Object> loadData(String reqObj) throws Exception {
        //用于接收返回数据(配置、分页、数据)
        Map<String, Object> map = new HashMap<>();


        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);


        //获取Query配置
        Query query = QueryUtil.getQuery(queryCondition);
           /*
        //获取所属的类
        Class<?> objClass = QueryUtil.getClassName(query.getClassName());
        // 分页信息
        PageInfo pageInfo = QueryUtil.getPageInfo(queryCondition, query);
        //返回数据
        List objList = getDataList(queryCondition, query, pageInfo, objClass, true);

        //table自定义方法，以后有需要的话可放开
        //List<Call> callList = getCallList(query);
        //query.setCallList(callList);
        //String userid= SecurityUtils.getSubject().getSession().getAttribute("userId").toString();
        //query=QueryUtil.getQueryCustomColumns(query,this.getSelectedColumns(query.getId(),queryCondition.getPageName(),userid));
        map.put("query", query);
        map.put("pageInfo", pageInfo);
        map.put("rows", objList);

         */
        map = null;
        return map;
    }


    public List getDataList() {

        List objList = null;

        /*
        //使用反射的接口查询，一般用于非常复杂的查询
        if (!query.getSimpleSearch()) {
            objList = QueryUtil.queryByService(queryCondition, query, pageInfo);
        }
        // sql 查询方式 (1=1 方式传值,同时支持变量传值@#),可映射到类
        else if (StrUtil.isNotBlank(query.getSql())) {
            Map<String, Object> params = QueryUtil.getSqlParams(queryCondition, query);
            String sql = params.get("sql").toString();
            Object[] objArrs = (Object[]) params.get("objArr");
            Type[] typeArrs = (Type[]) params.get("typeArr");
            if (query.getAllowPaging() && isQuery)
                objList = this.findMapBySql(sql, query.getCountStr(), pageInfo, objArrs, typeArrs, objClass);
            else
                objList = this.findMapBySql(sql, objArrs, typeArrs, objClass);
        }
        //var sql 查询方式 (非1=1方式传值 变量替换@#方式)，可映射到类
        else if (StrUtil.isNotBlank(query.getVarSql())) {
            StringBuilder sqlBuilder = new StringBuilder(query.getVarSql());
            QueryUtil.generateFilter(queryCondition, query, sqlBuilder);
            String sql = sqlBuilder.toString();
            if (query.getAllowPaging() && isQuery)
                objList = this.findMapBySql(sql, query.getCountStr(), pageInfo, new Object[]{}, new Type[]{}, objClass);
            else
                objList = this.findMapBySql(sql, new Object[]{}, new Type[]{}, objClass);
        }
        // criteria 离线查询方式
        else if (objClass != null) {
            DetachedCriteria criteria = QueryUtil.generateCriteria(queryCondition, query, objClass);
            pageInfo.setCount(this.getCountByCriteria(criteria));
            if (query.getAllowPaging() && isQuery)
                objList = this.getListByCriteria(criteria, pageInfo);
            else
                objList = this.findByCriteria(criteria);
        }

        */

        return objList;
    }


}
