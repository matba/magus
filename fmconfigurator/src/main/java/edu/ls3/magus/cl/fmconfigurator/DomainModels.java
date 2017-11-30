package edu.ls3.magus.cl.fmconfigurator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceTypeMap;
import edu.ls3.magus.cl.contextmanager.basic.StateFactTypeMap;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSet;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSetNFAnnotationMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAnnotationSet;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAtomicSetMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetric;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotation;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotationMap;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.utility.UtilityClass;
import smile.regression.OLS;

public class DomainModels {
	
	private static final int noOfObservationPerRegressand=10;
	
	private ContextModel contextModel;
	private ServiceCollection serviceCollection;
	private FeatureModel featureModel;
	private FeatureAnnotationSet featureModelAnnotation;
	

	public DomainModels() {
		contextModel = new ContextModel();
		serviceCollection = new ServiceCollection();
		setFeatureModel(null);
		setFeatureModelAnnotation(new FeatureAnnotationSet());
		
	}

	public static DomainModels ReadModels(String serviceFolder) throws Exception {
		DomainModels result = new DomainModels();

		InstanceTypeMap types = new InstanceTypeMap();
		StateFactTypeMap statefacttypes = new StateFactTypeMap();

		// File folder= new File("D:\\tt\\owlintact2");
		File folder = new File(serviceFolder);
		List<File> alreadyProcessed = new ArrayList<File>();

		for (File fXmlFile : folder.listFiles()) {
			if (alreadyProcessed.contains(fXmlFile))
				continue;
			File invocationFile = fXmlFile;
			File callbackFile = null;
			if (fXmlFile.getName().startsWith("R")) {
				String invocationFileName = "I" + fXmlFile.getName().substring(1);
				for (File f : folder.listFiles()) {
					if (f.getName().equals(invocationFileName)) {
						invocationFile = f;
						callbackFile = fXmlFile;
						alreadyProcessed.add(f);
						break;
					}
				}

			} else {
				if (fXmlFile.getName().startsWith("I")) {
					String callbackFileName = "R" + fXmlFile.getName().substring(1);
					for (File f : folder.listFiles()) {
						if (f.getName().equals(callbackFileName)) {
							callbackFile = f;
							alreadyProcessed.add(f);
							break;
						}
					}

				}
			}

			Service s = ServiceCollection.readServiceOld(invocationFile, statefacttypes, types, null, null, null);

			result.getServiceCollection().getServices().add(s);
			if (callbackFile != null) {
				Service sc = ServiceCollection.readServiceOld(callbackFile, statefacttypes, types, s.getInputList(),
						s.getInputs(), s);
				s.setReceiveService(sc);
			}

		}
		result.setContextModel(new ContextModel(types, statefacttypes, new ArrayList<Instance>()));

		return result;
	}

	public ContextModel getContextModel() {
		return contextModel;
	}

	public void setContextModel(ContextModel contextModel) {
		this.contextModel = contextModel;
	}

	public ServiceCollection getServiceCollection() {
		return serviceCollection;
	}

	public void setServiceCollection(ServiceCollection serviceCollection) {
		this.serviceCollection = serviceCollection;
		
		
			
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}

	public FeatureAnnotationSet getFeatureModelAnnotation() {
		return featureModelAnnotation;
	}

	public void setFeatureModelAnnotation(FeatureAnnotationSet featureModelAnnotation) {
		this.featureModelAnnotation = featureModelAnnotation;
	}

	public static DomainModels readFromDirectory(File curDir) throws Exception {
		String curDirAddress = curDir.getAbsolutePath();
		if (!curDirAddress.endsWith("/"))
			curDirAddress = curDirAddress + "/";
		String configurationFileAddress = curDirAddress + "configuration.xml";
		return readFromConfigurationFile(configurationFileAddress);
	}

	public static DomainModels readFromConfigurationFile(String configurationFileAddress) throws Exception {
		String curDirAddress = configurationFileAddress.substring(0, configurationFileAddress.lastIndexOf("/")+1 );
		DomainModels dm = new DomainModels();

		String configurationXml = UtilityClass.readFile(configurationFileAddress);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(configurationXml));
		Document doc = dBuilder.parse(is);

