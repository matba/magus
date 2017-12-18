
function serviceMashupFamilyClass(serviceMashupFamilyUri){
	var smf ={};
	smf.uri = serviceMashupFamilyUri;
	smf.featureModelAddress=null;
	smf.contextOntologyAddresses=[];
	smf.serviceAddresses =[]
	
	
	smf.parse = function(xml,fileAddress){
		
		 
		 xmlDoc = $.parseXML( xml );
		 $xml = $( xmlDoc );
		 
		 
			
		 var childNodes =  $($xml.children()[0]).children()
		 
		 for(var i = 0; i< childNodes.length; i++)
		 {
			 if(childNodes[i].nodeName=='ontologies' ){
				 var ontologiesChilds =$(childNodes[i]).children();
				 
				 for(var acntr = 0; acntr< ontologiesChilds.length; acntr++)
				 {
					 if(ontologiesChilds[acntr].nodeName=="ontology" ){
						var address =  $(ontologiesChilds[acntr]).attr('address');
						if(!utility.isAbsoluteAddress(address))
							address = utility.getAddressFragment( fileAddress)+address;
						smf.contextOntologyAddresses.push(address);
						 
					 }
					 
				 }
				 
			 }
			 if($($xml.children()[0]).children()[i].nodeName=='featuremodel' ){
				var address =  $(childNodes[i]).attr('address');
				if(!utility.isAbsoluteAddress(address))
					address = utility.getAddressFragment( fileAddress)+address;
				smf.featureModelAddress=address;
				 
			 }
			 if(childNodes[i].nodeName=='services' ){
				 var servicesChilds =$(childNodes[i]).children();
				 
				 for(var acntr = 0; acntr< servicesChilds.length; acntr++)
				 {
					 if(servicesChilds[acntr].nodeName=="service" ){
						 var address = $(servicesChilds[acntr]).attr('address');
							if(!utility.isAbsoluteAddress(address))
								address = utility.getAddressFragment( fileAddress)+address;
						smf.serviceAddresses.push(address);
						 
					 }
					 
				 }
				 
			 }
			 
		 }
		 //console.log(context.contextFactTypes);
	}
	
	
	return smf;
}
