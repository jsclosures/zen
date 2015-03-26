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
 *
 * @author admin
 *
 */
public class AuthService implements RestImplService {

    public static String CONTENTTYPE = "AUTH";
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"authkey","STRING"},
        {"authname","STRING"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
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
        }
        
        result.setValue("totalCount", tCache.getValue("numFound"));
        
        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    public DataBean postData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("AU"));  
        solrTmp.setValue("contenttype",CONTENTTYPE);
        
        solrTmp.setValue("username",args.getString("username"));  
        solrTmp.setValue("userkey",args.getString("userkey"));  
        solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 

        resultList.add(solrTmp);
        
        HttpSolrServer solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
        SolrHelper.commitToSolr(solrServer);
        //SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr insert attribute result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr insert attribute result: " + solrAttributeResult.getString("error"));
        result.setValue("totalCount",1);
        
        

        return (result);
    }
    
    public DataBean putData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        result.setValue("totalCount",1);
        result.setValue("status",0);
        
        

        return (result);
    }
    
    
    public DataBean deleteData(RestService context, HttpServletRequest req,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        result.setValue("totalCount",1);
        result.setValue("status",0);

        return (result);
    }

    public static void main(String[] args) {
    	AuthService gs = new AuthService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
