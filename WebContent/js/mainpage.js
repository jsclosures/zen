function buildMainPage(mainContext, mainId,currentChild) {

	require([
                    "dojox/mobile/ComboBox",
                    "dojox/mobile/Button",
                    "dojox/mobile/SimpleDialog",
                    "dojox/mobile/View",
                    "dojox/mobile/TextBox",
                    "dojox/mobile/RoundRect",
                    "dojox/mobile/Container",
                    "dojox/mobile/ContentPane",
                    "dojox/mobile/ScrollableView",
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore",
                    "dojo/store/Memory",
                    "dojo/data/ObjectStore"
         ], 
         function(){
        	 //console.log("building Main page");
        	 
        	 internalBuildMainPage(mainContext, mainId);
                 anyWidgetById(mainId).initChild();
                 anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildMainPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    console.log("Main page context " + context + " in : " + mainId);

    if (context) {
        context.initChild = function () {
		  //console.log("init Main page");
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
           // console.log("resize Main page: ");
		  
         var cContext = getCurrentContext();
		  
		  var tObj = dojo.byId(mainForm);
        }
        var started = false;
        
        context.startChild = function () {
		console.log("start Main page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                }
        }

        context.stopChild = function () {
		//console.log("stop Main page");
		
        }

        context.destroyChild = function () {
		//console.log("destroy Main page");
                for(var i = 0;i < connectorList.length;i++)
                {
                    deregisterEventHandler(connectorList[i]);
                }
                
                for(var i = 0;i < registeredWidgetList.length;i++)
                {
                    deregisterDijitWidget(registeredWidgetList[i]);
                }
	   }

        console.log("added lifecycle handlers to content page context");
    }


    function buildMainPage(context){
            var mainId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;

            var destroyChild = function () {
                    //console.log("destroy login page");
            
                    for(var i = 0;i < connectorList.length;i++)
                    {
                        deregisterEventHandler(connectorList[i]);
                    }
                    
                    for(var i = 0;i < registeredWidgetList.length;i++)
                    {
                        deregisterDijitWidget(registeredWidgetList[i]);
                    }
            }
                        
                    var outerContainer = new dojox.mobile.ScrollableView({id: mainId},dojo.byId(mainId));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    outerContainer.addChild(formContainer);
                    
                    var initialMessage = profileManager.getString("initialSearch");
                    
                    var label = new dojox.mobile.ContentPane({id: mainId + "title",content: initialMessage});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
                    
                    zen.speakMessage(initialMessage);
                
    
              var titleField = new dojox.mobile.ComboBox(
                          {
                              id: mainId + "search",
                              name: mainId + "search",
                              label: profileManager.getString("search"),
                              title: profileManager.getString("search"),
                              searchAttr: "name",
                              store:  new dojo.store.Memory({data:[]}),
                              
                              placeHolder: "",
                              onInput: function(evt){
                                  if ( evt && evt.keyCode == dojo.keys.ENTER) {
                                    doAction();
                                  }
                              }
                          }
                      );
                   registeredWidgetList.push(titleField.id);   
              
            formContainer.addChild(titleField);
            
            
          
    
            var loginButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "action",
                    innerHTML: profileManager.getString("submit"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "loginIcon",
                    onClick: function(){
                        doAction();
                    }
            });
            
            registeredWidgetList.push(loginButton.id);
            formContainer.addChild(loginButton); 
            
            var message = new dojox.mobile.ContentPane({id: mainId + "message",content: ""});
            registeredWidgetList.push(message.id);
            formContainer.addChild(message);
            
                formContainer.startup();
                outerContainer.startup();
    
            function handleResponse(response){
                //console.log(response);
                
                var mainFrame = response[0];
                
                zen.speakMessage(mainFrame.response);
                
                anyWidgetById(mainId + "title").set("content",mainFrame.response);
                anyWidgetById(mainId + "search").set("value","");
                var newStore = false;
                var placeHolder = "";
                if( mainFrame.currentField ){
                    var fieldType = mainFrame.currentField.type;
                    
                    
                    if( fieldType == 'confirm' ){
                        newStore = new dojo.store.Memory({idProperty: "id", data: mainFrame.currentField.options});
                        placeHolder = "Yes or No";
                    }
                    else {
                        //newStore = new dojo.store.Memory({idProperty: "value", data: []});
                    }
                    
                }
                else {
                    //newStore = new dojo.store.Memory({idProperty: "value", data: []});
                
                }
                //newStore = new dojo.data.ObjectStore({storeData: newStore});
                anyWidgetById(mainId + "search").closeDropDown();
                anyWidgetById(mainId + "search").set("placeHolder",placeHolder);
                anyWidgetById(mainId + "search").set("store",newStore);
                
                if( newStore && newStore.data.length > 0 ){
                    anyWidgetById(mainId + "search").set("value",newStore.data[0].id);
                }
                
                if( mainFrame.isComplete ){
                    currentState = false;
                    
                   /* var newFrame = dojo.clone(mainFrame);
                    var previousState = newFrame.previousState;
                    
                    delete newFrame["previousState"];
                    delete previousState["_slotValue"];
                    delete previousState["_grammarIndex"];
                    delete previousState["_input"];
                    var tContent = "Result: " + dojo.toJson(newFrame);
                    if( previousState ){
                        tContent += "<br/>Frame: " + dojo.toJson(previousState);
                    }
                    
                    anyWidgetById(mainId + "message").set("content",tContent);*/
                    
                }
            }
            
            var currentState = false;
            function doAction() {
                    var input = anyWidgetById(mainId + "search").get("value");
                    
                    if( !currentState ){
                        currentState = {input: input,callback: handleResponse};
                        anyWidgetById(mainId + "message").set("content","");
                    }
                    else {
                        currentState.input = input;
                    }
                    
                    zen.respond(currentState);
            }
            
            
            return( outerContainer );
    }
}
