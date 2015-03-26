package com.jsclosures;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONStringer;


public class RestServiceStub
	extends DataBean implements RestService
{
    public RestServiceStub(){
        super();
    }
    private Configuration configuration;
	
	public void initialize(DataBean config)
	{

		configuration = new Configuration();
		
		String dataSourceName = config.getString("datasourcename","jdbc/webuiDB");
		
		
		if( dataSourceName != null && dataSourceName.length() > 0 )
			configuration.setValue("datasourcename",dataSourceName);
		
		
		String solr = config.getString("solr","http://localhost/solr/");
		
		
		if( solr != null && solr.length() > 0 )
			configuration.setValue("solr",solr);
		
		
		String host = config.getString("host","http://localhost");
		
		
		if( host != null && host.length() > 0 )
			configuration.setValue("host",host);
		else
			configuration.setValue("host","http://localhost");
		
		
		String messageserver = config.getString("messageserver","http://localhost/queryui/cometd/");
		
		
		if( messageserver != null && messageserver.length() > 0 )
			configuration.setValue("messageserver",messageserver);
                
                
	    String logFile = config.getString("host","/tmp/rest.txt");
	    configuration.setValue("logfile",logFile);
            
            
	    String includeMessages = config.getString("includemessages","");
	    configuration.setValue("includemessages",includeMessages);
            
            
	    String webdriver = config.getString("webdriver","http://localhost:4444/wd/hub");
	    configuration.setValue("webdriver",webdriver);
	    
	  
	}
	
	public void setConfiguration(Configuration conf) {
	    configuration = conf;
	}
	
	public Configuration getConfiguration() {
		return( configuration );
	}
	
	public String getConfiguration(String which) {
		return( configuration.getString(which) );
	}

	public void writeLog(int level, String message)
	{
		Helper.writeLog(level,message);
	}

	public static DataBean getDefaultArguments(HttpServletRequest context)
	{
		DataBean args = new DataBean();


		return (args);
	}
    
}