		String rootExpression = "//*[name()='configuration']";
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

		NodeList childNodes = root.getChildNodes();

		List<String> contextModelAddress = new ArrayList<String>();
		String fmAddress="";
		List<String> serviceAddress = new ArrayList<String>();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node curChildNode = childNodes.item(i);
			if (curChildNode.getNodeName().equals("ontologies")) {
				NodeList ontologiesChilds = curChildNode.getChildNodes();

				for (int acntr = 0; acntr < ontologiesChilds.getLength(); acntr++) {
					if (ontologiesChilds.item(acntr).getNodeName().equals("ontology")) {
						String address = ontologiesChilds.item(acntr).getAttributes().getNamedItem("address")
								.getNodeValue();
						if (!address.startsWith("/"))
							address = curDirAddress + address;
						contextModelAddress.add(address);

					}

				}

			}
			if (curChildNode.getNodeName().equals("featuremodel")) {
				String address = curChildNode.getAttributes().getNamedItem("address").getNodeValue();
				if (!address.startsWith("/"))
					address = curDirAddress + address;
				fmAddress = address;

			}
			if (curChildNode.getNodeName().equals("services")) {

				NodeList servicesChilds = curChildNode.getChildNodes();

				for (int acntr = 0; acntr < servicesChilds.getLength(); acntr++) {
					if (servicesChilds.item(acntr).getNodeName().equals("service")) {
						String address = servicesChilds.item(acntr).getAttributes().getNamedItem("address")
								.getNodeValue();
						if (!address.startsWith("/"))
							address = curDirAddress + address;

						serviceAddress.add(address);

					}

				}

			}
		}
		
		String fmStr =UtilityClass.readFile(fmAddress);
		String cmStr= UtilityClass.readFile(contextModelAddress.get(0));
		
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		dm.setContextModel(cm);
		
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		
		dm.setFeatureModel(fm);
		dm.setFeatureModelAnnotation(fma);
		
		List<Service> services =new ArrayList<Service>();
		
		for(String curSvAddress: serviceAddress)
		{
			String owlFile = UtilityClass.readFile(curSvAddress);
			List<Service> s= Service.parseService(owlFile, cm);
			services.addAll(s);
		}
		
		dm.setServiceCollection(new ServiceCollection(services));

		return dm;
	}

	public void writeToDirectory(String directory) throws IOException, ParserConfigurationException, TransformerException {
		if(!directory.endsWith("/"))
			directory= directory+"/";
		//write context model file
		UtilityClass.writeFile(new File(directory+"contextModel.xml") , getContextModel().serializeToXml());
		
		//write fm file
		UtilityClass.writeFile(new File(directory+"fm.xml") , getFeatureModel().serializeToXml(getFeatureModelAnnotation()));
		
		//write services
		UtilityClass.createFolder(directory+"services");
		getServiceCollection().writeToDirectory(directory+"services");
		List<String> serviceAddresses = new ArrayList<String>();
		
		for(Service s: getServiceCollection().getServices()){
			
			serviceAddresses.add("services/"+s.getName()+".xml");
		}
		
		
		//write configuration file
		List<String>  contextModelAddresses = new ArrayList<String>();
		contextModelAddresses.add("contextModel.xml");
		
		DomainModelConfiguration dmc = new DomainModelConfiguration(contextModelAddresses, "fm.xml", serviceAddresses);
		
		
		UtilityClass.writeFile(new File(directory+"configuration.xml") , dmc.serializeToConfigurationFileXml());
		
	}
