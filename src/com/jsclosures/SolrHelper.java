package com.jsclosures;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

/**
utility methods to write to the solr server

<field name="username" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="userkey" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="authname" type="string" indexed="true" stored="true" multiValued="false"/>
<field name="authkey" type="string" indexed="true" stored="true" multiValued="false"/>

<field name="contenttitle" type="text_general" indexed="true" stored="true" multiValued="false"/>
<field name="contentbody" type="text_general" indexed="true" stored="true" multiValued="false"/>
<field name="contentall" type="text_general" indexed="true" stored="true" multiValued="false"/>

<field name="contentid" type="string" indexed="true" stored="true" multiValued="false"/>
 */
public class SolrHelper
{ 
     public static String MAPFIELDLIST[][] = {{"id","NUMBER"},
         {"mapkey","STRING"},
         {"mapvalue","STRING"},
         {"mapfield","STRING"},
         {"contenttype","STRING"},
         {"source","STRING"}};
     
    public static String SHARDFIELDLIST[][] = {{"id","NUMBER"},
        {"shardname","STRING"},
        {"shardurl","STRING"},
        {"shardsource","STRING"},
        {"contenttype","STRING"}};
    
    public static String SOURCEFIELDLIST[][] = {{"id","NUMBER"},
        {"sourcename","STRING"},
        {"sourcekey","STRING"},
        {"contenttype","STRING"}};
     /**
	 *get a instance of a solr server  reutrns null if unable to create instance
	 * @param url
	 * @param timeOut
	 * @return
	 */
	public static HttpSolrServer getSolrServer(String url, int timeOut)
	{
		HttpSolrServer solr = null;

		try
		{ //"http://localhost:8080/solr/"
			solr = new HttpSolrServer(url);
			solr.setConnectionTimeout(timeOut);
		}
		catch (Exception e)
		{
			//add logging here
		}

		return (solr);
	}
 /**
	 *Commit to a  solr server
	 * @param solr
	 * @return
	 */
	public static DataBean commitToSolr(HttpSolrServer solr)
	{
		DataBean result = new DataBean();
		try
		{
			solr.commit();
		}
		catch (Exception e)
		{
			result.setValue("error", e.toString());
		}

		return (result);
	}
  /**
    *Optimize to a  solr server
    * @param solr
    * @return
    */
   public static DataBean optimizeToSolr(HttpSolrServer solr)
   {
     DataBean result = new DataBean();
     try
     {
       solr.optimize();
     }
     catch (Exception e)
     {
       result.setValue("error", e.toString());
     }

     return (result);
   }
 /**
	 *Add a list of data beans to the solr server using the fields as a extracted list of attributes
	 * @param solr
	 * @param dataBeanList
	 * @param fieldList
	 * @return
	 */
	public static DataBean addDocumentsToSolr(HttpSolrServer solr, String[][] fieldList, ArrayList dataBeanList)
	{
		DataBean result = new DataBean();

		try
		{
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();


			SolrInputDocument doc;
			DataBean tEntry;
			String tStr;
			for (int i = 0; i < dataBeanList.size(); i++)
			{
				tEntry = (DataBean) dataBeanList.get(i);
				tEntry.setValue("id", tEntry.getString("id") + i);

				doc = new SolrInputDocument();

				for (int j = 0; j < fieldList.length; j++)
				{
					tStr = fieldList[j][0];
					if( tEntry.isValid(tStr) || !fieldList[j][1].equalsIgnoreCase("DATE") )
						doc.addField(tStr, tEntry.getString(tStr));
				}

				docs.add(doc);
			}

			solr.add(docs);
		}
		catch (Exception e)
		{
			result.setValue("error", e.toString());
		}

		return (result);
	}
	
