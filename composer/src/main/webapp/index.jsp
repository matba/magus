
<!DOCTYPE html>
<%@page import="com.sun.research.ws.wadl.Request"%>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
				<!-- <meta name="viewport" content="width=device-width, initial-scale=1"> -->
				<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
		<meta name="description" content="">
		<meta name="author" content="">
		<link rel="icon" href="./favicon.ico">

		<title>magus.online: Service Mashup Creator</title>

		<!-- Bootstrap core CSS -->
		

			<!-- IE10 viewport hack for Surface/desktop Windows 8 bug
<link href="../../assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">
-->
			<!-- Custom styles for this template -->
		<link href="./resources/ext/bootstrap/bootstrap.min.css" rel="stylesheet">
		
		<link href="./resources/css/bootstrapmod.css" rel="stylesheet">								
		<link href="./resources/css/fm.css" rel="stylesheet">
		<link href="./resources/css/workflow.css" rel="stylesheet">		
		<link href="./resources/css/bpelflow.css" rel="stylesheet">	
		<link href='https://fonts.googleapis.com/css?family=Fjalla+One' rel='stylesheet' type='text/css'>
		<link href="https://fonts.googleapis.com/css?family=Dancing+Script" rel="stylesheet"> 
		<link rel="stylesheet" href="./resources/ext/codemirror/codemirror.css">
		<link rel="stylesheet" href="./resources/ext/awesomplete/awesomplete.css">
		<link href="./resources/css/style.css" rel="stylesheet">
						<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
						<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]
<script src="../../assets/js/ie-emulation-modes-warning.js"></script>
-->
						<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
						<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->

	


	<script>
	
	var mashupfamilyAddress=null;
	
	<%
	String mashupfamilyAddress = request.getParameter("conf");
	
	if((mashupfamilyAddress!=null)&&(!mashupfamilyAddress.equals("")))
	{
	
	%>
	
	mashupfamilyAddress = "<%=mashupfamilyAddress%>";
	<%
	}
	%>
	
	</script>

	</head>

	<body>



		<nav class="navbar navbar-default ">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand logo-font" href="#" > <span style="color:#e05038"> magus</span><span style="color:#000000">.</span>online</a>
				</div>
				<div id="navbar" class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li class="active"><a href="#">Tool</a></li>
						<li><a onclick="aboutClick()" href="#about">About</a></li>
						<li><a onclick="contactClick()" href="#contact">Contact</a></li>
					</ul>
				</div><!--/.nav-collapse -->
			</div>
		</nav>

		<div class="container maincontainer " style="width:100%">


			<div class="col-xs-12 editor-container nopadding"  >

				<div class="col-xs-5 fm-container nopadding" >

