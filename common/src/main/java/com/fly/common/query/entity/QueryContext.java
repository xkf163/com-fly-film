package com.fly.common.query.entity;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

@ObjectCreate(pattern="queryContext")
public class QueryContext { 
	private int cacheSize;
	
	private List<Query> queries;
	public QueryContext() {
		cacheSize = 20;
		queries = new ArrayList();
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
    
	@SetNext
	public void addQuery(Query query) {
		this.queries.add(query);
	}
    
	public List getQueries() {
		return this.queries;
	} 

}