	public static DataBean addDocumentsToSolr(HttpSolrServer solr, ArrayList fieldList, ArrayList<DataBean> dataBeanList)
	{
		DataBean result = new DataBean();

		try
		{
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();


			SolrInputDocument doc;
			DataBean tEntry;
			String tStr;
			for (int i = 0; i < dataBeanList.size(); i++)
			{
				tEntry = (DataBean) dataBeanList.get(i);
				//tEntry.setValue("id", tEntry.getString("id") + i);

				doc = new SolrInputDocument();

				for (int j = 0; j < fieldList.size(); j++)
				{
					tStr = fieldList.get(j).toString();

					doc.addField(tStr, tEntry.getString(tStr));
				}

				docs.add(doc);
			}

			solr.add(docs);
		}
		catch (Exception e)
		{
			result.setValue("error", e.toString());
		}

		return (result);
	}
        
        
    public static DataBean updateDocumentsToSolr(HttpSolrServer solr, ArrayList fieldList, ArrayList<DataBean> dataBeanList)
    {
            DataBean result = new DataBean();

            try
            {
                    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();


                    SolrInputDocument doc;
                    DataBean tEntry;
                    String tStr;
                    for (int i = 0; i < dataBeanList.size(); i++)
                    {
                            tEntry = (DataBean) dataBeanList.get(i);
                            //tEntry.setValue("id", tEntry.getString("id") + i);

                            doc = new SolrInputDocument();

                            for (int j = 0; j < fieldList.size(); j++)
                            {
                                tStr = fieldList.get(j).toString();
                                if( !tStr.equalsIgnoreCase("id") ) {
                                    HashMap<String, Object> value = new HashMap<String, Object>(); 
                                    value.put("set",tEntry.getString(tStr)); 

                                    doc.addField(tStr, value);
                                }
                                else {
                                    doc.addField(tStr, tEntry.getString(tStr));
                                }
                            }

                            docs.add(doc);
                    }

                    solr.add(docs);
            }
            catch (Exception e)
            {
                    result.setValue("error", e.toString());
            }

            return (result);
    }
    
    public static DataBean updateDocumentsToSolr(HttpSolrServer solr, String[][] fieldList, ArrayList dataBeanList)
    {
            DataBean result = new DataBean();

            try
            {
                    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();


                    SolrInputDocument doc;
                    DataBean tEntry;
                    String tStr;
                    for (int i = 0; i < dataBeanList.size(); i++)
                    {
                            tEntry = (DataBean) dataBeanList.get(i);
                            tEntry.setValue("id", tEntry.getString("id") + i);

                            doc = new SolrInputDocument();

                            for (int j = 0; j < fieldList.length; j++)
                            {
                                tStr = fieldList[j][0];
                                if( !tStr.equalsIgnoreCase("id") ) {
                                    HashMap<String, Object> value = new HashMap<String, Object>(); 
                                    value.put("set",tEntry.getString(tStr)); 

                                    doc.addField(tStr, value);
                                }
                                else {
                                    doc.addField(tStr, tEntry.getString(tStr));
                                }
                            }

                            docs.add(doc);
                    }

                    solr.add(docs);
            }
            catch (Exception e)
            {
                    result.setValue("error", e.toString());
            }

            return (result);
    }    
        
    public static DataBean removeDocumentsFromSolr(HttpSolrServer solr, ArrayList dataBeanList)
    {
            DataBean result = new DataBean();

            try
            {
                    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();


                    SolrInputDocument doc;
                    DataBean tEntry;
                    String tStr;
                    for (int i = 0; i < dataBeanList.size(); i++)
                    {
                            tEntry = (DataBean) dataBeanList.get(i);
                            solr.deleteById(tEntry.getString("id"));
                    }

                    
            }
            catch (Exception e)
            {
                    result.setValue("error", e.toString());
            }

            return (result);
    
    }
    
    public static DataBean removeDocumentsFromSolrByQuery(HttpSolrServer solr,String query)
    {
            DataBean result = new DataBean();

            try
            {
                    solr.deleteByQuery(query);
                    
            }
            catch (Exception e)
            {
                    result.setValue("error", e.toString());
            }

            return (result);
    
    }
    
