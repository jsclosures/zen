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
public class BoardService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"boardtitle","STRING"},
        {"boardbody","STRING"},
        {"boardbackground","STRING"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
     public static String CONTENTTYPE = "BOARD";
    public static String CONTENTTYPEPREFIX = "BR";
    
    public BoardService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }

    public static void main(String[] args) {
    	BoardService gs = new BoardService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
