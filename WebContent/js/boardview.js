function buildBoardViewPage(mainContext, mainId,currentChild) {

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
                    "dojo/data/ItemFileReadStore",
                    "dojox/gfx",
                    "dojox/gfx/Moveable"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildBoardViewPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildBoardViewPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var maxBounds = false;
    
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

        var surface = false;
        var started = false; 

        context.startChild = function () {
                if( !started ){
                    started = true;
                    buildMainPage({id: mainForm});
                    
                }
                
		//console.log("start content page");
                if( !surface ){
                    var canvas = anyWidgetById(mainForm + "canvas");
                    var dim = {width: 300,height: 300};
                
                    dojox.gfx.createSurface(mainForm + "canvas",dim.width,dim.height).whenLoaded(this, function(newSurface){
                        setSurface(newSurface);
                    });
                }
                
        }
        var currentTool = false;
            
        context.selectTool = function(which){
        
            if( currentTool ){
                anyWidgetById(mainForm + currentTool).set("disabled",false);
            }
        
            currentTool = which;
            
            var rec = anyWidgetById(mainId).getTarget();
                
            anyWidgetById(mainForm + "titlelabel").set("content",rec.label + " - " + getCurrentContext().UIProfileManager.getString(currentTool));
            anyWidgetById(mainForm + currentTool).set("disabled",true);
        }
        
        context.removeSelected = function(){
            console.log("remove selected");
            if( selectedList ){
                var tList = selectedList.sort(function compare(a, b) {return( b-a);});
                var uiManager = getCurrentContext().UIProfileManager;
                
                for(var i = 0;i < tList.length;i++){
                    var idx = uiManager.ensureInteger(tList[i]);
                    var node = boardInfo.items[idx];
                    boardInfo.items.splice(idx,1);
                    surface.remove(shapeList[idx]);
                    
                    var removeItem = {};
                    removeItem.contenttype = "BOARDITEM";
                    removeItem.id = node.id;
                    var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                    
                    var doLater = function(data){
                        console.log(data);
                    }
                    getDataService(tURL, doLater)['delete'](false,removeItem);   
                }
                
                selectedList = false;
            }
        }
            
        var startPoint = false;
        function showWhatToolMade(start,stop,tool){
            console.log(start + " " + stop + " " + tool);
            if( tool == 'line' ){
                var info = {x1: start.offsetX,y1: start.offsetY,x2: stop.offsetX,y2: stop.offsetY,color: "#6c7178",style: "Solid",width: 3,cap: "round"};
                
                addBoardItem({itemtype: 'line',iteminfo: info});
            }
            else if( tool == 'rectangle' ){
                var info = {x: start.offsetX,y: start.offsetY,width: stop.offsetX-start.offsetX,height: stop.offsetY-start.offsetY,color: "#6c7178",style: "Solid",cap: "round",fill: "#880000"};
                
                addBoardItem({itemtype: 'rectangle',iteminfo: info});
            }
            else if( tool == 'ellipse' ){
                var info = {cx: start.offsetX,cy: start.offsetY,rx: Math.abs(stop.offsetX-start.offsetX)/2,ry: Math.abs(stop.offsetY-start.offsetY)/2,color: "#6c7178",style: "Solid",fill: "#880000",width: 3,cap: "round"};
                
                addBoardItem({itemtype: 'ellipse',iteminfo: info});
            }
            else if( tool == 'text' ){
                var info = {x: start.offsetX,y: start.offsetY,text: "jsclosures",color: "#6c7178",style: "Solid",cap: "round",fill: "#880000",font: {family:"sans-serif",size:"7pt",weight:"normal"}};
                
                addBoardItem({itemtype: 'text',iteminfo: info});
            }
        }
        
        function onMouseDown(evt){
            if( getCurrentContext().getCurrentView() == mainId && currentTool != 'select' ){
            
                var sPos = dojo.position(anyWidgetById(mainForm + "canvas").domNode);
                if( evt.clientX >= sPos.x && evt.clientY >= sPos.y 
                    && evt.clientX <= sPos.x+sPos.w && evt.clientY <= sPos.y+sPos.h ){
                        //console.log("down: " + evt);
                        startPoint = evt;
                        dojo.stopEvent(evt);
                        return( false );
                }
                else
                    return( true );
            }
            else
                return( true );
        }

        function onMouseMove(evt){
                if( getCurrentContext().getCurrentView() == mainId && currentTool != 'select' && startPoint ){
                    //console.log("move: " + evt);
                
                    dojo.stopEvent(evt);
                }
        }

        function onMouseUp(evt){
                if( getCurrentContext().getCurrentView() == mainId && currentTool != 'select' && startPoint ){
                    //console.log("up: " + evt);
                    var endPoint = evt;
                    showWhatToolMade(startPoint,endPoint,currentTool);
                    startPoint = false;
                    dojo.stopEvent(evt);
                }
        }
        
        function setSurface(s){
            surface = s;
            var container = anyWidgetById(mainForm + "canvas");
            
            var sPos = dojo.position(container.domNode);
            maxBounds = {x: sPos.w,y: sPos.h};
            
            
            /*var tgConnect = dojo.connect(container.domNode,"onmousemove",onMouseMove);
            connectorList.push(tgConnect);
            
            var tgConnect = dojo.connect(container.domNode,"onmousedown",onMouseDown);
            connectorList.push(tgConnect);
                
            var tgConnect = dojo.connect(container.domNode,"onmouseup",onMouseUp);
            connectorList.push(tgConnect);*/
            
            var tgConnect = surface.on("onmousemove",onMouseMove);
            connectorList.push(tgConnect);
            
            var tgConnect = surface.on("onmousedown",onMouseDown);
            connectorList.push(tgConnect);
                
            var tgConnect = surface.on("onmouseup",onMouseUp);
            connectorList.push(tgConnect);
            
            var tgConnect = surface.on(dojo.touch.move,onMouseMove);
            connectorList.push(tgConnect);
            
            var tgConnect = surface.on(dojo.touch.press,onMouseDown);
            connectorList.push(tgConnect);
                
            var tgConnect = surface.on(dojo.touch.release,onMouseUp);
            connectorList.push(tgConnect);
            
            /*container.on("onmousemove",onMouseMove);
            container.on("onmousedown",onMouseDown);
            container.on("onmouseup",onMouseUp);*/
        }
        
        function addBoardItem(item){     
            if( item ){
                console.log(item);
                boardInfo.items.push(item);
                drawItem(item,boardInfo.items.length-1);
                
                var newItem = {};
                newItem.itemtype = item.itemtype;
                newItem.iteminfo = dojo.toJson(item.iteminfo);
                
                newItem.contenttype = "BOARDITEM";
                newItem.parentid = target.id;
                var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                var doLater = function(data){
                    console.log(data);
                }
                getDataService(tURL, doLater)['post'](false,newItem);   
            }
        }
        
        function addBoardInfo(msg){
            var item = msg.data.text;
            
            if( item ){
                item = dojo.fromJson(item);
                console.log(item);
                var requestData = {contenttype: "BOARDITEM",parentid: target.id,itemtype: item.itemtype,iteminfo: dojo.clone(item.iteminfo)};
                requestData.iteminfo = dojo.toJson(requestData.iteminfo);
                
                var doLater = function(data){
                    console.log("update complete");
                    var newItem = data.items[0];
                    
                    boardInfo.items.push(newItem);
                    newItem.iteminfo = dojo.fromJson(newItem.iteminfo);
                
                    drawItem(newItem,boardInfo.items.length-1);
                }
                
                var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                    
                getDataService(tURL, doLater)['post'](false,requestData);   
            }
        }
        
        var boardInfo = false;
        function loadBoardData(data){
            surface.clear();
            
            if( target && target.boardbackground ){
                surface.createImage({x:0,y:0, width:360, height:240, src:target.boardbackground})
            }
            
            boardInfo = {};
            if( data.items ){
                boardInfo.items = data.items;
                for(var i = 0;i < data.items.length;i++){
                    data.items[i].iteminfo = dojo.fromJson(data.items[i].iteminfo);
                }
            }
            else {
                boardInfo.items = new Array();
            }
            /*boardInfo.items = new Array();
            
            boardInfo.items.push({id: "1",type: "line",info: {x1: 10,y1: 10,x2: 100,y2: 100,color: "#6c7178",style: "Solid",width: 3,cap: "round"}});
            boardInfo.items.push({id: "2",type: "text",info: {x: 100,y: 100,text: "jsclosures.com",color: "#007178",style: "Solid",cap: "round",font: {family:"sans-serif",size:"7pt",weight:"normal"}}});
            boardInfo.items.push({id: "3",type: "rectangle",info: { x: 110, y: 110, width:20, height:20,color: "#007178",style: "Solid",cap: "round",fill: "#880000"}});
            boardInfo.items.push({id: "4",type: "ellipse",info: { cx: 210, cy: 210, rx:20, ry:20,color: "#007170",style: "Solid",cap: "round",fill: "#880000"}});
            */
            
            drawBoardData();
        }
        
        function shapeMoveComplete(evt,ctx,node,shift){
            console.log("shape call back " + node);
            var requestData = {contenttype: "BOARDITEM",id: node.id,parentid: node.parentid,itemtype: node.itemtype,iteminfo: dojo.clone(node.iteminfo)};
            if( requestData.itemtype == 'line' ){
                requestData.iteminfo.x1 = node.iteminfo.x1 + shift.dx; 
                requestData.iteminfo.x2 = node.iteminfo.x2 + shift.dx; 
                requestData.iteminfo.y1 = node.iteminfo.y1 + shift.dy;
                requestData.iteminfo.y2 = node.iteminfo.y2 + shift.dy;
            }
            else if( requestData.itemtype == 'rectangle' ){
                requestData.iteminfo.x = node.iteminfo.x + shift.dx; 
                requestData.iteminfo.y = node.iteminfo.y + shift.dy;
            }
            else if( requestData.itemtype == 'ellipse' ){
                requestData.iteminfo.cx = node.iteminfo.cx + shift.dx; 
                requestData.iteminfo.cy = node.iteminfo.cy + shift.dy;
            }
            else if( requestData.itemtype == 'text' ){
                requestData.iteminfo.x = node.iteminfo.x + shift.dx; 
                requestData.iteminfo.y = node.iteminfo.y + shift.dy;
            }
            requestData.iteminfo = dojo.toJson(requestData.iteminfo);
            
            var doLater = function(data){
                console.log("update complete");
                requestData.iteminfo = dojo.fromJson(requestData.iteminfo);
            
                boardInfo.items[ctx.getBaseIndex()] = requestData;
                ctx.setNode(requestData);
            }
            
            var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
            getDataService(tURL, doLater)['put'](false,requestData);   
        }
        var shapeConnectList = false;
        var selectedList = false;
        var shapeList = false;
        function shapeClick(evt,ctx,node){
            console.log("shape click: " + evt);
            
            if( currentTool == 'select' ){
                if( !selectedList ) 
                    selectedList = new Array();
                
                var match = "" + ctx.getBaseIndex();
                var cIdx = indexOfString(selectedList,match);
                
                if( cIdx < 0 ){
                    selectedList.push(match);
                }
                else {
                    selectedList.splice(cIdx,1);
                }
                console.log("selectedlist: " + selectedList);
            }
        }
        function drawItem(currentItem,baseIndex){
            if( currentItem.itemtype == 'line' ){
                    var line = surface.createLine({x1: currentItem.iteminfo.x1,
                                                    y1: currentItem.iteminfo.y1,
                                                    x2: currentItem.iteminfo.x2,
                                                    y2: currentItem.iteminfo.y2});
        
                    line.setStroke({
                        style: currentItem.iteminfo.style,
                        width: currentItem.iteminfo.width,
                        cap: currentItem.iteminfo.cap,
                        color: currentItem.iteminfo.color
                    });
                    
                    var tMoveable = new jsclosures.ui.Moveable(line);
                    tMoveable.setMaxBounds(maxBounds);
                    tMoveable.setMoveCallback(shapeMoveComplete);
                    tMoveable.setNode(currentItem);
                    tMoveable.setSurface(surface);
                    tMoveable.setBaseIndex(baseIndex);
                    tMoveable.setClickCallback(shapeClick);
                    shapeList.push(line);
                }
                else if( currentItem.itemtype == 'text' ){
                    var text = surface.createText({x: currentItem.iteminfo.x,
                                                    y: currentItem.iteminfo.y,
                                                    text: currentItem.iteminfo.text});
                    if( currentItem.iteminfo.font ){
                        text.setFont(currentItem.iteminfo.font);
                    }
                    
                    text.setStroke({
                        style: currentItem.iteminfo.style,
                        width: currentItem.iteminfo.width,
                        cap: currentItem.iteminfo.cap,
                        color: currentItem.iteminfo.color
                    });
                    
                    var tMoveable = new jsclosures.ui.Moveable(text);
                    tMoveable.setMaxBounds(maxBounds);
                    tMoveable.setMoveCallback(shapeMoveComplete);
                    tMoveable.setNode(currentItem);
                    tMoveable.setSurface(surface);
                    tMoveable.setBaseIndex(baseIndex);
                    tMoveable.setClickCallback(shapeClick);
                    shapeList.push(text);
                }
                else if( currentItem.itemtype == 'rectangle' ){
                    var rect = surface.createRect({x: currentItem.iteminfo.x,
                                                    y: currentItem.iteminfo.y,
                                                    width: currentItem.iteminfo.width,
                                                    height: currentItem.iteminfo.height});
        
                    if( currentItem.iteminfo.fill ){
                        rect.setFill(currentItem.iteminfo.fill);
                    }
                    
                    rect.setStroke({
                        style: currentItem.iteminfo.style,
                        width: currentItem.iteminfo.width,
                        cap: currentItem.iteminfo.cap,
                        color: currentItem.iteminfo.color
                    });
                    
                    var tMoveable = new jsclosures.ui.Moveable(rect);
                    tMoveable.setMaxBounds(maxBounds);
                    tMoveable.setMoveCallback(shapeMoveComplete);
                    tMoveable.setNode(currentItem);
                    tMoveable.setSurface(surface);
                    tMoveable.setBaseIndex(baseIndex);
                    tMoveable.setClickCallback(shapeClick);
                    shapeList.push(rect);
                }
                else if( currentItem.itemtype == 'ellipse' ){
                    var ellipse = surface.createEllipse({cx: currentItem.iteminfo.cx,
                                                    cy: currentItem.iteminfo.cy,
                                                    rx: currentItem.iteminfo.rx,
                                                    ry: currentItem.iteminfo.ry});
                    if( currentItem.iteminfo.fill ){
                        ellipse.setFill(currentItem.iteminfo.fill);
                    }
                    
                    ellipse.setStroke({
                        style: currentItem.iteminfo.style,
                        width: currentItem.iteminfo.width,
                        cap: currentItem.iteminfo.cap,
                        color: currentItem.iteminfo.color
                    });
                    
                    var tMoveable = new jsclosures.ui.Moveable(ellipse);
                    tMoveable.setMaxBounds(maxBounds);
                    tMoveable.setMoveCallback(shapeMoveComplete);
                    tMoveable.setNode(currentItem);
                    tMoveable.setSurface(surface);
                    tMoveable.setBaseIndex(baseIndex);
                    tMoveable.setClickCallback(shapeClick);
                    shapeList.push(ellipse);
                }
        }
        
        function drawBoardData(){
            if( shapeConnectList ){
                for(var i = 0;i < shapeConnectList.length;i++){
                    deregisterEventHandler(shapeConnectList[i]);
                }
                shapeConnectList = false;
            }
            if( shapeList ){
                for(var i = 0;i < shapeList.length;i++){
                   surface.remove(shapeList[i]);
                }
                shapeList = false;
            }
            if( boardInfo && boardInfo.items ){
                shapeConnectList = new Array();
                shapeList = new Array();
                var items = boardInfo.items;
                for(var i = 0;i < items.length;i++){
                    var currentItem = items[i];
                    
                    drawItem(currentItem,i);
                }
                
            }
        }
        
        context.stopChild = function () {
		console.log("stop boardview page");
		disconnectFromPrivateChannel(getCurrentContext().UIProfileManager.ensureString(target.id));
        }
        var target = false;
        context.getTarget = function(){
            return( target );
        }
        context.setTarget = function (rec) {
		console.log("start boardview page");
                target = rec;
		console.log("target: " + target);
                
                anyWidgetById(mainForm + "titlelabel").set("content",target.boardtitle);
                
                
                connectToPrivateChannel(getCurrentContext().UIProfileManager.ensureString(target.id),addBoardInfo);
                
                getCurrentContext().CacheManager.getData({contenttype:"BOARDITEM",parentid: target.id,nocache: true,callback: loadBoardData});
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
            var formId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: formId},dojo.byId(formId));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: formId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    var label = new dojox.mobile.ContentPane({id: formId + "titlelabel",content: profileManager.getString("contentTitle")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
    

            var controlContainer = new dojox.mobile.RoundRect({id: formId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "delete",
                    innerHTML: profileManager.getString("delete"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "deleteIcon",
                    onClick: function(){
                        doDeleteAction();
                    }
            });
            
            registeredWidgetList.push(saveButton.id);
            controlContainer.addChild(saveButton); 
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "cancel",
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
            
            
            function selectTool(which){
                anyWidgetById(mainId).selectTool(which);
            }
            
            function removeSelected(){
                anyWidgetById(mainId).removeSelected();
            }
            
            var selectButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "select",
                    innerHTML: profileManager.getString("select"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "selectIcon",
                    onClick: function(){
                        selectTool("select");
                    }
            });
            
            registeredWidgetList.push(selectButton.id);
            controlContainer.addChild(selectButton); 
            
            var lineButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "line",
                    innerHTML: profileManager.getString("line"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "lineIcon",
                    onClick: function(){
                        selectTool("line");
                    }
            });
            
            registeredWidgetList.push(lineButton.id);
            controlContainer.addChild(lineButton); 
            
            var rectButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "rectangle",
                    innerHTML: profileManager.getString("rectangle"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "rectangleIcon",
                    onClick: function(){
                        selectTool("rectangle");
                    }
            });
            
            registeredWidgetList.push(rectButton.id);
            controlContainer.addChild(rectButton); 
            
            var ellipseButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "ellipse",
                    innerHTML: profileManager.getString("ellipse"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "ellipseIcon",
                    onClick: function(){
                        selectTool("ellipse");
                    }
            });
            
            registeredWidgetList.push(ellipseButton.id);
            controlContainer.addChild(ellipseButton);
            
            
            var textButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "text",
                    innerHTML: profileManager.getString("text"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "textIcon",
                    onClick: function(){
                        selectTool("text");
                    }
            });
            
            registeredWidgetList.push(textButton.id);
            controlContainer.addChild(textButton);
            
            var removeButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "remove",
                    innerHTML: profileManager.getString("remove"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "removeIcon",
                    onClick: function(){
                        removeSelected();
                    }
            });
            
            registeredWidgetList.push(removeButton.id);
            controlContainer.addChild(removeButton);
            
            
            var canvas = new dojox.mobile.ContentPane({id: formId + "canvas"});
                    registeredWidgetList.push(canvas.id);
                    formContainer.addChild(canvas);
            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doDeleteAction() {
                    var rec = anyWidgetById(mainId).getTarget();
                    
                    var requestData = {contenttype: "BOARD"};
                     requestData.id = rec.id;
                     
                     console.log(requestData);
                     
                     var doLater = function(data){
                         getCurrentContext().CacheManager.purgeType({contenttype: "BOARD"});
                         getCurrentContext().setCurrentView("board"); 
                     }
                     
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater)['delete'](false,requestData);         
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("board");           
            }
            
        
            
            
            return( outerContainer );
    }
}