  public static ModifiableSolrParams getQueryParametersFromURLArguments(DataBean args)
  {
    ModifiableSolrParams params = new ModifiableSolrParams();
    
    HashMap values = args.getValues();
    Enumeration keys = new IteratorEnumeration(values.keySet().iterator());;
    String tStr;
    
    while(keys.hasMoreElements())
    {
         tStr = keys.nextElement().toString();
        params.set(tStr,args.getString(tStr));
    }
    
    return( params );
  }
  /**
	 *create the default set of query parameters to the solr server
	 * @return
	 */
  public static ModifiableSolrParams getQueryParameters()
  {
    ModifiableSolrParams params = new ModifiableSolrParams();
    
    
    return( params );
  }
  /**
	 *query the solr server and return a list of data beans
	 * @param solr
	 * @param params
	 * @param fieldList
	 * @return
	 */
  public static DataBean querySolr(HttpSolrServer solr,ModifiableSolrParams params, String[][] fieldList)
  {
    DataBean result = new DataBean();
    try
    {
      QueryResponse response = solr.query(params);
         
      SolrDocumentList docList = response.getResults();
      int resultSize = docList.size();
         result.setValue("numFound",docList.getNumFound());
        
         
      SolrDocument tEntry;
         DataBean dataBean;
         String tStr;
      for(int i = 0;i < resultSize;i++)
      {
          tEntry = docList.get(i);
           dataBean = new DataBean();
          for (int j = 0; j < fieldList.length; j++)
          {
            tStr = fieldList[j][0];
              if( tEntry.getFieldValue(tStr) != null )
            dataBean.setValue(tStr,String.valueOf(tEntry.getFieldValue(tStr)).trim());
          }
          result.addToCollection("entrylist", dataBean);
      }
    }
    catch (Exception e)
    {
      result.setValue("error", e.toString());
    }

    return (result);
  }
  
  
    public static DataBean querySolr(RestService context,HttpSolrServer solr,DataBean args, ArrayList fieldList,PrintWriter out)
    {
      DataBean result = new DataBean();
      try
      {
          if( !args.isValid("start") )
                  args.setValue("start",0);
          
          if( !args.isValid("rows") )
                  args.setValue("rows",1000);
           
          SolrHelper.readQueryArguments(context, args,args);
          
          int batchSize = args.getInt("rows");
          long totalFound = 0;
          
          for(long totalRead = 0,start = 0;;){
              ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(args);
              
              QueryResponse response = solr.query(params);
               
              SolrDocumentList docList = response.getResults();
              int resultSize = docList.size();
              
              if( start == 0 )
                 totalFound = docList.getNumFound();
              
            
              totalRead += resultSize;
              
               
              writeOutSolrQuery(docList,fieldList,out);
              
              if( totalRead == totalFound ){
                  break;
              }
              
              start += batchSize;
              args.setValue("start",start);
          }
          
          
          result.setValue("numFound",String.valueOf(totalFound));
      }
      catch (Exception e)
      {
        result.setValue("error", e.toString());
        System.out.println("solr stream query error: " + e.toString());
      }

      return (result);
    }
  
