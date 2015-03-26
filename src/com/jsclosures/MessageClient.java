package com.jsclosures;


public class MessageClient extends DataBean {
    
    private org.cometd.client.BayeuxClient messageClient;
    private org.cometd.bayeux.client.ClientSessionChannel messageChannel;
    private RestService context;
    private DataBean queryArgs;
    private String key;
    private boolean includeMessages = true;
    
    public MessageClient(RestService context,DataBean queryArgs){
        super();
        this.context = context;
        this.queryArgs = queryArgs;
        this.key = queryArgs.getString("messagekey","1000");
        this.includeMessages = context.getConfiguration().getString("includemessages").equalsIgnoreCase("true");
        
        if( this.includeMessages ){
            this.messageClient = Helper.initializeMessageService(context,queryArgs);
            this.messageChannel = Helper.messageServiceOpenChannel(context,queryArgs,messageClient,"/zen/app");
        }
    }
    
    public MessageClient(RestService context,DataBean queryArgs,org.cometd.client.BayeuxClient messageClient,org.cometd.bayeux.client.ClientSessionChannel messageChannel){
        super();
        
        this.context = context;
        this.queryArgs = queryArgs;
        this.messageClient = messageClient;
        this.messageChannel = messageChannel;
    }
    
    public void sendCustomMessage(String channel,String type,String tag,String message){
        if( this.includeMessages ){
            Helper.messageServicePublishChannel(context,queryArgs,messageClient,key,type,channel,tag,message);
        }
    }
    
    public void sendMessage(String tag,String message){
        sendMessage("MESSAGE",tag,message);
    }
    public static String CONTROLMESSAGEOPEN = "<control>";
    public static String CONTROLMESSAGECLOSE = "</control>";
    public static String CONTROLMESSAGE = "control";
    
    public void sendControlMessage(String message){
        //sendMessage("MESSAGE",tag,CONTROLMESSAGEOPEN.concat(message).concat(CONTROLMESSAGECLOSE));
        sendMessage("MESSAGE",CONTROLMESSAGE,message);
    }
    
    public void sendMessage(String type,String tag,String message){
        if( this.includeMessages ){
            Helper.messageServicePublishChannel(context,queryArgs,messageChannel,key,type,tag,message);
        }
    }
    
    public void destroy(){
        if( this.includeMessages ){
            Helper.finalizeMessageService(context,queryArgs,messageClient);
        
            this.messageClient = null;
            this.messageChannel = null;
        }
    }
}