define(["dojo/_base/lang","dojo/_base/declare","dojo/_base/array","dojo/_base/event","dojo/_base/connect",
    "dojo/dom-class","dojo/_base/window","js/Mover.js"],
    function(lang,declare,arr,event,connect,domClass,win,Mover){
        return declare("jsclosures.ui.Moveable", null, {
            constructor: function(shape, params){
                // summary: an object, which makes a shape moveable
                // shape: dojox.gfx.Shape: a shape object to be moved
                // params: Object: an optional object with additional parameters;
                //	following parameters are recognized:
                //		delay: Number: delay move by this number of pixels
                //		mover: Object: a constructor of custom Mover
                this.shape = shape;
                this.delay = (params && params.delay > 0) ? params.delay : 0;
                this.mover = (params && params.mover) ? params.mover : Mover;
                this.events = [
                this.shape.connect("onmousedown", this, "onMouseDown")
                // cancel text selection and text dragging
                //, dojo.connect(this.handle, "ondragstart",   dojo, "stopEvent")
                //, dojo.connect(this.handle, "onselectstart", dojo, "stopEvent")
                ];
            },
	
            // methods
            destroy: function(){
                // summary: stops watching for possible move, deletes all references, so the object can be garbage-collected
                arr.forEach(this.events, this.shape.disconnect, this.shape);
                this.events = this.shape = null;
            },
	
            // mouse event processors
            onMouseDown: function(e){
                // summary: event processor for onmousedown, creates a Mover for the shape
                // e: Event: mouse event
                if(this.delay){
                    this.events.push(
                        this.shape.connect("onmousemove", this, "onMouseMove"),
                        this.shape.connect("onmouseup", this, "onMouseUp"));
                    this._lastX = e.clientX;
                    this._lastY = e.clientY;
                }else{
                    var tMover = new this.mover(this.shape, e, this);
                    tMover.setMaxBounds(this.maxBounds);
                }
                event.stop(e);
            },
            onMouseMove: function(e){
                // summary: event processor for onmousemove, used only for delayed drags
                // e: Event: mouse event
                if(Math.abs(e.clientX - this._lastX) > this.delay || Math.abs(e.clientY - this._lastY) > this.delay){
                    this.onMouseUp(e);
                    new this.mover(this.shape, e, this);
                }
                event.stop(e);
            },
            onMouseUp: function(e){
                // summary: event processor for onmouseup, used only for delayed delayed drags
                // e: Event: mouse event
                this.shape.disconnect(this.events.pop());
                this.shape.disconnect(this.events.pop());
            },
	
            // local events
            onMoveStart: function(/* dojox.gfx.Mover */ mover){
                // summary: called before every move operation
                connect.publish("/gfx/move/start", [mover]);
                domClass.add(win.body(), "dojoMove");
            },
            onMoveStop: function(/* dojox.gfx.Mover */ mover){
                // summary: called after every move operation
                connect.publish("/gfx/move/stop", [mover]);
                domClass.remove(win.body(), "dojoMove");
            },
            onFirstMove: function(/* dojox.gfx.Mover */ mover){
            // summary: called during the very first move notification,
            //	can be used to initialize coordinates, can be overwritten.
	
            // default implementation does nothing
            },
            onMove: function(evt,/* dojox.gfx.Mover */ mover, /* Object */ shift){
                // summary: called during every move notification,
                //	should actually move the node, can be overwritten.
                //this.onMoving(mover, shift);
                        
                if( this.onMoving(mover, shift) ) {
                    this.shape.applyLeftTransform(shift);
                    this.onMoved(evt,mover, shift);
                }
            },
            onMoving : function (mover, shift)
            {
                var resultX = true;
                var resultY = true;
                      
                if( true ){
                /*var transform = this.shape.getTransform();
                    // If this.shape hasn't been transformed yet
                    // getTransform() will return null, so we
                    // need to handle the special case of the first mouse movement.
                    if (transform==null) {
                        // We aren't dealing with rotations here, so we will
                        // just define initial values for the translations dx and dy.
                        transform = {
                            dx: 0,
                            dy: 0
                        };
                    }
                    
                    var shapeBounds = mover.shape.rawNode.getBBox ? mover.shape.rawNode.getBBox() : {
                        width: 100,
                        height: 100
                    };
                    // var shapeWidth = shapeBounds.width;
                    //var shapeHeight = shapeBounds.height;
                                        
                    if( shift.dx < 0 ) {  //move left
                        var normalizedPosition = this.normalizePosition(mover,-100);
                    
                        var totalShiftX = normalizedPosition.lastX + shift.dx;
                         
                        console.log("move left x: " + totalShiftX + " dx: " + shift.dx + " lx: " + normalizedPosition.lastX + " mx: " + this.maxBounds.x + " sw: " + shapeBounds.width);
                        if( totalShiftX <= 0 ){
                            console.log("veto move left");
                            shift.dx = 0;
                            resultX = false;
                        }
                    }
                    else if( shift.dx > 0 ){  //move right
                        var normalizedPosition = this.normalizePosition(mover,-30);
                    
                        totalShiftX = normalizedPosition.lastX + shift.dx + (shapeBounds.width);
                         
                        console.log("move right x: " + totalShiftX + " dx: " + shift.dx + " lx: " + normalizedPosition.lastX + " mx: " + this.maxBounds.x + " sw: " + shapeBounds.width);
                        if( totalShiftX >= this.maxBounds.x )
                        {
                            console.log("veto move right");
                            shift.dx = 0;
                            resultX = false;
                        }
                    }
                          
                    if( shift.dy < 0 ) {  //move up
                        var normalizedPosition = this.normalizePosition(mover,-80);
                    
                        var totalShiftY = normalizedPosition.lastY + shift.dy - 60;
                         
                        console.log("move up y: " + totalShiftY + " dy: " + shift.dy + " ly: " + normalizedPosition.lastY + " my: " + this.maxBounds.y + " sh: " + shapeBounds.height);
                        if( totalShiftY <= 0 ){
                            console.log("veto move up");
                            shift.dy = 0;
                            resultY = false;
                        }
                    }
                    else if( shift.dy > 0 ){
                        var normalizedPosition = this.normalizePosition(mover,-90);
                    
                        totalShiftY = normalizedPosition.lastY + shift.dy + (shapeBounds.height);
                         
                        console.log("move down y: " + totalShiftY + " dy: " + shift.dy + " ly: " + normalizedPosition.lastY + " my: " + this.maxBounds.y + " sh: " + shapeBounds.height);
                        if( totalShiftY >= this.maxBounds.y )
                        {
                            console.log("veto move down");
                            shift.dy = 0;
                            resultY = false;
                        }
                    }*/
                }
                    
                return( resultX || resultY );
            },
            normalizePosition: function(mover,direction){
                return( {
                    lastX: mover.lastX + (direction),
                    lastY: mover.lastY + (direction)
                } );
            },
            maxBounds: {
                x: 0,
                y:0
            },
            setMaxBounds: function(b){
                this.maxBounds = b;
            },
            node: false,
            setNode: function(n){
                this.node = n;
            },
            surface: false,
            setSurface: function(s){
                this.surface = s;
            },
            moveCallback: false,
            setMoveCallback: function(b){
                this.moveCallback = b;
            },
            clickCallback: false,
            setClickCallback: function(b){
                this.clickCallback = b;
            },
            baseIndex: -1,
            setBaseIndex: function(i){ this.baseIndex = i;},
            getBaseIndex: function(){ return( this.baseIndex );},
            onMoved: function(evt,/* dojox.gfx.Mover */ mover, /* Object */ shift){
                // summary: called after every incremental move,
                //	can be overwritten.
            
                // default implementation does nothing
                console.log("moved shaped ");
                /* for(var n in shift){
                    var attr = shift[n];
                    console.log("key: "+n+" value: "+attr);
                }*/
                
                var getNodeX = function(n){
                    var result;
                    if( n.itemtype == 'line' )
                        result = (n.x2 - n.x1)/2;
                    else if( n.itemtype == 'rectangle' )
                        result = n.x;
                    else if( n.itemtype == 'ellipse' )
                        result = n.cx;
                    else if( n.itemtype == 'text' )
                        result = n.x;
                          
                    return( result );
                }
                var getNodeY = function(n){
                    var result;
                    if( n.itemtype == 'line' )
                        result = (n.y2 - n.y1)/2;
                    else if( n.itemtype == 'rectangle' )
                        result = n.y;
                    else if( n.itemtype == 'ellipse' )
                        result = n.cy;
                    else if( n.itemtype == 'text' )
                        result = n.y;
                          
                    return( result );
                }
                if( this.moveCallback && (Math.abs(shift.dx) >= 1 || Math.abs(shift.dy) >= 1) ){
                    var shapeBounds = false && mover.shape.rawNode.getBBox ? mover.shape.rawNode.getBBox() : {
                        x: getNodeX(this.node) + shift.dx,//evt.layerX,
                        y: getNodeY(this.node) + shift.dy//evt.layerY
                    };
                    console.log("move callback");
                    
                    evt.layerX = shapeBounds.x;
                    evt.layerY = shapeBounds.y;

                    this.moveCallback(evt,this,this.node,shift);
                }
                else if( this.clickCallback ){
                    this.clickCallback(evt,this,this.node);
                }
            }
        });
    });



