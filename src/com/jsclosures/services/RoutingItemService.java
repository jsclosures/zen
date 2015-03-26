package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 * DataBean tmp = new DataBean();
        try{
	        Connection conn = ConnectionManager.getConnection(context,"default");
	
	        tmp.setValue("id", conn != null ? "ben-jamin" : "bad");
        }
        catch(Exception e){
        	tmp.setValue("id",e.getMessage());
        }

        resultList.add(tmp);
 * @author admin
 *
 */
public class RoutingItemService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"parentid","STRING"},
                                            {"mode","STRING"},
                                            {"className","STRING"},
                                            {"target","STRING"},
                                            {"required","STRING"},
                                            {"sequence","STRING"},
                                            {"contenttype","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "ROUTINGITEM";
    public static String CONTENTTYPEPREFIX = "RTI";
    
    public RoutingItemService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
        setHasParent(true);
    }
    
    public static void main(String[] args) {
    	RoutingItemService gs = new RoutingItemService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
