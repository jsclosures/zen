function buildRoutingEditPage(mainContext, mainId,currentChild) {

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
        	 
        	 internalBuildRoutingEditPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildRoutingEditPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
            
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
	

        }
        var started = false; 
        context.startChild = function () {
		//console.log("start content page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                }
                
                //navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia;
        }

        context.stopChild = function () {
		//console.log("stop content page");
		
        }
        
        context.setTarget = function (target) {
		//console.log("start content page");
		console.log(target);
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
            var mainId = context.id;
            var formFields = new Array();
            formFields.push({label: "name",name: "name"});
            formFields.push({label: "action",name: "action"});
            formFields.push({label: "target",name: "target"});
                    
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: mainId},dojo.byId(mainId));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    
                    for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                        var label = new dojox.mobile.ContentPane({id: mainId + tField.name + "label",content: profileManager.getString(tField.label)});
                        registeredWidgetList.push(label.id);
                        formContainer.addChild(label);
    
                          var newField = new dojox.mobile.TextBox(
                                      {
                                          id: mainId + tField.name,
                                          name: mainId + tField.name
                                      }
                                  );
                               registeredWidgetList.push(newField.id);   
                          
                        formContainer.addChild(newField);
                    }
                    
        
            
            
            var controlContainer = new dojox.mobile.RoundRect({id: mainId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "save",
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
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "cancel",
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
                     var requestData = {contenttype: "ROUTING"};
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
                         getCurrentContext().CacheManager.purgeType({contenttype: "ROUTING"});
                         getCurrentContext().setCurrentView("routing"); 
                     }
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater).post(false,requestData);             
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("routing");           
            }
            
            
            return( outerContainer );
    }
}
