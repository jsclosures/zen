define(["dojo/_base/lang","dojo/_base/array", "dojo/_base/declare", "dojo/_base/connect", "dojo/_base/event"], 
  function(lang,arr,declare,connect,evt){
	return declare("jsclosures.ui.Mover", null, {
		constructor: function(shape, e, host){
			// summary: an object, which makes a shape follow the mouse,
			//	used as a default mover, and as a base class for custom movers
			// shape: dojox.gfx.Shape: a shape object to be moved
			// e: Event: a mouse event, which started the move;
			//	only clientX and clientY properties are used
			// host: Object?: object which implements the functionality of the move,
			//	 and defines proper events (onMoveStart and onMoveStop)
			this.shape = shape;
			this.lastX = e.clientX
			this.lastY = e.clientY;
			var h = this.host = host, d = document,
				firstEvent = connect.connect(d, "onmousemove", this, "onFirstMove");
			this.events = [
				//connect.connect(d, "onmousemove", this, "onMouseMove"),
				connect.connect(d, "onmouseup",   this, "destroy"),
				// cancel text selection and text dragging
				connect.connect(d, "ondragstart",   evt, "stop"),
				connect.connect(d, "onselectstart", evt, "stop"),
				firstEvent
			];
			// notify that the move has started
			if(h && h.onMoveStart){
				h.onMoveStart(this);
			}
		},
		// mouse event processors
		onMouseMove: function(e){
			// summary: event processor for onmousemove
			// e: Event: mouse event
			var x = e.clientX;
			var y = e.clientY;
                        //if( x > 0 && y > 0 && x >= 0 && x <= this.maxBounds.x && y >= 0 && y <= this.maxBounds.y ){
                        if( !isNaN(x) && !isNaN(y) ){
                            this.host.onMove(e,this, {dx: x - this.lastX, dy: y - this.lastY});
                            this.lastX = x;
                            this.lastY = y;
                        }
			evt.stop(e);
		},
		// utilities
		onFirstMove: function(){
			// summary: it is meant to be called only once
			this.host.onFirstMove(this);
			connect.disconnect(this.events.pop());
		},
		destroy: function(evt){
                        this.onMouseMove(evt);
			// summary: stops the move, deletes all references, so the object can be garbage-collected
			arr.forEach(this.events, connect.disconnect);
			// undo global settings
			var h = this.host;
			if(h && h.onMoveStop){
				h.onMoveStop(this);
			}
			// destroy objects
			this.events = this.shape = null;
		},
                maxBounds: {
                    x: 0,
                    y:0
                },
                setMaxBounds: function(b){
                    this.maxBounds = b;
                }
	});
});