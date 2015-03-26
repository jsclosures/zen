function buildRoutingItemEditPage(mainContext, mainId,currentChild) {

	require([
                    "dojox/mobile/ComboBox",
                    "dojox/mobile/Button",
                    "dojox/mobile/SimpleDialog",
                    "dojox/mobile/View",
                    "dojox/mobile/TextBox",
                    "dojox/mobile/TextArea",
                    "dojox/mobile/RoundRect",
                    "dojox/mobile/Container",
                    "dojox/mobile/ContentPane",
                    "dojox/mobile/ScrollableView",
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore"
         ], 
         function(){
        	 //console.log("building routingedit page");
        	 
        	 internalBuildRoutingItemEditPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildRoutingItemEditPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var formFields = new Array();
    formFields.push({label: "sequence",name: "sequence"});
    formFields.push({label: "mode",name: "mode"});
    formFields.push({label: "className",name: "className"});
    formFields.push({label: "target",name: "target"});
    formFields.push({label: "required",name: "required"});
             
    //console.log("content page context " + context + " in : " + mainId);

    if (context) {
        context.initChild = function () {
		  //console.log("init Content page");
		  var iHtml = '<div id="' + mainForm + '"></div>';
		  
                if( !context.useDojo ) 
                {
                        dojo.byId(mainId).innerHTML = iHtml;
                }
                else
                {
                        dijit.byId(mainId).attr("content",iHtml);
                }
        }

        context.resizeDisplay = function () {
            //console.log("resize Content page: ");
		  
         var cContext = getCurrentContext();
		  
		  var tObj = dojo.byId(mainForm);

        }
        var started = false; 
        context.startChild = function () {
		//console.log("start content page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                }
        }

        context.stopChild = function () {
		//console.log("stop content page");
		
        }
        var parent = false;
        context.setParent = function (p) {
                parent = p;
        }
        context.getParent = function () {
            return( parent );
        }

        
        var target = false;
        context.setTarget = function (t) {
                target = t;
		//console.log("start content page");
		console.log(target);
                for(var i = 0;i < formFields.length;i++){
                    var tField = formFields[i];
                       
                    var tObj = anyWidgetById(mainForm + tField.name);
                    
                    if( tObj ){
                        tObj.set("value",target[tField.name] ? target[tField.name] : "");
                    }
                }
        }
        
        context.getTarget = function () {
            return( target );
        }

        context.destroyChild = function () {
		//console.log("destroy content page");
                for(var i = 0;i < connectorList.length;i++)
                {
                    deregisterEventHandler(connectorList[i]);
                }
                
                for(var i = 0;i < registeredWidgetList.length;i++)
                {
                    deregisterDijitWidget(registeredWidgetList[i]);
                }
	   }

        //console.log("added lifecycle handlers to content page context");
    }

    function buildMainPage(context){
                   
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: mainForm},dojo.byId(mainForm));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainForm + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    
                    for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                        var label = new dojox.mobile.ContentPane({id: mainForm + tField.name + "label",content: profileManager.getString(tField.label)});
                        registeredWidgetList.push(label.id);
                        formContainer.addChild(label);
    
                          var newField = new dojox.mobile.TextBox(
                                      {
                                          id: mainForm + tField.name,
                                          name: mainForm + tField.name
                                      }
                                  );
                               registeredWidgetList.push(newField.id);   
                          
                        formContainer.addChild(newField);
                    }
                    
        
            
            
            var controlContainer = new dojox.mobile.RoundRect({id: mainForm + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "save",
                    innerHTML: profileManager.getString("save"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "saveIcon",
                    onClick: function(){
                        doSaveAction();
                    }
            });
            
            registeredWidgetList.push(saveButton.id);
            controlContainer.addChild(saveButton); 
            
            var deleteButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "delete",
                    innerHTML: profileManager.getString("delete"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "deleteIcon",
                    onClick: function(){
                        doDeleteAction();
                    }
            });
            
            registeredWidgetList.push(deleteButton.id);
            controlContainer.addChild(deleteButton);   
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "cancel",
                    innerHTML: profileManager.getString("cancel"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "cancelIcon",
                    onClick: function(){
                        doCancelAction();
                    }
            });
            
            registeredWidgetList.push(cancelButton.id);
            controlContainer.addChild(cancelButton); 
            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doSaveAction() {
                     var requestData = {contenttype: "ROUTINGITEM"};
                     requestData.parentid = anyWidgetById(mainId).getParent().id;
                     if( anyWidgetById(mainId).getTarget().id ){
                         requestData.id = anyWidgetById(mainId).getTarget().id;
                     }
                     for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                         requestData[tField.name] = anyWidgetById(mainForm + tField.name).get("value");
                     }
                     
                     console.log(requestData);
                     
                     var doLater = function(data){
                        for(var i = 0;i < formFields.length;i++){
                            var tField = formFields[i];
                             anyWidgetById(mainForm + tField.name).set("value","");
                         }
                         getCurrentContext().CacheManager.purgeType({contenttype: "ROUTINGITEM" + requestData.parentid});
                         getCurrentContext().setCurrentView("routingview"); 
                         anyWidgetById("routingview").reloadTarget();
                     }
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     if( requestData.id )
                        getDataService(tURL, doLater).put(false,requestData);     
                     else
                        getDataService(tURL, doLater).post(false,requestData);             
            }
            
            function doDeleteAction() {
                     var requestData = {contenttype: "ROUTINGITEM",id: anyWidgetById(mainId).getTarget().id};
                     
                     console.log(requestData);
                     
                     var doLater = function(data){
                        for(var i = 0;i < formFields.length;i++){
                            var tField = formFields[i];
                             anyWidgetById(mainForm + tField.name).set("value","");
                         }

                         getCurrentContext().setCurrentView("routingview"); 
                         anyWidgetById("routingview").reloadTarget();
                     }
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater)["delete"](false,requestData); 
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("routingview");           
            }
            
            
            return( outerContainer );
    }
}
