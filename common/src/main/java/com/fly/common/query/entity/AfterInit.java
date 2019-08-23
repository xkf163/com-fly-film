package com.fly.common.query.entity;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

@ObjectCreate(pattern="queryContext/query/afterInit")
public class AfterInit {	
	List<AfterCall> afterCallList;
	public AfterInit(){
		afterCallList=new ArrayList<AfterCall>();
	}
	@SetNext
	public void addAfterCall(AfterCall call){
		afterCallList.add(call);
	}
	public List<AfterCall> getAfterCallList() {
		return afterCallList;
	}
	
	
}
