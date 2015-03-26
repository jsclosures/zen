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
public class RoutingService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"name","STRING"},
                                            {"action","STRING"},
                                            {"target","STRING"},
                                            {"contenttype","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "ROUTING";
    public static String CONTENTTYPEPREFIX = "RT";
    
    public RoutingService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
   

    public static void main(String[] args) {
    	RoutingService gs = new RoutingService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
