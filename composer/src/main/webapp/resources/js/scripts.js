
var bpelCodeMirror = CodeMirror(document.getElementById("bpelCodeEditor"), {
  lineNumbers: true,
  value: "",
  mode:  "xml"
});

//$.ajax({
//    type: "GET",
//    url: "flow.xml",
//	contextType: "text/plain",
//    dataType: "text",
//    success: function(data){
//		var newdoc =  CodeMirror.Doc(data, "xml");
//		bpelCodeMirror.swapDoc(newdoc);
//  },
//  error: function() {
//    alert("There was an error reading feature model file");
//  }
//  });


function clearResultTabs(){
	d3.select("#workflow").select("#maingroup").selectAll("*").remove();
	d3.select("#bpelflow").select("#maingroup").selectAll("*").remove();
	var newdoc =  CodeMirror.Doc("", "xml");
	bpelCodeMirror.swapDoc(newdoc);
	if($('#runningfeatureModelVissvg').length)
		$('#runningfeatureModelVissvg').remove();
}

function aboutClick(){
	utility.showMessage('About','<strong><i>MAGUS</i></strong> is a tool supported framework which allows end-users to build self-healing service mashups effortlessly. <br/> This tool has been developed at  magus.online has been developed in <i>Laboratory for Systems, Software, and Semantics <strong>(LS3)</strong></i> by Mahdi Bashari <br/> <img src=\"resources/img/ls3lab_logo.png\"/>');
}

function contactClick(){
	utility.showMessage('Contact','Feel free to contact me: <br/><strong>mbashari AT unb DOT ca</strong>');
	
}

function downloadBPELSVG(){
	var svgcontent =$("<div />").append($("#bpelflow").clone()).html();  
	download(svgcontent, "featuremodel.svg", "text/xml")
}

function progressRecursion(val){
//	utility.sleep(300).then(() => {
//	    // Do something after the sleep!
//		if(val <100)
//			{
//				$('#loadingModal_pb').css('width', val+'%').attr('aria-valuenow', val);
//				progressRecursion(val+5);
//			}
//		else{
//			$('#loadingModal').modal('hide');
//			utility.sleep(300).then(() => {
//			utility.showMessage('Welcome to magus.online','This tool has only been tested in Chrome browser.');
//			});
//		}
//	});
}

//$('#messageboxModal').modal('show');

jQuery(document).ready(function(){
	var progressValue=5;
	
	//progressRecursion(progressValue);
	
	var isChromium = window.chrome,
    winNav = window.navigator,
    vendorName = winNav.vendor,
    isOpera = winNav.userAgent.indexOf("OPR") > -1,
    isIEedge = winNav.userAgent.indexOf("Edge") > -1,
    isIOSChrome = winNav.userAgent.match("CriOS");
    
	if(isIOSChrome){
		
		
	} else if(isChromium !== null && isChromium !== undefined && vendorName === "Google Inc." && isOpera == false && isIEedge == false) {
		if(mashupfamilyAddress!=null )
		{
		SMComboSelected(serviceMashupList.length-1);
		loadSMClick();
		}
	} else { 
		$('#notsupportedModal').modal('show');
	}
	
});