//	
//	public  Map<AtomicSet, Double> findAtomicSetContributionValue(FeatureAtomicSetMap fasm, ServiceNonfunctionalAnnotationMap am, NonfunctionalMetricType nmt, Map<FeatureModelConfiguration, FlowComponentNode> serviceMashupCache) throws Exception{
//		Map<AtomicSet,Double> result = new HashMap<AtomicSet, Double>();
//		
//		Map<Service, ServiceNonfunctionalAnnotation> map = am.getAnnotationMap();
//		
//		
//		
//		// create the dataset
//		
//		List<FeatureModelConfiguration> observationFMCs  = getFeatureModel().generateRegressionConfigurations(noOfObservationPerRegressand,fasm);
//		
//		
//		List<AtomicSet> allAtomicSets = fasm.getAllAtomicSets(false);
//		
//		int noOfRegressors = allAtomicSets.size();
//		
//		int noOfObservation =observationFMCs.size();
//		
//		
//		
//		
//		// create the matrix
//		
//		double[] y = new double[noOfObservation];
//		double[][] x = new double[noOfObservation][noOfRegressors];
//		
//		
//		for(int cntr=0;cntr<noOfObservation; cntr++){
//			
//			
//			
//			
//			System.out.println(observationFMCs.get(cntr).toString());
//			
//			y[cntr] = findFeatureModelConfigurationNonfunctionalValue(observationFMCs.get(cntr),  nmt,  map,serviceMashupCache);
//			
//			//find atomic set assignment array
//			
//			for(int ascntr =0; ascntr< allAtomicSets.size(); ascntr++){
//				AtomicSet as = allAtomicSets.get(ascntr);
//				if(observationFMCs.get(cntr).getFeatureAtomicSetStatus(as.getFeatureList())){
//					x[cntr][ascntr]=1;
//				}
//				else
//				{
//					x[cntr][ascntr]=0;
//				}
//			}
//			
//		}
//		
//		
//		
//		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
//		
//		regression.setNoIntercept(true);
//		
//		
//		regression.newSampleData(y, x);
//		
//		double[] beta = regression.estimateRegressionParameters();    
//		
//		double regressandVariance = regression.estimateRegressandVariance();
//
//		double rSquared = regression.calculateRSquared();
//
//		double sigma = regression.estimateRegressionStandardError();
//		
//		double adjRSquared = regression.calculateAdjustedRSquared();
//		
//		System.out.println("Regressand variance:" +regressandVariance + " Rsquared: "+ rSquared +" Adjusted RSquared: "+ adjRSquared+ " sigma: "+ sigma);
//		
//		
//		
//		for(int cnt=0; cnt<allAtomicSets.size(); cnt++){
//			result.put(allAtomicSets.get(cnt), beta[cnt]);
//		}
//		
//		return result;
//	}
//	
//	
//	public  Map<AtomicSet, Double> findAtomicSetContributionValue(FeatureAtomicSetMap fasm, ServiceNonfunctionalAnnotationMap am, NonfunctionalMetricType nmt, List<FeatureModelConfiguration> observationFMCs,Map<FeatureModelConfiguration, FlowComponentNode> serviceMashupCache) throws Exception{
//		Map<AtomicSet,Double> result = new HashMap<AtomicSet, Double>();
//		
//		Map<Service, ServiceNonfunctionalAnnotation> map = am.getAnnotationMap();
//		
//		List<AtomicSet> allAtomicSets = fasm.getAllAtomicSets(true);
//		
//		int noOfRegressors = allAtomicSets.size();
//		
//		// create the dataset
//		
//		
//		
//		int noOfObservation =observationFMCs.size();
//		
//		
//		
//		
//		// create the matrix
//		
//		double[] y = new double[noOfObservation];
//		double[][] x = new double[noOfObservation][noOfRegressors];
//		
//		
//		for(int cntr=0;cntr<noOfObservation; cntr++){
//			
//			
//			System.out.println(observationFMCs.get(cntr).toString());
//			
//			y[cntr] = nmt.getRegressionValue(findFeatureModelConfigurationNonfunctionalValue(observationFMCs.get(cntr),  nmt,  map,serviceMashupCache));
//			
//			//find atomic set assignment array
//			
//			for(int ascntr =0; ascntr< allAtomicSets.size(); ascntr++){
//				AtomicSet as = allAtomicSets.get(ascntr);
//				if(observationFMCs.get(cntr).getFeatureAtomicSetStatus(as.getFeatureList())){
//					x[cntr][ascntr]=1;
//				}
//				else
//				{
//					x[cntr][ascntr]=0;
//				}
//			}
//			
//		}
//		double[] beta = null;
//		
//		for(int tryCntr=0; tryCntr<50;tryCntr++){
//			
//			try{
//			
//				OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
//				
//				regression.setNoIntercept(true);
//				
//				
//				regression.newSampleData(y, x);
//				
//				beta = regression.estimateRegressionParameters();    
//				
//				double regressandVariance = regression.estimateRegressandVariance();
//		
//				double rSquared = regression.calculateRSquared();
//		
//				double sigma = regression.estimateRegressionStandardError();
//				
//				double adjRSquared = regression.calculateAdjustedRSquared();
//				
//				System.out.println("Regressand variance:" +regressandVariance + " Rsquared: "+ rSquared +" Adjusted RSquared: "+ adjRSquared+ " sigma: "+ sigma);
//			}catch(SingularMatrixException ex){
//				System.out.println("Singular Matrix Exception");
//				UtilityClass.DoubleShuffle(x, y);
//				continue;
//			}
//			break;
//		}
//		
//		
//		for(int cnt=0; cnt<allAtomicSets.size(); cnt++){
//			result.put(allAtomicSets.get(cnt), nmt.getInverseRegressionValue(beta[cnt]));
//			System.out.println(beta[cnt]);
//		}
//		
//		List<AtomicSet> nsasl =  fasm.getUnchangableSelectionAtomicSets();
//		
//		for(int cnt=0; cnt<nsasl.size(); cnt++){
//			result.put(nsasl.get(cnt), 0d);
//		}
//		
//		return result;
//	}
	/***
	 * 
	 * @param fasm
	 * @param am
	 * @param nmt
	 * @param observationFMCs
	 * @param serviceMashupCache
	 * @param cv
	 * @return RSquared
	 * @throws Exception
	 */
	public  double findAtomicSetContributionValueOLS(FeatureAtomicSetMap fasm, ServiceNonfunctionalAnnotationMap am, NonfunctionalMetricType nmt, List<FeatureModelConfiguration> observationFMCs,Map<FeatureModelConfiguration, FlowComponentNode> serviceMashupCache, Map<AtomicSet, Double> cv) throws Exception{
		
		
		Map<Service, ServiceNonfunctionalAnnotation> map = am.getAnnotationMap();
		
		List<AtomicSet> allAtomicSets = fasm.getAllAtomicSets(true);
		
		int noOfRegressors = allAtomicSets.size();
		
		// create the dataset
		
		
		
		int noOfObservation =observationFMCs.size();
		
		
		
		
		// create the matrix
		
		double[] y = new double[noOfObservation];
		double[][] x = new double[noOfObservation][noOfRegressors-1];
		
		
		for(int cntr=0;cntr<noOfObservation; cntr++){
			
			
			System.out.println(observationFMCs.get(cntr).toString());
			
			y[cntr] = nmt.getRegressionValue( findFeatureModelConfigurationNonfunctionalValue(observationFMCs.get(cntr),  nmt,  map,serviceMashupCache));
			
			//find atomic set assignment array
			int idx =0;
			for(int ascntr =0; ascntr< allAtomicSets.size(); ascntr++){
				AtomicSet as = allAtomicSets.get(ascntr);
				if(as.equals(fasm.getFasMap().get(getFeatureModel().getRootFeature())))
					continue;
				if(observationFMCs.get(cntr).getFeatureAtomicSetStatus(as.getFeatureList())){
					x[cntr][idx]=1;
				}
				else
				{
					x[cntr][idx]=0;
				}
				idx++;
			}
			
		}
		double[] beta = null;
		
		OLS a = new OLS(x, y,true);
		
		beta= a.coefficients();
		
		int idx=0;
		for(int cnt=0; cnt<allAtomicSets.size(); cnt++){
			if(allAtomicSets.get(cnt).equals(fasm.getFasMap().get(getFeatureModel().getRootFeature()))){
				cv.put(allAtomicSets.get(cnt), nmt.getInverseRegressionValue(  a.intercept()));
				System.out.println(a.intercept());
			}
			else
			{
				cv.put(allAtomicSets.get(cnt), nmt.getInverseRegressionValue( beta[idx]));
				System.out.println(beta[idx]);
				idx++;
			}
			
		}
		
		List<AtomicSet> nsasl =  fasm.getUnchangableSelectionAtomicSets();
		
		for(int cnt=0; cnt<nsasl.size(); cnt++){
			cv.put(nsasl.get(cnt),nmt.getNeutralValue());
		}
		
		return a.RSquared();
	}
	
	
