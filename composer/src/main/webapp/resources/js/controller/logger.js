function loggerClass(){
	var logger ={};
	logger.cid ='#loggingArea';
	
	logger.addToLog = function(text){
		$(logger.cid).val($(logger.cid).val()+'\r\n'+text);
		$(logger.cid ).scrollTop($(logger.cid )[0].scrollHeight);
	}
	
	logger.clear = function(){
		$(logger.cid).val("");
	}
	
	logger.getLog = function(){
		return $(logger.cid).val();
	}
	
	
	return logger;
}
	
	
	
	