    public static void writeOutSolrQuery(SolrDocumentList docList, ArrayList fieldList,PrintWriter out)
    {
      try
      {
          
        int resultSize = docList.size();
          
        System.out.println("solr stream query num found: " + docList.getNumFound());   
        SolrDocument tEntry;
           DataBean dataBean;
           String tStr;
        for(int i = 0;i < resultSize;i++)
        {
            tEntry = docList.get(i);
             dataBean = new DataBean();
            for (int j = 0; j < fieldList.size(); j++)
            {
                tStr = fieldList.get(j).toString();
                if( tEntry.getFieldValue(tStr) != null )
                    dataBean.setValue(tStr,String.valueOf(tEntry.getFieldValue(tStr)).trim());
            }
            out.print(Helper.toCSVLine(dataBean, fieldList, true));
            //result.addToCollection("entrylist", dataBean);
           // System.out.println("solr stream query found obj: " + dataBean.toString()); 
        }
      }
      catch (Exception e)
      {
        System.out.println("solr stream query error: " + e.toString());
      }

    }
    
  
  public static DataBean querySolrFacet(HttpSolrServer solr,ModifiableSolrParams params)
  {
    DataBean result = new DataBean();
    try
    {
      QueryResponse response = solr.query(params);
      
      DataBean dataBean;
      long counter = 0;
      
      List<FacetField> fflist = response.getFacetFields();
      for(FacetField ff : fflist){
          String ffname = ff.getName();
          int ffcount = ff.getValueCount();
          List<Count> counts = ff.getValues();
          for(Count c : counts){
              String facetLabel = c.getName();
              long facetCount = c.getCount();
              dataBean = new DataBean();
              dataBean.setValue("id",counter++);
              dataBean.setValue("name",facetLabel);
              dataBean.setValue("count",facetCount);
              
              result.addToCollection("entrylist", dataBean);
          }
      }
    }
    catch (Exception e)
    {
      result.setValue("error", e.toString());
    }

    return (result);
  }
  
  
    /**
     *  use the solr.select.url provider parameter to set the default location
     *  of the solr data
     * @param context
     * @return
     */
    public static String getDefaultDataSourceURL(RestService context)
    {
         com.jsclosures.Configuration conf = context.getConfiguration();
         
         String dataSourceURL = conf != null ? conf.getString("solr","http://localhost/solr") : "http://localhost/solr";
            
            return( dataSourceURL );
    }
    /**
     *use the solr.select.shards provider parameter to include the default
     * version=2.2
     * indent=0
     * wt=xml
     * @param context
     * @return
     */
    public static DataBean getDefaultArguments(RestService context)
    {
         DataBean args = new DataBean();
         args.setValue("version","2.2");
         args.setValue("indent","0");
         args.setValue("wt","json");
            
         String shards = context.getConfiguration().getString("solr.select.shards","");
         
            if( shards.length() > 0 )
                 args.setValue("shards",shards);
            
            return( args );
    }
    /**
     *partially match from the beginning of a parameter name
     * @param context
     * @param name
     * @return
     */
    public static String matchParameterName(RestService context,HttpServletRequest req,String name) {
      String result = null;
      
        Enumeration pList = req.getParameterNames();
        String tKey;
        
        while( pList.hasMoreElements() ) {
                tKey = pList.nextElement().toString();
                if( tKey.toUpperCase().startsWith(name.toUpperCase()) ) {
                     result = tKey;
                     break;
                }
      }
      return( result );
    }
    /**
     *read the sort order set by the ui control for sorting of results
     * it looks for the pattern  sort(<columnname>[+-])
     * @param context
     * @param args
     */
    public static void readSortArguments(RestService context,HttpServletRequest req,DataBean args)
    {
         String sortParameterName = matchParameterName(context,req,"sort(");
         context.writeLog(1,"sortParameterName: " + sortParameterName);
         if( sortParameterName != null ) {
             String sortStr = sortParameterName;
             String direction = sortStr.substring("sort(".length(),"sort(".length()+1);
           String fieldName = sortStr.substring("sort(".length()+1);
           fieldName = fieldName.substring(0,fieldName.indexOf(")"));
           
           
           String sortValue =  fieldName + " ";
           
           if( direction.equals("-") )
             sortValue = sortValue + "desc";
          else
           sortValue = sortValue + "asc";
           
           context.writeLog(1,"sort value: " + sortValue);
           
           args.setValue("sort",sortValue);
         }
        else if( args.isValid("sort") ){
            args.setValue("sort",args.getString("sort"));
        }
    }
    /**
     *Read the paging (start,count) variables from the request
     * for dojo this in the http header as the "Range" header.
     * @param context
     * @param args
     */
    public static void readPagingArguments(RestService context,HttpServletRequest req,DataBean args)
    {
        String start = req.getParameter("start");
        if( start == null || start.length() == 0 )
            start = "0";
        
        String rows = req.getParameter("rows");
        if( rows == null || rows.length() == 0 )
            rows = "25";
        
         String range = req.getHeader("Range");
         context.writeLog(1,"range: " + req.getHeader("Range"));
            
         if( range != null && range.length() > 0 && range.indexOf("items=") == 0 )
         {
              //items=0-24
              String subRange = range.substring("items=".length());
              
              context.writeLog(1,"subrange: " + subRange);
              int idx = subRange.indexOf("-");
              
              if( idx > -1 )
              {
                   start = subRange.substring(0,idx);
                   rows = subRange.substring(idx+1);
              }
         }
            
       context.writeLog(1,"paging: " + start + " " + rows );
       args.setValue("start",start);
       args.setValue("rows",rows);
    }
    /**
     *Read all the different query fields out of the request.
     * @param context
     * @param args
     */
    public static void readQueryArguments(RestService context,DataBean queryArgs,DataBean solrArgs)
    {
         String queryStr = "*:*";
         
         String name = queryArgs.getString("name","");
         boolean hasData = false;
         if( name.length() > 0 )
         {
              queryStr = "name:('" + name.replaceAll(" "," AND ") + "')";
              hasData = true;
         }
         
         String nameStr = queryArgs.getString("namestr","");
         
         if( nameStr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND namestr:(\"" + nameStr + "\")";
              else
                   queryStr = "namestr:(\"" + nameStr + "\")";
              
              hasData = true;
         }
         
        String id = queryArgs.getString("id","");
        
        if( id.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND id:(\"" + id + "\")";
             else
                  queryStr = "id:(\"" + id + "\")";
             
             hasData = true;
        }
         
         String parentID = queryArgs.getString("parentid","");
         
         if( parentID.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND parentid:(\"" + parentID + "\")";
              else
                   queryStr = "parentid:(\"" + parentID + "\")";
              
              hasData = true;
         }
         
         String attributevaluestr = queryArgs.getString("attributevaluestr","");
         //org.apache.solr.client.solrj.util.ClientUtils.escapeQueryChars(
         if( attributevaluestr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND attributevaluestr:(\"" + attributevaluestr + "\")";
              else
                   queryStr = "attributevaluestr:(\"" + attributevaluestr + "\")";
              
              hasData = true;
         }
         
         String attributenamestr = queryArgs.getString("attributenamestr","");
         //org.apache.solr.client.solrj.util.ClientUtils.escapeQueryChars(
         if( attributenamestr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND attributenamestr:(\"" + attributenamestr + "\")";
              else
                   queryStr = "attributenamestr:(\"" + attributenamestr + "\")";
              
              hasData = true;
         }
         
         String modelStr = queryArgs.getString("modelstr","");
         
         if( modelStr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND modelstr:(\"" + modelStr + "\")";
              else
                   queryStr = "modelstr:(\"" + modelStr + "\")";
              
              hasData = true;
         }
         
         String brandStr = queryArgs.getString("brandstr","");
         
         if( brandStr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND brandstr:(\"" + brandStr + "\")";
              else
                   queryStr = "brandstr:(\"" + brandStr + "\")";
              
              hasData = true;
         }
         
         String manufacturerStr = queryArgs.getString("manufacturerstr","");
         
         if( manufacturerStr.length() > 0 )
         {
              if( hasData )
                 queryStr = queryStr + " AND manufacturerstr:(\"" + manufacturerStr + "\")";
              else
                   queryStr = "manufacturerstr:(\"" + manufacturerStr + "\")";
              
              hasData = true;
         }
         
        String category = queryArgs.getString("category","");
           
           if( category.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND category:('" + category + "')";
                else
                     queryStr = "category:('" + category + "')";
                
                hasData = true;
           }
           
        String model = queryArgs.getString("model","");
           
           if( model.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND model:('" + model.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "model:('" + model.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
        String manufacturer = queryArgs.getString("manufacturer","");
           
           if( manufacturer.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND manufacturer:('" + manufacturer.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "manufacturer:('" + manufacturer.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
        String contenttype = queryArgs.getString("contenttype","");
           
           if( contenttype.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND contenttype:(\"" + contenttype.toUpperCase() + "\")";
                else
                     queryStr = "contenttype:(\"" + contenttype.toUpperCase() + "\")";
                
                hasData = true;
           }
           
        String source = queryArgs.getString("source","");
           
           if( source.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND source:(" + source.toUpperCase().replaceAll("~~~"," OR ") + ")";
                else
                     queryStr = "source:(" + source.toUpperCase().replaceAll("~~~"," OR ") + ")";
                
                hasData = true;
           }
           
           String brand = queryArgs.getString("brand","");
           
           if( brand.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND brand:('" + brand.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "brand:('" + brand.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String externalid = queryArgs.getString("externalid","");
           
           if( externalid.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND externalid:('" + externalid + "')";
                else
                     queryStr = "externalid:('" + externalid + "')";
                
                hasData = true;
           }
           
           String sourceid = queryArgs.getString("sourceid","");
           
           if( sourceid.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND sourceid:('" + sourceid + "')";
                else
                     queryStr = "sourceid:('" + sourceid + "')";
                
                hasData = true;
           }
           
           String attributevalue = queryArgs.getString("attributevalue","");
           
           if( attributevalue.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND attributevalue:('" + attributevalue.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "attributevalue:('" + attributevalue.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String memory = queryArgs.getString("memory","");
           
           if( memory.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND memory:('" + memory.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "memory:('" + memory.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String processortype = queryArgs.getString("processortype","");
           
           if( processortype.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND processortype:('" + processortype.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "processortype:('" + processortype.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String processorspeed = queryArgs.getString("processorspeed","");
           
           if( processorspeed.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND processorspeed:('" + processorspeed.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "processorspeed:('" + processorspeed.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String operatingsystem = queryArgs.getString("operatingsystem","");
           
           if( operatingsystem.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND operatingsystem:('" + operatingsystem.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "operatingsystem:('" + operatingsystem.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String harddrivecapacity = queryArgs.getString("harddrivecapacity","");
           
           if( harddrivecapacity.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND harddrivecapacity:('" + harddrivecapacity.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "harddrivecapacity:('" + harddrivecapacity.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String mapField = queryArgs.getString("mapfield","");
           
           if( mapField.length() > 0 )
           {
        	   if( hasData )
                   queryStr = queryStr + " AND mapfield:(\"" + mapField + "\")";
                else
                     queryStr = "mapfield:(\"" + mapField + "\")";
                
                hasData = true;
           }
           
           String mapKey = queryArgs.getString("mapkey","");
           
           if( mapKey.length() > 0 )
           {
        	   if( hasData )
                   queryStr = queryStr + " AND mapkey:(\"" + mapKey + "\")";
                else
                     queryStr = "mapkey:(\"" + mapKey + "\")";
                
                hasData = true;
           }
           
           String attributename = queryArgs.getString("attributename","");
           
           if( attributename.length() > 0 )
           {
                if( hasData )
                   queryStr = queryStr + " AND attributename:('" + attributename.replaceAll(" "," AND ") + "')";
                else
                     queryStr = "attributename:('" + attributename.replaceAll(" "," AND ") + "')";
                
                hasData = true;
           }
           
           String price = queryArgs.getString("price","");
           
           if( queryArgs.isValid("price") )
           {
                if( hasData )
                     queryStr = queryStr + " AND price:[" + queryArgs.getFloat("price") + " TO *]";
                else
                     queryStr = "price:[" + queryArgs.getFloat("price") + " TO *]";
                
                hasData = true;
           }
           
            
            if( queryArgs.isValid("startdate") )
            {
                String startDate = queryArgs.getString("startdate","");
                
                 if( hasData )
                      queryStr = queryStr + " AND last_modified:[" + startDate + " TO *]";
                 else
                      queryStr = "last_modified:[" + startDate + " TO *]";
                 
                 hasData = true;
            }
            
        String username = queryArgs.getString("username","");
        
        if( username.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND username:(\"" + username + "\")";
             else
                  queryStr = "username:(\"" + username + "\")";
             
             hasData = true;
        }
        
        String userkey = queryArgs.getString("userkey","");
        
        if( userkey.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND userkey:(\"" + userkey + "\")";
             else
                  queryStr = "userkey:(\"" + userkey + "\")";
             
             hasData = true;
        }
        
        String authname = queryArgs.getString("authname","");
        
        if( authname.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND authname:(\"" + authname + "\")";
             else
                  queryStr = "authname:(\"" + authname + "\")";
             
             hasData = true;
        }
        
        String authkey = queryArgs.getString("authkey","");
        
        if( authkey.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND authkey:(\"" + authkey + "\")";
             else
                  queryStr = "userkey:(\"" + authkey + "\")";
             
             hasData = true;
        }
        
        String action = queryArgs.getString("action","");
        
        if( action.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND action:(\"" + action + "\")";
             else
                  queryStr = "action:(\"" + action + "\")";
             
             hasData = true;
        }
        
        String target = queryArgs.getString("target","");
        
        if( target.length() > 0 )
        {
             if( hasData )
                queryStr = queryStr + " AND target:(\"" + target + "\")";
             else
                  queryStr = "target:(\"" + target + "\")";
             
             hasData = true;
        }
        
        String location = queryArgs.getString("location");
        
        if( location.length() > 0 ){
            String ds = queryArgs.getString("distance","5");
            
            String fq = "{!geofilt pt=" + location + " sfield=location d=" + ds + "}";
            
            solrArgs.setValue("fq",fq);
        }
           
           
           String filter = queryArgs.getString("filter","");
           
           if( filter.length() > 0 )
           {
        	   ArrayList filterList = Helper.splitFields(filter, "~~~");
        	   
        	   if( filterList.size() > 0 ){
        		   StringBuffer filterStr = new StringBuffer("(");
        		   
        		   for(int i = 0,size = filterList.size();i < size;i++){
        			   if( i > 0 )
        				   filterStr.append(" OR ");
        			   
        			   filterStr.append("namestr:\"");
        			   filterStr.append(filterList.get(i).toString());
        			   filterStr.append("\"");
        		   }
        		   
        		   filterStr.append(")");
        		   
	        	   if( hasData )
	                   queryStr = queryStr + " AND " + filterStr;
	                else
	                     queryStr = filterStr.toString();
	                
	                hasData = true;
        	   }
           }
           
           
     
           context.writeLog(1,"query str: " + queryStr);
         
           solrArgs.setValue("q",queryStr);
    }
    
    public static String hashID(String value){
    	long hash = 0;
        for (int i = 0; i < value.length(); i++) {
          hash = 32 * hash + value.charAt(i);
        }
       
        return( String.valueOf(hash) );
    }
    
    
    public static String hashIDWithPrefix(String prefix){
    	long hash = 0;
    	String value = String.valueOf(Calendar.getInstance().getTime().getTime());
    	
        for (int i = 0; i < value.length(); i++) {
          hash = 32 * hash + value.charAt(i);
        }
       
        return( prefix + String.valueOf(hash) );
    }
    
    public static String getTimestamp(){
        return( getTimestamp(java.util.Calendar.getInstance().getTime()) );
    }
    
    public static String getTimestamp(java.util.Date date){
    	String result = "";
    	
    	java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        
    	result = out.format(date);

        return( result );
    }
    
    
    public static String mapField(RestService context,DataBean args){
    	String result = args.getString("mapvalue");
    	
    	String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype","MAP");
        args.setValue("source",args.getValue("source"));
        args.setValue("mapkey",args.getValue("mapkey"));
        args.setValue("mapfield",args.getValue("mapfield"));
         
        SolrHelper.readQueryArguments(context, args,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        params.set("rows","1");
        HttpSolrServer server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, SolrHelper.MAPFIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));

        ArrayList<DataBean> entryList = (ArrayList<DataBean>)tCache.getCollection("entrylist");
        
        if( entryList != null && entryList.size() > 0 ){
        	result = entryList.get(0).getString("mapvalue");
        }
        
    	return( result );
    }
    
    
    public static DataBean getSourceList(RestService context,DataBean args){
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype","SOURCE");
          
        SolrHelper.readQueryArguments(context, args,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        params.set("rows","100000");
        HttpSolrServer server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, SolrHelper.SOURCEFIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
        
        return( tCache );
    }
    
    public static DataBean getShardList(RestService context,DataBean args){
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype","SHARD");
          
        SolrHelper.readQueryArguments(context, args,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        params.set("rows","100000");
        HttpSolrServer server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, SolrHelper.SHARDFIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
        
        return( tCache );
    }
    
    /**
     *Read all the different query fields out of the request.
     * @param context
     * @param args
     */
    public static void readQueryArguments(Object context, DataBean queryArgs, DataBean solrArgs,int level) {
        String queryStr = "*:*";

        String body = queryArgs.getString("body", "");
        boolean hasData = false;
        if (body.length() > 0) {
            queryStr = "body:(" + body + ")";
            hasData = true;
        }

        String terms = queryArgs.getString("terms", "");

        if (terms.length() > 0) {
            if (hasData)
                queryStr = queryStr + " AND terms:(\"" + terms + "\")";
            else
                queryStr = "terms:(\"" + terms + "\")";

            hasData = true;
        }
        
        if( level == 0 ){
            String others[] = {"terms_nn","terms_nns","terms_nnp","terms_nnps","terms_np","terms_vbz","terms_vbp","terms_vb","terms_vp","terms_vbg","terms_vbd","terms_vbn","terms_jj","terms_jjr","terms_jjs","terms_rb","terms_rbr","terms_rbs","terms_tk"};
            
            for(int i = 0;i < others.length;i++){
                String term = others[i];
                
                String tTerm = queryArgs.getString(term, "");
    
                if (tTerm.length() > 0) {
                    if (hasData)
                        queryStr = queryStr + " AND " + term + ":(" + tTerm + ")";
                    else
                        queryStr = term + ":(" + tTerm + ")";
    
                    hasData = true;
                }
            }
        } 
        else if( level == 1 ){
            String lesser[] = {"terms_noun","terms_verb","terms_adjective"};
            
            for(int i = 0;i < lesser.length;i++){
                String term = lesser[i];
                
                String tTerm = queryArgs.getString(term, "");
    
                if (tTerm.length() > 0) {
                    if (hasData)
                        queryStr = queryStr + " OR " + term + ":(" + tTerm + ")";
                    else
                        queryStr = term + ":(" + tTerm + ")";
    
                    hasData = true;
                }
            }
        }

        solrArgs.setValue("q", queryStr);
    }

}