public  double findAtomicSetContributionValueOLS(FeatureAtomicSetMap fasm, ServiceNonfunctionalAnnotationMap am, NonfunctionalMetricType nmt, Map<FeatureModelConfiguration,FlowComponentNode> trainingFMCServiceMashupMap, AtomicSetNFAnnotationMap asnfam ) throws Exception{
		
		
		Map<Service, ServiceNonfunctionalAnnotation> map = am.getAnnotationMap();
		
		List<AtomicSet> allAtomicSets = fasm.getAllAtomicSets(true);
		
		int noOfRegressors = allAtomicSets.size();
		
		// create the dataset
		
		FeatureModelConfiguration[] trainingFMCs = trainingFMCServiceMashupMap.keySet().toArray(new FeatureModelConfiguration[0]);
		
		
		
		
		
		
		
		// create the matrix
		
		double[] y = new double[trainingFMCs.length];
		double[][] x = new double[trainingFMCs.length][noOfRegressors-1];
		
		
		for(int cntr=0;cntr<trainingFMCs.length; cntr++){
			
			
			System.out.println(trainingFMCs[cntr].toString());
			
			y[cntr] = nmt.getRegressionValue(findFeatureModelConfigurationNonfunctionalValue(trainingFMCs[cntr],  nmt,  map,trainingFMCServiceMashupMap));
			
			//find atomic set assignment array
			int idx =0;
			for(int ascntr =0; ascntr< allAtomicSets.size(); ascntr++){
				AtomicSet as = allAtomicSets.get(ascntr);
				if(as.equals(fasm.getFasMap().get(getFeatureModel().getRootFeature())))
					continue;
				if(trainingFMCs[cntr].getFeatureAtomicSetStatus(as.getFeatureList())){
					x[cntr][idx]=1;
				}
				else
				{
					x[cntr][idx]=0;
				}
				idx++;
			}
			
		}
		double[] beta = null;
		
		OLS a = new OLS(x, y,true);
		
		beta= a.coefficients();
		
		int idx=0;
		for(int cnt=0; cnt<allAtomicSets.size(); cnt++){
			if(allAtomicSets.get(cnt).equals(fasm.getFasMap().get(getFeatureModel().getRootFeature()))){
				asnfam.getMap().get(allAtomicSets.get(cnt)).getAnnotation().put(nmt, new NonfunctionalMetric(nmt, nmt.getInverseRegressionValue(a.intercept()), 0d)  );
				System.out.println(a.intercept());
			}
			else
			{
				asnfam.getMap().get(allAtomicSets.get(cnt)).getAnnotation().put(nmt, new NonfunctionalMetric(nmt, nmt.getInverseRegressionValue(beta[idx]), 0d)  );
				System.out.println(beta[idx]);
				idx++;
			}
			
		}
		
		List<AtomicSet> nsasl =  fasm.getUnchangableSelectionAtomicSets();
		
		for(int cnt=0; cnt<nsasl.size(); cnt++){
			asnfam.getMap().get(nsasl.get(cnt)).getAnnotation().put(nmt, new NonfunctionalMetric(nmt,  nmt.getNeutralValue(), 0d)  );
			
		}
		
		return a.RSquared();
	}
	
	public double findFeatureModelConfigurationNonfunctionalValue(FeatureModelConfiguration fmc, NonfunctionalMetricType nmt, Map<Service, ServiceNonfunctionalAnnotation> map,Map<FeatureModelConfiguration, FlowComponentNode> serviceMashupCache) throws Exception{
		FlowComponentNode fcn =null;
		if(serviceMashupCache.containsKey(fmc)){
			fcn = serviceMashupCache.get(fmc);
		}
		else
		{
			FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(this, fmc);
			fcn =  fmcmg.buildServiceMashup();
			serviceMashupCache.put(fmc, fcn);
		}
		
		return nmt.getAggregatedValue(map, fcn);
	}

}
