package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 * @author admin
 *
 */
public class BoardItemService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"parentid","STRING"},
        {"itemtype","STRING"},
        {"iteminfo","STRING"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
     public static String CONTENTTYPE = "BOARDITEM";
    public static String CONTENTTYPEPREFIX = "BRI";
    
    public BoardItemService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
        setHasParent(true);
        setSort("sequence asc");
    }

    public static void main(String[] args) {
    	BoardItemService gs = new BoardItemService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
