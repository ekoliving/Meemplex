/*////////////////////////////////////////////////////////////////////////////////////////////// 
   XMLRPC.Connection Version 0.8.2 (for ActionScript 2.0)
   Last Modified: 11-19-2004
   First Modified: 01-20-2004
   
   :::::::::
   
   Contact Information:
   Matt Shaw <matt@dopelogik.com>

//////////////////////////////////////////////////////////////////////////////////////////////*/
import XMLRPC.Method;
import XMLRPC.Parser;

dynamic class XMLRPC.Connection extends XML {
	private var _VERSION:String = "0.8.2";
	private var _PRODUCT:String = "XMLRPC.Connection";
	private var _TRACE_LEVEL:Number = 0;
	private var _server:String;
	private var _method_call:XMLRPC.Method;
	private var _rpc_response:Object;
	private var _parser:XMLRPC.Parser;

	public var LoadFunction:Function;
	public var OnFailed:Function;
		
	/*///////////////////////////////////////////////////////
	Constructor()
	?:	Constructor!
	IN:	RPC Server URL, Desired timeout
	OUT:	Instance of object
 	///////////////////////////////////////////////////////*/
	public function Connection(server:String) {
		//prepare method response handler
		this.ignoreWhite = true;
		
		//init method
		this._method_call = new XMLRPC.Method();
		
		//init parser
		this._parser = new XMLRPC.Parser();

		this.Server = server;
		
		//Have to init these or else __resolve will catch them
		this.LoadFunction=function(){}
		this.OnFailed=function(){}
		
		DTrace("Object instance created. (v" + _VERSION + ")",1);
	}
	
	
	/*///////////////////////////////////////////////////////
	__resolve()
	?:		Calls a method to RPC server using direct Method call
	        This is slick.
	IN:	  	Method Name
	OUT:   	Boolean
 	///////////////////////////////////////////////////////*/
 	public function __resolve(method:String) : Boolean {
	    return this._call(method);    
	}
	
	public function Call(method:String) : Boolean {
//	    trace('XMLRPC.Connection.Call is deprecated! Use direct method call. ie: Connection.'+method+'()');
		return this[method]();
	}
	
	/*///////////////////////////////////////////////////////
	Call()
	?:		Calls a method to RPC server
	IN:	  	Method Name
	OUT:   	Boolean
 	///////////////////////////////////////////////////////*/
	private function _call(method:String) : Boolean {
		DTrace(method+"() -> " + this.Server,1);
		
		if (this.Server==undefined){
			this.DTrace("No Server was specified.",0);
			return false;
		}
		
		//Clear any old responses
		this._rpc_response = null;
		
		this._method_call.setMethod(method);
		this._method_call.Render();
		
		this._method_call.sendAndLoad(this.Server, this);
		
		return true; //all went well
	}
	
	
	
	/*///////////////////////////////////////////////////////
	AddParameter()
	?:	Wrapper for Method.AddParameter()
 	///////////////////////////////////////////////////////*/
	public function AddParameter(param_value:Object,param_type:String):Boolean {
		return this._method_call.AddParameter(param_value,param_type);
	}
	
	/*///////////////////////////////////////////////////////
	ClearParameters()
	?:	Wrapper for Method.ClearParameters()
 	///////////////////////////////////////////////////////*/
	public function ClearParameters():Void{
		this._method_call.ClearParameters()
	}
	

	/*///////////////////////////////////////////////////////////////////////
	UnMarshall()
	?: 		A wrapper for Parser()
	IN:		Void
	OUT: 	A Variable, depending RPCMethod return type
	///////////////////////////////////////////////////////////////////////*/
	private function UnMarshall() : Object {
		DTrace("UnMarshall()",2);
		DTrace("MethodResponse:" + super.toString(),3);
		if (this.IsLoaded()) {
			if (this.status == 0) {
				DTrace("UnMarshall(): Ready to parse response.",3);
				if (this._rpc_response == null) {
					this._rpc_response = this._parser.Parse(this.firstChild);
				}
				DTrace("UnMarshall(): Finished Parsing",3);
				return this._rpc_response;
			}
			DTrace("UnMarshall(): Parsing error: "+this.status,1);
			return null;
		}
		DTrace("UnMarshall(): The _rpc_responseHandler object has not yet loaded.",3);
		return null;
	}
	

	/*///////////////////////////////////////////////////////////////////////
	OnLoaded()
	?: 	Private onLoad for XML. Then triggers user OnLoad.
	IN: 	Boolean
	OUT: Void
	///////////////////////////////////////////////////////////////////////*/
	private function onLoad(success:Boolean) : Void {
		this.DTrace("onLoad()",1);
		if (success) {
			var a = this.UnMarshall();
			this.LoadFunction(a);
			this.CleanUp();
		} else {
			this.onFailed();
		}
	}
	
	/*///////////////////////////////////////////////////////////////////////
	onFailed()
	?: 	 Triggered when RPC fails
	IN:  Void
	OUT: Void
	///////////////////////////////////////////////////////////////////////*/
	private function onFailed():Void {
		this.DTrace("onFailed()",1);
		this.OnFailed();
	}
	

	/*///////////////////////////////////////////////////////////////////////
	IsLoaded()
	?: 	Finds out if XML is loaded from RPC server
	IN: 	Void
	OUT: Boolean
	///////////////////////////////////////////////////////////////////////*/
	public function IsLoaded():Boolean {
		DTrace("IsLoaded()",3);
		return this.loaded;
	}
	
	
	/*///////////////////////////////////////////////////////////////////////
	Server : getter/setter 
	///////////////////////////////////////////////////////////////////////*/
	public function get Server():String{
		return this._server;
	}
	
	public function set Server(a:String){
		this._server = a;
	}
	
	
	/*///////////////////////////////////////////////////////////////////////
	CleanUp()
	?: 	
	///////////////////////////////////////////////////////////////////////*/
	private function CleanUp():Void {
		this.parseXML(null);
		this._method_call.CleanUp();
		this._rpc_response=null;
	}
	
	/*///////////////////////////////////////////////////////////////////////
	Quiet: Setter
	?: 	
	///////////////////////////////////////////////////////////////////////*/
	
	public function get Quiet():Boolean{
		if (this._TRACE_LEVEL==0) return true;
		return false;
	}
	
	public function set Quiet(a:Boolean){
		if (a)
			this._TRACE_LEVEL=0
		else
			this._TRACE_LEVEL=3
			
		this._method_call.Quiet=a;
		this._parser.Quiet=a;
		
	}	
	
	public function toString():String{
		return '<XMLRPC.Connection Object>';
	}
	
	
	/*///////////////////////////////////////////////////////////////////////
	DTrace()
	?: 	 traces to Output
	IN:  A message, level of verboseness (higher=more)
	OUT: Void
	///////////////////////////////////////////////////////////////////////*/
	private function DTrace(a,trace_level:Number):Void {
		if ( this._TRACE_LEVEL >= trace_level){
			trace(this._PRODUCT + " -> " + a);
		}
	}
}