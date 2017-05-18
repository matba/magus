function utilityClass(){
	var utility ={};
	utility.getEntityFragment = function(uri){
		if(uri.lastIndexOf("#")!=-1)
			return uri.substring(uri.lastIndexOf("#")+1);
		else
			if(uri.lastIndexOf("/")!=-1)
				return uri.substring(uri.lastIndexOf("/")+1);
			else
				return uri
	}
	utility.clean = function(inp){
		if(inp.startsWith("#")){
			return inp.substring(inp.lastIndexOf("#")+1);
		}
		else
		{
			return inp;
		}
	}
	utility.isAbsolute =function(inp){
		return (inp.lastIndexOf("://")>-1);
	}
	utility.guid = function() {
		  function s4() {
		    return Math.floor((1 + Math.random()) * 0x10000)
		      .toString(16)
		      .substring(1);
		  }
		  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		    s4() + '-' + s4() + s4() + s4();
		}
	utility.sleep = function  (time) {
		  return new Promise((resolve) => setTimeout(resolve, time));
		}
	utility.getAddressFragment = function(uri){
		if(uri.lastIndexOf("/")!=-1)
			return uri.substring(0,uri.lastIndexOf("/")+1);
		else
			return "";
	}
	utility.isURL = function(input){
		if(input.lastIndexOf("/")!=-1)
			return true;
		else
			return false;
	}
	utility.isAbsoluteAddress = function(input){
		if(input.lastIndexOf("://")!=-1)
			return true;
		else
			return false;
	}
	utility.showMessage= function(title,message){
		$('#messageboxModal_title').html(title);
		$('#messageboxModal_text').html(message);
		$('#messageboxModal').modal('show');
	}
	return utility;
	
	
}
