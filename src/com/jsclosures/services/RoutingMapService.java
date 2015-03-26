package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.params.ModifiableSolrParams;

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
public class RoutingMapService implements RestImplService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"name","STRING"},
                                            {"action","STRING"},
                                            {"target","STRING"},
                                            {"contenttype","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "ROUTING";
     
    public DataBean getData(RestService context, HttpServletRequest req,DataBean args) {
    	
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        columNameList.add("value");

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype",CONTENTTYPE);
        
        SolrHelper.readSortArguments(context, req,solrArgs);
        SolrHelper.readPagingArguments(context,req, solrArgs);
        SolrHelper.readQueryArguments(context, args,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        HttpSolrServer server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, FIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
        result.setValue("resultcount", tCache.getString("numFound"));

        ArrayList entryList = tCache.getCollection("entrylist");
        if (entryList != null)
        {
            resultList = entryList;
            
            if( resultList.size() > 0 ){
                DataBean cArgs = new DataBean();
                cArgs.setValue("contenttype",RoutingItemService.CONTENTTYPE);
                cArgs.setValue("parentid",((DataBean)resultList.get(0)).getValue("id"));
                
                DataBean tChildCache = new RoutingItemService().getData(context,req,cArgs);
                
                if( tChildCache.getCollection("beanlist") != null ){
                    ((DataBean)resultList.get(0)).setCollection("routing",tChildCache.getCollection("beanlist"));
                }
            }
        }
        
        result.setValue("totalCount", tCache.getValue("numFound"));
        
        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    public DataBean postData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();


        return (result);
    }
    
    public DataBean putData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        

        return (result);
    }
    
    
    public DataBean deleteData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        

        return (result);
    }

    public static void main(String[] args) {
    	RoutingMapService gs = new RoutingMapService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
