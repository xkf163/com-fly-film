package com.fly.common.query.util;


import com.fly.common.base.pojo.PageInfo;
import com.fly.common.exception.QueryException;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.pojo.QueryDefinition;
import com.fly.common.utils.SpringContextUtil;
import com.fly.common.utils.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by billJiang on 2017/1/18.
 * e-mail:jrn1012@petrochina.com.cn qq:475572229
 * query查询的工具类
 */
public class QueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);

    /**
     * 根据类名获取类
     *
     * @param className 类名
     * @return 类
     * @throws ClassNotFoundException 找不到类异常
     */
    public static Class<?> getClassName(String className) throws ClassNotFoundException {
        Class<?> objClass = null;
        if (!StrUtil.isEmpty(className)) {
            objClass = Class.forName(className);
        }
        return objClass;

    }

    /**
     * 根据queryCondition获取query
     *
     * @param queryCondition 查询条件
     * @return 查询参数
     * @throws QueryException xml配置不存在 queryId为空异常
     */
    public static Query getQuery(QueryCondition queryCondition) throws QueryException {
        if (queryCondition.getQuery() != null)
            return queryCondition.getQuery();
        String queryId = queryCondition.getQueryId();
        if (!StrUtil.isEmpty(queryId)) {

            Query query = QueryDefinition.getQueryById(queryId);
            if (query == null) {
                throw new QueryException("queryId为【" + queryId + "】的xml配置不存在");
            } else {
                return query;
            }
        } else {
            throw new QueryException("queryId为空，请指定queryId!");
        }
    }



    /*
     * 获取分页信息
     *
     * @param queryCondition 查询条件
     * @param query          查询配置
     * @return 分页信息
     */
    public static PageInfo getPageInfo(QueryCondition queryCondition) {
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {

        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        return pageInfo;
    }

    /**
     * 通过接口方式查询，获取数据列表
     *
     * @param queryCondition 查询参数
     * @param query          查询配置
     * @param pageInfo       分页信息
     * @return 数据列表
     * @throws QueryException service和method不存在异常，以及method内部异常
     */
    public static List queryByService(QueryCondition queryCondition, Query query, PageInfo pageInfo) throws QueryException {
        Object service = SpringContextUtil.getBean(query.getService());
        if (service == null) {
            throw new QueryException("service为【" + query.getService() + "】的接口不存在");
        }
        Class clazz = service.getClass();
        if (StrUtil.isEmpty(query.getMethod())) {
            throw new QueryException("method为【" + query.getMethod() + "】不存在");
        }
        try {
            Method method = clazz.getDeclaredMethod(query.getMethod(), QueryCondition.class, PageInfo.class);
            return (List) method.invoke(service, queryCondition, pageInfo);
        } catch (Exception ex) {
            throw new QueryException("【" + query.getService() + "】接口的【" + query.getMethod() + "】方法调用异常");
        }
    }





    /**
     * 获取排序配置,使用ID替换key 解决排序问题
     *
     * @param condition 查询条件（界面）
     * @param query     查询配置
     * @return 排序
     */
    private static String getSortInfo(QueryCondition condition, Query query) {
        String sortInfo = !StrUtil.isEmpty(condition.getSortInfo()) ? condition.getSortInfo() : query.getOrder();
        if (StrUtil.isEmpty(sortInfo))
            return sortInfo;
        String[] arr = sortInfo.split(",");
        for (String str : arr) {
            String[] keyArr = str.split(" ");
            String key = keyArr[0].trim();
            String id = getColumnIdByKey(query, key);
            if (!StrUtil.isEmpty(id))
                sortInfo = sortInfo.replace(key, id);
        }
        return sortInfo;
    }

    private static String getColumnIdByKey(Query query, String key) {
        for (Column column : query.getColumnList()) {
            if (column.getKey().equals(key)) {
                return column.getId();
            }
        }
        return key;
    }

    /**
     * 获取是否需要当前1=1的条件注入private
     *
     * @param map 前端值
     * @return 是否需要作为条件注入1=1
     */
    private static boolean getIsCondition(Map map) {
        boolean isCondition = true;
        if (map.get("isCondition") != null)
            isCondition = Boolean.valueOf(map.get("isCondition").toString());
        return isCondition;
    }

    /**
     * 获取查询操作符
     *
     * @param map    前台参数
     * @param column 列配置
     * @return 操作符
     */
    private static String getOperatorStr(Map map, Column column) {
        if (map.get("operator") != null && !StrUtil.isEmpty(map.get("operator").toString())) {
            return map.get("operator").toString();
        }
        return column.getOperator();
    }


    /**
     * 查询值是否为空
     *
     * @param value 值
     * @return 是否为空
     */
    private static boolean isNotEmptyValue(Object value) {
        if (value == null)
            return false;
        if (StrUtil.isEmpty(value.toString())) {
            return false;
        }
        if (value.toString().equals("%%") || value.toString().equals("%") || value.toString().equals(","))
            return false;
        return true;
    }




    /**
     * 获取服务器端注入的查询条件
     *
     * @param query 查询配置
     * @return 服务器端拼接的查询条件
     */
    private static List<Map<String, Object>> getServerConditions(Query query) {
        List<Column> collist = query.getColumnList();
        List<Map<String, Object>> serverConditions = new ArrayList<>();
        for (Column column : collist) {
            //控制是否是服务器端查询条件的开关
            if (!column.getIsServerCondition()) {
                continue;
            }
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("value", column.getValue());
            conditionMap.put("operator", column.getOperator());
            conditionMap.put("key", column.getKey());
            serverConditions.add(conditionMap);
        }
        return serverConditions;
    }

    /**
     * 处理变量条件@#
     *
     * @param sqlBuilder sql
     * @param key        键
     * @param value      值
     * @param column     列配置
     * @return 替换为值后的sql
     */
    private static StringBuilder processVariable(StringBuilder sqlBuilder, String key, Object value, Column column) {
        String sql = sqlBuilder.toString();
        if (sql.contains("@" + key + "#")) {
            if (value instanceof String) {
                if (column.getIsQuote()) {
                    sql = sql.replaceAll("@" + key + "#", "'" + value + "'");
                } else {
                    sql = sql.replaceAll("@" + key + "#", value.toString());
                }
            } else if (value instanceof Integer) {
                sql = sql.replaceAll("@" + key + "#", value.toString());
            } else if (value instanceof ArrayList) {
                String str = getJoinList((List) value, column);
                sql = sql.replaceAll("@" + key + "#", str);
            }
        } else {
            return sqlBuilder;
        }
        sqlBuilder.delete(0, sqlBuilder.toString().length()).append(sql);
        return sqlBuilder;
    }

    /**
     * 处理集合类型数据 处理成类似’abc','ba'的数据
     *
     * @param list   数据集合
     * @param column 列配置
     * @return 处理后的数据
     */
    private static String getJoinList(List list, Column column) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            if (obj == null) {
                continue;
            }
            if (obj instanceof String) {
                if (column.getIsQuote())
                    sb.append("'").append(obj.toString()).append("'").append(",");
                else
                    sb.append(obj.toString()).append(",");
            }
            if (obj instanceof Integer)
                sb.append(obj.toString()).append(",");
        }
        String str = sb.toString();
        if (sb.length() > 0)
            str = sb.substring(0, sb.length() - 1);
        return str;
    }




}