<div class="col-xs-12  toolbar" >
<span>Mashup Family: </span>
<div class="dropdown" style="width:200px;display:inline" >
  <button class="btn btn-default dropdown-toggle btn-xs" type="button" id="SMSelectionCombo" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
    Select...
    <span class="caret"></span>
  </button>
  <ul class="dropdown-menu" id="SMSelectionUL" aria-labelledby="dropdownMenu1">
    <li><a onclick="" href="#">Order Processing(http://magus.online/repositories/orderprocessing/configuration.xml)</a></li>
  </ul>
</div>
<button class="btn btn-default btn-xs" onclick="loadSMClick();" ><span class="glyphicon glyphicon-upload" style="color:green" aria-hidden="true"></span> Load</button>
				<button class="btn btn-default btn-xs"  data-toggle="modal" data-target="#smLoad"><span class="glyphicon glyphicon-plus" style="color:blue" aria-hidden="true" ></span> Add</button>

				
</div>
					<div class="tabbable tabs-below"   style="margin-top:26px; height:100%">
						<div class="tab-content" id="fmtabs"  style="height:100px" >
							<div class="tab-pane  scrollable" id="contextModeltab">
								<div id="contextModelModal" class="modal-dialog" role="document" style="width:95%;margin-top: 10px;display:none">
								    <div class="modal-content">
								      <div class="modal-header">
								        
								        <h4 class="modal-title" id="myModalLabel">Context Model</h4>
								      </div>
								      <div class="modal-body">
											<form>
									          <div id="ontologyEdit_Alert" class="alert alert-danger" style="display:none" role="alert"></div>
											  <div class="form-group">
											    <label for="ontologyEdit_entitiesList">Entity Types</label>
											    <div  class="form-control" id="ontologyEdit_entitieTypeList"  style="height:auto; width:100%;  padding:2px">
											    
												
											    </div>
											    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToContext('entityType');"></span>
				
											  </div>
											  <div id="ontologyEdit_entityTypeAddDiv" class="addDivContainer">
												  <div class="form-group" >
													    <label for="ontologyEdit_entityInput">New Entity Type</label>
													    <input type="text" class="form-control" id="ontologyEdit_entityTypeInput" style="height:30px;" placeholder="New Entity Type">
													    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitToContext('entityType');"></span>
												  </div>
											  </div>
											  
											  <div class="form-group">
											    <label for="ontologyEdit_entitiesList">Entity Instances</label>
											    <div  class="form-control" id="ontologyEdit_instanceList"  style="height:auto; width:100%;  padding:2px">
											    
												
											    </div>
											    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToContext('instance');"></span>
				
											  </div>
											  <div id="ontologyEdit_InstanceAddDiv" class="addDivContainer">
											  
											  <div class="form-group" >
												    <label for="ontologyEdit_entityInput">New Instance Type</label>
												    <input type="text" class="form-control" id="ontologyEdit_entityInstanceTypeInput" style="height:30px;" placeholder="New Instance Type">
											  </div>
											  <div class="form-group" >
												    <label for="ontologyEdit_entityInput">New Instance Name</label>
												    <input type="text" class="form-control" id="ontologyEdit_entityInstanceNameInput" style="height:30px;" placeholder="New Instance Name">
												    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitToContext('instance');"></span>
											  </div>
											  </div>
											  
											  
											 
											  
											  <div class="form-group">
											    <label for="ontologyEdit_entitiesList">Fact Types</label>
											    <div  class="form-control" id="ontologyEdit_factTypeList"  style="height:auto; width:100%;  padding:2px">
											    
												
											    </div>
											    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToContext('factType');"></span>
				
											  </div>
											  <div id="ontologyEdit_FactTypeAddDiv" class="addDivContainer">
											  <div class="form-group" >
												    <label for="ontologyEdit_entityInput">Fact Type Name</label>
												    <input type="text" class="form-control" id="ontologyEdit_factTypeInput" style="height:30px;" placeholder="Fact Type Name">
											  </div>
											  <div class="form-group" >
												    <label for="ontologyEdit_entityInput">First Entity Type</label>
												    <input type="text" class="form-control" id="ontologyEdit_firstEntityTypeInput" style="height:30px;" placeholder="First Entity Type">
											  </div>
											  <div class="form-group" >
												    <label for="ontologyEdit_entityInput">Second Entity Type</label>
												    <input type="text" class="form-control" id="ontologyEdit_secondEntityInput" style="height:30px;" placeholder="Second Entity Type">
												    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitToContext('factType');"></span>
											  </div>
											  </div>
											</form>
										</div>
										<div class="modal-footer">
									        	
									        <button type="button" class="btn btn-default btn-xs" onclick="downloadCM();"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true" ></span> Download</button>
									    </div>
									</div>
								</div>
							
							
							
							</div>
							<div class="tab-pane  scrollable" id="serviceAnnotationtab">
							<div id="serviceAnnotationtabToolbar" style="width:100%;height:30px;display:none">
							<button class="btn btn-default btn-xs" style="float:right;margin-right:10px;margin-top:7px;" data-toggle="modal" data-target="#modalServiceAdd"><span class="glyphicon glyphicon-plus" style="color:blue;" aria-hidden="true" ></span> Add Service</button>
							</div>
							<div id="serviceAnnotationContainer">
							
							</div>
							
							</div>
							<div class="tab-pane  scrollable" id="fmedittab">
							<div id="edittabFMContainer">
							<div style="position:absolute; top:35px; right:10px">
								<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="downloadFMSVG()"><span class="glyphicon glyphicon-download-alt" style="color:green;" aria-hidden="true"></span> DL As SVG</button>
								<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="downloadFM()"><span class="glyphicon glyphicon-download-alt" style="color:green;" aria-hidden="true"></span> Download FM</button>
								<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="transferToConfiguration();"><span class="glyphicon glyphicon-transfer" style="color:green;" aria-hidden="true"></span> Update Configuration</button>
								
							
							 </div>
							
							
							
							</div>
							
							</div>
							<div class="tab-pane  active scrollable" id="fmconftab">
							<div id="conftabFMContainer">
							
							<div class="integritybox">
							<div style="font-weight: bold;font-style: italic;"> Integrity Constraints: </div>
							<div id="integrityConstraints">
							
							
							</div>
							
							</div>
							
							<div style="position:absolute; top:35px; right:10px">
								<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="generateMashup();"><span class="glyphicon glyphicon-cog" style="color:green;" aria-hidden="true"></span> Generate Mashup</button>
								<button class="btn btn-default btn-xs" style=" float:right" onclick="conffeatureModelVis.resetSelection();"><span class="glyphicon glyphicon-refresh" style="color:red;" aria-hidden="true"></span> Reset</button>
							
							 </div>
							
							
							</div>
							</div>

						</div>
						<ul class="nav nav-tabs inpage-nav-tab" >
							<li ><a href="#contextModeltab"  data-toggle="tab">Context Model</a></li>
							<li ><a href="#serviceAnnotationtab" id="serviceAnnotationlink"   data-toggle="tab">Services</a></li>
							<li ><a href="#fmedittab"  data-toggle="tab">Feature Model Editor</a></li>
							<li class="active"><a href="#fmconftab" id="fmconftablink" data-toggle="tab">Feature Model Configurator</a></li>
		
						</ul>
					</div>
				</div>

				<div class="col-xs-7 flow-container nopadding"   >

					<div class="tabbable tabs-below"  style="height:100%"  >
						<div class="tab-content" id="flowtabs"  style="height:100px" >
							<div class="tab-pane  scrollable" id="runningfmtab">
							<div id="runningFMContainer">
							<div class="col-xs-12  toolbar" >

							</div>
							<div style="position:absolute; top:35px; right:10px">
								<div class="rfmLegend" style="color:#ff0000;">Requested Feature</div>
								<div class="rfmLegend" style="color:#0000ff;" >Service Mashup Feature</div>
								<div class="rfmLegend" style="color:#ff00ff;" >Requested &amp; Service Mashup Feature</div>
							 </div>
							
							
							
							</div>
							
							</div>
						
							<div class="tab-pane scrollable" id="workflowtab">
							<div class="col-xs-12  toolbar" >
<button class="btn btn-default btn-xs" ><span class="glyphicon glyphicon-save-file" style="color:white" aria-hidden="true"></span> Save</button>
				<button class="btn btn-default btn-xs" style="float:right" > <span class="glyphicon glyphicon-retweet" style="color:white" aria-hidden="true"></span> Redraw</button>
							</div>
							<svg id="workflow">
							<defs>
							<marker id="markerArrow" markerWidth="13" markerHeight="13" refX="2" refY="6"
									   orient="auto"  markerUnits="strokeWidth" >
							<path d="M2,2 L2,11 L10,6 L2,2" style="fill: #999;" />
							</marker>
							
							</defs>
							<g id="maingroup">
								
								</g>
							</svg>
							
							</div>
							<div class="tab-pane  scrollable" id="bpelGraphTab">
							<div class="col-xs-12  toolbar" >
<button class="btn btn-default btn-xs" onclick="download(bpelXMLDoc, 'bpel.xml', 'text/xml');" ><span class="glyphicon glyphicon-save-file" style="color:white" aria-hidden="true"></span> Save</button>
				<button class="btn btn-default btn-xs" onclick="window.prompt('Copy to clipboard: Ctrl+C, Enter', bpelXMLDoc);"  > <span class="glyphicon  glyphicon-copy" style="color:white" aria-hidden="true"></span> Copy to clipboard</button>
				<button class="btn btn-default btn-xs" onclick="downloadBPELSVG();" ><span class="glyphicon glyphicon-save-file" style="color:white" aria-hidden="true"></span> DL as SVG</button>

<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="utility.showMessage('Not available','Deploying is not available in this version!')"><span class="glyphicon glyphicon-share-alt" style="color:green;" aria-hidden="true"></span> Deploy</button>
<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="utility.showMessage('How to change service availability','In order make a service in the BPEL process unavailable click on it. You can also change or restore service availability by clicking on its availability (top-right) button in Services tab. The service mashup will adapt if needed when service availability is changed.')" ><span class="glyphicon glyphicon-alert" style="color:yellow;" aria-hidden="true"></span> How To Change Service Availability</button>
							
</div>


							<svg id="bpelflow" width="50" height="50">
								<defs>
									<linearGradient id="evengrad" x1="0%" y1="0%" x2="0%" y2="100%">
									  <stop offset="0%" style="stop-color:rgb(255,255,255);stop-opacity:1" />
									  <stop offset="100%" style="stop-color:rgb(240,240,240);stop-opacity:1" />
									</linearGradient>
									<radialGradient id="stgrad" cx="60%" cy="30%" r="50%" fx="50%" fy="50%">
									  <stop offset="0%" style="stop-color:rgb(255,255,255);
									  stop-opacity:0" />
									  <stop offset="100%" style="stop-color:rgb(115,161,117);stop-opacity:1" />
									</radialGradient>
									<radialGradient id="engrad" cx="60%" cy="30%" r="50%" fx="50%" fy="50%">
									  <stop offset="0%" style="stop-color:rgb(255,255,255);
									  stop-opacity:0" />
									  <stop offset="100%" style="stop-color:rgb(102,119,153);stop-opacity:1" />
									</radialGradient>
									<marker id="markerArrow" markerWidth="13" markerHeight="13" refX="2" refY="6"
															   orient="auto"  markerUnits="strokeWidth" >
													<path d="M2,2 L2,11 L10,6 L2,2" style="fill: #999;" />
									</marker>
									<marker id="markerArrowLink" markerWidth="8" markerHeight="8" refX="2" refY="6"
									   orient="auto"  markerUnits="strokeWidth" >
							<path d="M2,2 L2,11 L10,6 L2,2" style="fill: #FA1414;" />
							</marker>
								</defs>
								
								<g id="maingroup">
								
								</g>
								
							
							</svg>
							</div>
							<div class="tab-pane  active" id="bpelCodeTab" >
							<div class="col-xs-12  toolbar" >
<button class="btn btn-default btn-xs" onclick="download(bpelXMLDoc, 'bpel.xml', 'text/xml');" ><span class="glyphicon glyphicon-save-file" style="color:white" aria-hidden="true"></span> Save</button>
				<button class="btn btn-default btn-xs" onclick="window.prompt('Copy to clipboard: Ctrl+C, Enter', bpelXMLDoc);" > <span class="glyphicon  glyphicon-copy" style="color:white" aria-hidden="true"></span> Copy to clipboard</button>

<button class="btn btn-default btn-xs" style=" float:right;margin-left:5px" onclick="utility.showMessage('Not available','Deploying is not available in this version!')"><span class="glyphicon glyphicon-share-alt" style="color:green;" aria-hidden="true"></span> Deploy</button>
</div>
<div id="bpelCodeEditor" style="height:auto" >


</div>
	
							
							</div>
						</div>
						<ul class="nav nav-tabs inpage-nav-tab"  >
							<li ><a href="#runningfmtab"  data-toggle="tab">Running FM</a></li>
							<li ><a href="#workflowtab"  data-toggle="tab">Workflow Graph</a></li>
							<li><a href="#bpelGraphTab" data-toggle="tab">BPEL Graph</a></li>
							<li class="active"><a href="#bpelCodeTab" data-toggle="tab">BPEL Code</a></li>
						</ul>
					</div>

					<!-- tabs bottom -->




				</div>

			</div>

			<div class="col-xs-12 console-container nopadding"   >

				<div class="col-xs-12 toolbar" >
				<button class="btn btn-default btn-xs" onclick="download(logger.getLog(), 'log.txt', 'text/plain');" ><span class="glyphicon glyphicon-save-file" style="color:white" aria-hidden="true" ></span> Save</button>
				<button class="btn btn-default btn-xs" onclick="window.prompt('Copy to clipboard: Ctrl+C, Enter', logger.getLog());" > <span class="glyphicon  glyphicon-copy" style="color:white" aria-hidden="true" ></span> Copy to clipboard</button>
				<button class="btn btn-default btn-xs" onclick="logger.clear();"> <span class="glyphicon glyphicon-remove" style="color:red" aria-hidden="true" ></span> Clear</button>
				</div>	
				
				<div class="tabbable tabs-below" style="margin-top:26px"   style=" height:100%">
					
					<div class="tab-content" id="bottomtabs"  style="height:100px" >
						<div class="tab-pane active" id="logtab" style="height:100%">
						<textarea id="loggingArea" class="logging form-control " style="height:100%;" readonly></textarea>
						</div>
						<div class="tab-pane" id="errortab" style="height:100%" >
						<textarea class="logging form-control "  style="height:100%" readonly></textarea>
						</div>

					</div>
					<ul class="nav nav-tabs inpage-nav-tab" >
						<li class="active"><a href="#logtab"  data-toggle="tab"><span class="glyphicon glyphicon-pencil" style="color:black" aria-hidden="true"></span> Log</a></li>
						<li><a href="#errortab" data-toggle="tab"><span class="glyphicon glyphicon-remove-circle" style="color:red" aria-hidden="true"></span> Errors</a></li>

					</ul>
				</div>

				<!-- tabs bottom -->



				<!-- /tabs -->


			</div>

		</div><!-- /.container -->

	<!-- Modals -->
	
	<!-- Annotation Edit Model -->
	<div class="modal fade" id="featureEdit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel">Edit Feature</h4>
	      </div>
	      <div class="modal-body">
	        <form>
	          <div class="form-group">
			    <label for="featureNameInput">Name</label>
			    <input type="text" class="form-control" id="featureEdit_featureNameInput" style="height:30px;" placeholder="Feature Name">
			  </div>
			  <div class="form-group">
			    <label for="featureEdit_entitiesList">Entities Set</label>
			    <div  class="form-control" id="featureEdit_entitiesList"  style=" width:568px; overflow-y: auto; padding:2px">
			    
				
			    </div>
			    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addAnnotation('entity');"></span>
			  </div>
			  <div class="form-group">
			    <label for="featureEdit_preconditionsList">Preconditions Set</label>
			    <div  class="form-control" id="featureEdit_preconditionsList"  style=" width:568px; overflow-y: auto;padding:2px">
			    
				
			    </div>
			    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addAnnotation('precondition');"></span>
			  </div>
			  <div class="form-group">
			    <label for="featureEdit_effectsList">Effects Set</label>
			    <div  class="form-control" id="featureEdit_effectsList"  style=" width:568px; overflow-y: auto; padding:2px">
				
			    </div>
			    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addAnnotation('effect');"></span>
			  </div>
			  <div id="featureEdit_factAddDiv" style="display: none;">
			  <div class="form-group" >
			    <label for="featureEdit_factTypeInput">FactType</label>
			    <input type="text" class="form-control" id="featureEdit_factTypeInput" style="height:30px;" placeholder="Fact Type">
			  </div>
			  <div class="form-group" >
			    <label for="featureEdit_firstEntityInput">First Entity</label>
			    <input type="text" class="form-control" id="featureEdit_firstEntityInput" style="height:30px;" placeholder="First Entity">
			  </div>
			  <div class="form-group" >
			    <label for="featureEdit_secondEntityInput">Second Entity</label>
			    <input type="text" class="form-control" id="featureEdit_secondEntityInput" style="height:30px;" placeholder="Second Entity">
			     <span id="featureEdit_addFact" class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" ></span>
			  </div>
			  </div>
			  <div id="featureEdit_entityAddDiv" style="display: none;">
			  <div class="form-group" >
			    <label for="featureEdit_entityTypeInput">Entity Type</label>
			    <input type="text" class="form-control" id="featureEdit_entityTypeInput" style="height:30px;" placeholder="Fact Type">
			  </div>
			  <div class="form-group" >
			    <label for="featureEdit_entityInput">First Entity</label>
			    <input type="text" class="form-control" id="featureEdit_entityInput" style="height:30px;" placeholder="First Entity">
			    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="addEntity()"></span>
			  </div>
			  
			  </div>
			 <div id="featureEdit_Alert" class="alert alert-danger" style="display:none" role="alert"></div>
			</form>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" onclick="updateFMFeature();">Save changes</button>
	      </div>
	    </div>
	  </div>
	</div>
	<!-- Mashup Family Add Model -->
	
	<div class="modal fade" id="smLoad" tabindex="-1" role="dialog" aria-labelledby="smLoadLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel">Load Service Mashup Family</h4>
	      </div>
	      <div class="modal-body">
	      <div class="alert alert-danger" id="smLoad_errorBox" style="display:none" role="alert">The configuration file is not valid!</div>
			<form>
			  <div class="form-group">
			    <label for="smLoad_URL">Service Mashup Configuration Name</label>
			    <input type="text" class="form-control" id="smLoad_Name" placeholder="Configuration URL">
			  </div>
			  <div class="form-group">
			    <label for="smLoad_URL">Service Mashup Configuration URL</label>
			    <input type="text" class="form-control" id="smLoad_URL" placeholder="Configuration URL">
			  </div>
			  
			</form>
			<div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" onclick="loadSMFamily($('#smLoad_URL').val(),$('#smLoad_Name').val());$('#smLoad').modal('hide');">Load</button>
	      	</div>
		    </div>
		  </div>
		</div>
	</div>
	
	
	<!-- Service Add Model -->
	
	<div class="modal fade" id="modalServiceAdd" tabindex="-1" role="dialog" aria-labelledby="modalServiceAddLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel">Add Service</h4>
	      </div>
	      <div class="modal-body">
	      <div class="alert alert-danger" id="modalServiceAdd_errorBox" style="display:none" role="alert">The configuration file is not valid!</div>
			<form>
			  <div class="form-group">
			    <label for="modalServiceAdd_Name">Service Name</label>
			    <input type="text" class="form-control" id="modalServiceAdd_Name" placeholder="Service Name">
			  </div>
			  <div class="form-group">
			    <label for="modalServiceAdd_URI">Service Base URI</label>
			    <input type="text" class="form-control" id="modalServiceAdd_URI" placeholder="Service Base URI">
			  </div>
			  <div class="checkbox">
    				<label>
      				<input id="modalServiceAdd_hasCallback" type="checkbox"> Results in callback
    				</label>
  			  </div>
			</form>
			<div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" onclick="addService();">Add</button>
	      	</div>
		    </div>
		  </div>
		</div>
	</div>
	
	<!-- Service Mashup Add Model -->
	
	<div class="modal fade" id="modalSMAdd" tabindex="-1" role="dialog" aria-labelledby="modalSMAddLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel">Add Service Mashup Family</h4>
	      </div>
	      <div class="modal-body">
	      <div class="alert alert-danger" id="modalSMAdd_errorBox" style="display:none" role="alert">The configuration file is not valid!</div>
			<form>
			  <div class="form-group">
			    <label for="modalSMAdd_Name">Root Feature Name</label>
			    <input type="text" class="form-control" id="modalSMAdd_Name" placeholder="Root Feature Name">
			  </div>
			  <div class="form-group">
			    <label for="modalSMAdd_URI">Feature Model Base URI</label>
			    <input type="text" class="form-control" id="modalSMAdd_URI" placeholder="Feature Model Base URI">
			  </div>
			  <div class="form-group">
			    <label for="modalSMAdd_CMURI">Context Model Base URI</label>
			    <input type="text" class="form-control" id="modalSMAdd_CMURI" placeholder="Context Model Base URI">
			  </div>
			</form>
			<div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" onclick="addServiceMashupFamily();">Add</button>
	      	</div>
		    </div>
		  </div>
		</div>
	</div>
	
	
	<!--MessageBox Modal -->
	<div class="modal fade" id="messageboxModal" tabindex="-1" role="dialog">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="messageboxModal_title">Adaptation result</h4>
	      </div>
	      <div class="modal-body">
	        <p id="messageboxModal_text">
	        The service mashup functionality <b> <span class="label label-success">Recovered</span> </b> by adaptation through <b><span class="label label-success"><span class="glyphicon glyphicon-random" aria-hidden="true"></span> Replanning</span></b>.  
	        
	        </p>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<!--Please wait Modal -->
	<div class="modal fade" id="pleaseWaitModal" tabindex="-1" role="dialog">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-body">
	        <img src="./resources/img/loading.gif"/> Please wait...
	      </div>
	      
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<!--MessageBox Modal -->
	<div class="modal fade" id="loadingModal" tabindex="-1" role="dialog">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        
	        <h4 class="modal-title" id="loadingModal_title">Magus.Online is loading...</h4>
	      </div>
	      <div class="modal-body">
	        <div class="progress">
			  <div id="loadingModal_pb" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="5" aria-valuemin="0" aria-valuemax="100" style="width: 5%">
			    <span class="sr-only">5% Complete</span>
			  </div>
			</div>
	      </div>
	      
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<!--MessageBox Modal -->
	<div class="modal fade" id="notsupportedModal" tabindex="-1" role="dialog">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        
	        <h4 class="modal-title" id="loadingModal_title">Not Supported!</h4>
	      </div>
	      <div class="modal-body">
	        Magus.online only supports Chrome browser.
	      </div>
	      
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<!-- Templates -->
	
	<script id="serviceTemplate" type="x-tmpl-mustache">
		<div id="serviceEdit_serviceModal{{no}}" class="modal-dialog" role="document" style="width:95%;margin-top: 10px;display:block">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" id="serviceEdit_AvailableBtn{{no}}" style="float:right;background-color:green;color:white" class="btn btn-xs" onclick="toggleServiceAvailability({{no}});"><span class="glyphicon glyphicon-ok" aria-hidden="true" ></span> Available</button>
	        <h4 class="modal-title" id="serviceEdit_Name{{no}}">Service</h4>
	      </div>
	      <div class="modal-body">
				<form>
		         
				  <!-- service inputs -->
				  <div class="form-group">
				    <label for="serviceEdit_inputList{{no}}">Inputs</label>
				    <div  class="form-control" id="serviceEdit_inputList{{no}}"  style="height:auto; width:100%;  padding:2px">
				    
					
				    </div>
				    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('input',{{no}});"></span>

				  </div>
				  <div id="serviceEdit_InputAddDiv{{no}}" class="addDivContainer">
				  
					  <div class="form-group" >
						    <label for="serviceEdit_inputTypeInput{{no}}">Input Type</label>
						    <input type="text" class="form-control" id="serviceEdit_inputTypeInput{{no}}" style="height:30px;" placeholder="Input Type">
					  </div>
					  <div class="form-group" >
						    <label for="serviceEdit_inputNameInput{{no}}">Input Name</label>
						    <input type="text" class="form-control" id="serviceEdit_inputNameInput{{no}}" style="height:30px;" placeholder="Input Name">
						    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('input',{{no}});"></span>
					  </div>
				  </div>
				  
				  <!-- service output -->
				  <div class="form-group">
				    <label for="serviceEdit_outputList{{no}}">Outputs</label>
				    <div  class="form-control" id="serviceEdit_outputList{{no}}"  style="height:auto; width:100%;  padding:2px">
				    
					
				    </div>
				    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('output',{{no}});"></span>

				  </div>
				  <div id="serviceEdit_outputAddDiv{{no}}" class="addDivContainer">
				  
					  <div class="form-group" >
						    <label for="serviceEdit_outputTypeInput{{no}}">Output Type</label>
						    <input type="text" class="form-control" id="serviceEdit_outputTypeInput{{no}}" style="height:30px;" placeholder="Output Type">
					  </div>
					  <div class="form-group" >
						    <label for="serviceEdit_outputNameInput{{no}}">Output Name</label>
						    <input type="text" class="form-control" id="serviceEdit_outputNameInput{{no}}" style="height:30px;" placeholder="Output Name">
						    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('output',{{no}});"></span>
					  </div>
				  </div>
				 
				 
				 <!-- service vars -->
				  <div class="form-group">
				    <label for="serviceEdit_varList{{no}}">Variables</label>
				    <div  class="form-control" id="serviceEdit_varList{{no}}"  style="height:auto; width:100%;  padding:2px">
				    
					
				    </div>
				    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('var',{{no}});"></span>

				  </div>
				  <div id="serviceEdit_VarAddDiv{{no}}" class="addDivContainer">
				  
					  <div class="form-group" >
						    <label for="serviceEdit_varTypeInput{{no}}">Variable Type</label>
						    <input type="text" class="form-control" id="serviceEdit_varTypeInput{{no}}" style="height:30px;" placeholder="Variable Type">
					  </div>
					  <div class="form-group" >
						    <label for="serviceEdit_varNameInput{{no}}">Variable Name</label>
						    <input type="text" class="form-control" id="serviceEdit_varNameInput{{no}}" style="height:30px;" placeholder="Variable Name">
						    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('var',{{no}});"></span>
					  </div>
				  </div>
				  
				  <!-- service preconditions -->
				  

				  
				  <div class="form-group">
				    <label for="serviceEdit_preconditionList{{no}}">Preconditions</label>
				    <div  class="form-control" id="serviceEdit_preconditionList{{no}}"  style="height:auto; width:100%;  padding:2px">
				    
					
				    </div>
				    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('precondition',{{no}});"></span>

				  </div>
				  <div id="serviceEdit_PreconditionAddDiv{{no}}" class="addDivContainer">
				  <div class="checkbox">
    				<label>
      				<input id="serviceEdit_preconditionIsNot{{no}}" type="checkbox"> not
    				</label>
  				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_preconditionFactTypeInput{{no}}">Fact Type</label>
					    <input type="text" class="form-control" id="serviceEdit_preconditionFactTypeInput{{no}}" style="height:30px;" placeholder="Fact Type]">
				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_firstParameterInput{{no}}">First Parameter</label>
					    <input type="text" class="form-control" id="serviceEdit_preconditionFirstParameterInput{{no}}" style="height:30px;" placeholder="First Parameter">
				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_preconditionSecondParameterInput{{no}}">Second Parameter</label>
					    <input type="text" class="form-control" id="serviceEdit_preconditionSecondParameterInput{{no}}" style="height:30px;" placeholder="Second Parameter">
					    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('precondition',{{no}});"></span>
				  </div>
				  </div>
				  
				  <!-- service effect -->
				  
				  <div class="form-group">
				    <label for="serviceEdit_effectList{{no}}">Effects</label>
				    <div  class="form-control" id="serviceEdit_effectList{{no}}"  style="height:auto; width:100%;  padding:2px">
				    
					
				    </div>
				    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('effect',{{no}});"></span>

				  </div>
				  <div id="serviceEdit_EffectAddDiv{{no}}" class="addDivContainer">
				  <div class="checkbox">
    				<label>
      				<input id="serviceEdit_effectIsNot{{no}}" type="checkbox"> not
    				</label>
  				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_effectfactTypeInput{{no}}">Fact Type</label>
					    <input type="text" class="form-control" id="serviceEdit_effectfactTypeInput{{no}}" style="height:30px;" placeholder="Fact Type]">
				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_effectFirstParameterInput{{no}}">First Parameter</label>
					    <input type="text" class="form-control" id="serviceEdit_effectFirstParameterInput{{no}}" style="height:30px;" placeholder="First Parameter">
				  </div>
				  <div class="form-group" >
					    <label for="serviceEdit_effectSecondParameterInput{{no}}">Second Parameter</label>
					    <input type="text" class="form-control" id="serviceEdit_effectSecondParameterInput{{no}}" style="height:30px;" placeholder="Second Parameter">
					    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('effect',{{no}});"></span>
				  </div>
				  </div>
				  
				</form>
			</div>
			
				<div id="serviceEdit_callbackDiv{{no}}" style="display:none">
				  <div class="modal-header">
			        
			        <h4 class="modal-title" id="serviceEdit_callbackName{{no}}">Service Callback</h4>
			      </div>
			      <div class="modal-body">
						
						 <!-- service output -->
						  <div class="form-group">
						    <label for="serviceEdit_callbackoutputList{{no}}">Outputs</label>
						    <div  class="form-control" id="serviceEdit_callbackoutputList{{no}}"  style="height:auto; width:100%;  padding:2px">
						    
							
						    </div>
						    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('coutput',{{no}});"></span>

						  </div>
						  <div id="serviceEdit_callbackOutputAddDiv{{no}}" class="addDivContainer">
						  
							  <div class="form-group" >
								    <label for="serviceEdit_callbackOutputTypeInput{{no}}">Output Type</label>
								    <input type="text" class="form-control" id="serviceEdit_callbackOutputTypeInput{{no}}" style="height:30px;" placeholder="Output Type">
							  </div>
							  <div class="form-group" >
								    <label for="serviceEdit_callbackOutputNameInput{{no}}">Output Name</label>
								    <input type="text" class="form-control" id="serviceEdit_callbackOutputNameInput{{no}}" style="height:30px;" placeholder="Output Name">
								    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('coutput',{{no}});"></span>
							  </div>
						  </div>
						
						<!-- service preconditions -->
						  
						  <div class="form-group">
						    <label for="serviceEdit_callbackpreconditionList{{no}}">Preconditions</label>
						    <div  class="form-control" id="serviceEdit_callbackpreconditionList{{no}}"  style="height:auto; width:100%;  padding:2px">
						    
							
						    </div>
						    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('cprecondition',{{no}});"></span>

						  </div>
						  <div id="serviceEdit_callbackPreconditionAddDiv{{no}}" class="addDivContainer">
				  		  	<div class="checkbox">
    							<label>
      								<input id="serviceEdit_callbackPreconditionIsNot{{no}}" type="checkbox"> not
    							</label>
  				  			</div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackPreconditionFactTypeInput{{no}}">Fact Type</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackPreconditionFactTypeInput{{no}}" style="height:30px;" placeholder="Fact Type]">
						  </div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackPreconditionFirstParameterInput{{no}}">First Parameter</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackPreconditionFirstParameterInput{{no}}" style="height:30px;" placeholder="First Parameter">
						  </div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackPreconditionSecondParameterInput{{no}}">Second Parameter</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackPreconditionSecondParameterInput{{no}}" style="height:30px;" placeholder="Second Parameter">
							    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('cprecondition',{{no}});"></span>
						  </div>
						  </div>
						  
						  <!-- service effect -->
						  
						  <div class="form-group">
						    <label for="serviceEdit_callbackeffectList{{no}}">Effects</label>
						    <div  class="form-control" id="serviceEdit_callbackeffectList{{no}}"  style="height:auto; width:100%;  padding:2px">
						    
							
						    </div>
						    <span class="glyphicon  glyphicon-plus" style="color:green;float: right" aria-hidden="true" onclick="addToServiceAnnotation('ceffect',{{no}});"></span>

						  </div>
						  <div id="serviceEdit_callbackEffectAddDiv{{no}}" class="addDivContainer">
				  			<div class="checkbox">
    							<label>
      								<input id="serviceEdit_callbackEffectIsNot{{no}}" type="checkbox"> not
    							</label>
  				  			</div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackEffectFactTypeInput{{no}}">Fact Type</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackEffectFactTypeInput{{no}}" style="height:30px;" placeholder="Fact Type]">
						  </div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackEffectFirstParameterInput{{no}}">First Parameter</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackEffectFirstParameterInput{{no}}" style="height:30px;" placeholder="First Parameter">
						  </div>
						  <div class="form-group" >
							    <label for="serviceEdit_callbackEffectSecondParameterInput{{no}}">Second Parameter</label>
							    <input type="text" class="form-control" id="serviceEdit_callbackEffectSecondParameterInput{{no}}" style="height:30px;" placeholder="Second Parameter">
							    <span class="glyphicon  glyphicon-arrow-up" style="color:green;float: right" aria-hidden="true" onclick="commitServiceAnnotation('ceffect',{{no}});"></span>
						  </div>
						  </div>
						
				  </div>

			</div>	
<div class="modal-footer">
									        	
									        <button type="button" class="btn btn-default btn-xs" onclick="downloadService({{no}});"><span class="glyphicon glyphicon-arrow-down" aria-hidden="true" ></span> Download</button>
									    </div>  
		</div>
	</div>					


	</script>
	<script id="annotationTemplate" type="x-tmpl-mustache">
		<div id="annotationbox" class="annotationbox" style="left:{{leftLocation}}px;top:{{rightLocation}}px;" >
			<div style="position: absolute;right:10px; top:0px; " ><a style="color:red" href="#" onclick="$('#annotationbox').remove()">x</a></div>

			<div class="lineDiv  annotationLine" style="">E:</div><div class="lineDiv annotationLineVal" style="">{{{enititiesList}}}</div><br/>
			<div class="lineDiv  annotationLine math-font bold" style="">P:</div><div class="lineDiv annotationLineVal" style="">{{{preconditionList}}}</div><br/>
			<div class="lineDiv  annotationLine math-font bold" style="">E:</div><div class="lineDiv annotationLineVal" style="">{{{effectList}}}</div><br/>
			<div class="lineDiv" style="float:right" >

				<div class="dropdown" style="width:200px;display:inline" >
  					<button class="btn btn-default dropdown-toggle btn-xs" type="button" id="addFeatureDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
    					<span class="glyphicon  glyphicon-plus" style="color:green" aria-hidden="true"></span>Add
    					<span class="caret"></span>
  					</button>
  					<ul class="dropdown-menu" aria-labelledby="addFeatureDropdown">
    					<li><a href="#" onclick="addChild('mandatory','{{uuid}}',{{featureNumber}} );">Mandatory Child</a></li>
						<li><a href="#" onclick="addChild('optional','{{uuid}}',{{featureNumber}} );">Optional Child</a></li>
						<li><a href="#" onclick="addChild('alternative','{{uuid}}',{{featureNumber}} );">Alternative Group</a></li>
						<li><a href="#" onclick="addChild('orgroup','{{uuid}}',{{featureNumber}} );">Or Group</a></li>
  					</ul>
				</div>

				
				<button class="btn btn-default btn-xs" onclick="removeFeature('{{uuid}}',{{featureNumber}});" > <span class="glyphicon  glyphicon-minus" style="color:red" aria-hidden="true"></span> Delete</button>
				<button class="btn btn-default btn-xs" onclick="loadFMAnnotationDialog('{{uuid}}');$('#featureEdit').modal('show')" > <span class="glyphicon  glyphicon-pencil" style="color:orange" aria-hidden="true"></span> Edit</button>
			</div>
		</div>
	</script>
	
	
	
	
	
										
	<!-- Placed at the end of the document so the pages load faster -->
	
	<script src="./resources/ext/jquery/jquery.min.js"></script>
	<script src="./resources/ext/bootstrap/bootstrap.min.js"></script>
	<script src="./resources/ext/d3/d3.v3.min.js"></script>
	<script src="./resources/ext/mustache/mustache.min.js"></script>
	<script src="./resources/ext/awesomplete/awesomplete.min.js"></script>
	<script src="./resources/ext/codemirror/codemirror.js"></script>
	<script src="./resources/ext/codemirror/javascript.js"></script>
	<script src="./resources/ext/codemirror/xml.js"></script>
	<script src="./resources/ext/dlfile/download.js"></script>
	
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug 
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>-->
	<script src="./resources/js/controller/globals.js"></script>
	<script src="./resources/js/utility.js"></script>
	
	<script src="./resources/js/model/context.js"></script>
	<script src="./resources/js/model/featureModel.js"></script>
	<script src="./resources/js/model/service.js"></script>
	<script src="./resources/js/model/serviceMashupFamily.js"></script>
	
	<script src="./resources/js/controller/logger.js"></script>
	<script src="./resources/js/controller/featureModelVisualization.js"></script>
	<script src="./resources/js/controller/featureModel.js"></script>
	<script src="./resources/js/controller/context.js"></script>
	<script src="./resources/js/controller/service.js"></script>
	<script src="./resources/js/controller/serviceMashupFamily.js"></script>
	
	<script src="./resources/js/workflow.js"></script>
	<script src="./resources/js/bpelgraph.js"></script>
	
	<!--
	<script src="./resources/ext/codemirror/runmode.js"></script>
	<script src="./resources/ext/codemirror/colorize.js"></script>
	
	<script src="./resources/ext/codemirror/css.js"></script>
	<script src="./resources/ext/codemirror/htmlmixed.js"></script>-->
	<script src="./resources/js/scripts.js"></script>
	<script src="./resources/js/controller/resizehandler.js"></script>
</body>
</html>
																														