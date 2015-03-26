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
public class SolrService implements RestImplService {

    public SolrService(){
        //placeHolder
    }
    private String fieldList[][] = {{"id","NUMBER"},
                                            {"contenttype","STRING"},
                                            {"last_modified","DATE"}};
    private String contentType = "CONTENT";
    private String contentTypePrefix = "CT";
    private boolean hasParent = false;
    private String sortOn = "";
    
    public void setHasParent(boolean hasParent){
        this.hasParent = hasParent;
    }
    public void setFieldList(String fieldList[][]){
        this.fieldList = fieldList;
    }
    
    public void setContentType(String contentType){
        this.contentType = contentType;
    }
    
    public void setContentTypePrefix(String contentTypePrefix){
        this.contentTypePrefix = contentTypePrefix;
    }
    
    public void setSort(String sort){
        this.sortOn = sort;
    }
    
    public DataBean getData(RestService context, HttpServletRequest req,DataBean args) {
    	
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
        for(int i = 0;i < fieldList.length;i++)
        {
          columNameList.add(fieldList[i][0]);
        }
        
        columNameList.add("value");

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype",contentType);
        
        
        
        SolrHelper.readSortArguments(context, req,solrArgs);
        
        if( !solrArgs.isValid("sort") && sortOn != null && sortOn.length() > 0 ){
            solrArgs.setValue("sort",sortOn);
        }
        
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
        DataBean tCache = SolrHelper.querySolr(server, params, fieldList);
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
        
        for(int i = 0;i < fieldList.length;i++)
        {
          columNameList.add(fieldList[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        
        DataBean solrTmp = new DataBean();
        for(int i = 0;i < fieldList.length;i++)
        {
            if( fieldList[i][0].equalsIgnoreCase("id") ){
                solrTmp.setValue("id",SolrHelper.hashIDWithPrefix(contentTypePrefix)); 
            }
            else if( fieldList[i][0].equalsIgnoreCase("contenttype") ){
                solrTmp.setValue("contenttype",contentType); 
            }
            else if( fieldList[i][0].equalsIgnoreCase("last_modified") ){
                solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 
            }
            else {
                solrTmp.setValue(fieldList[i][0],args.getString(fieldList[i][0]));  
            }
        }

        resultList.add(solrTmp);
        
        HttpSolrServer solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,fieldList,resultList);
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
        
        for(int i = 0;i < fieldList.length;i++)
        {
          columNameList.add(fieldList[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        context.writeLog(1,"solr update document id: " + args.getValue("id"));
        
        DataBean solrTmp = new DataBean();
        for(int i = 0;i < fieldList.length;i++)
        {
            if( fieldList[i][0].equalsIgnoreCase("contenttype") ){
                solrTmp.setValue("contenttype",contentType); 
            }
            else if( fieldList[i][0].equalsIgnoreCase("last_modified") ){
                solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 
            }
            else {
                solrTmp.setValue(fieldList[i][0],args.getString(fieldList[i][0]));  
            }
        }

        resultList.add(solrTmp);
        
        HttpSolrServer solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);
        
        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);

        solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,fieldList,resultList);
        SolrHelper.commitToSolr(solrServer);
        //SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr update result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr update attribute result: " + solrAttributeResult.getString("error"));
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
        
        for(int i = 0;i < fieldList.length;i++)
        {
          columNameList.add(fieldList[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
    
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",args.getValue("id"));  

        resultList.add(solrTmp);
        
        HttpSolrServer solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);
        
        if( hasParent ){
            SolrHelper.removeDocumentsFromSolrByQuery(solrServer,"parentid:" + args.getString("id"));
            
        }
        SolrHelper.commitToSolr(solrServer);
       // SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr delete attribute result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr delete attribute result: " + solrAttributeResult.getString("error"));
       
        result.setValue("totalCount",1);
        result.setValue("status",0);
        
        

        return (result);
    }

    public static void main(String[] args) {
    	SolrService gs = new SolrService();
        DataBean foo = gs.getData(null, null,new DataBean());
        System.out.println(foo);
    }
}
