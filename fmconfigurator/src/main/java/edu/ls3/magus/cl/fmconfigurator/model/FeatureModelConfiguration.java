package edu.ls3.magus.cl.fmconfigurator.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.UnexpectedException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalConstraint;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.configuration.PlannerConfiguration;
import edu.ls3.magus.utility.Holder;
import edu.ls3.magus.utility.UtilityClass;

public class FeatureModelConfiguration {
	
	private List<Feature> featureList;
	private Set<Feature> criticalFeatureSet;
	private float distance;

	public FeatureModelConfiguration(){
		featureList = new ArrayList<Feature>();
		criticalFeatureSet = new HashSet<Feature>();
	}
	
	public FeatureModelConfiguration(List<Feature> flc) {
		this();
		distance =0;
		featureList = flc;
	}
	public FeatureModelConfiguration(String[] uuids, FeatureModel fm) {
		
		this();		
		Map<String,Feature> fmap = fm.getFeaturesUUIDMap();
		
		for(String uuid:uuids)
			featureList.add(fmap.get(uuid));
		
	}
	
	public List<String> getSelectedFeatureUUIDs(){
		List<String> result = new ArrayList<String>();
		for(Feature f:featureList){
			result.add(f.getUuid());
		}
		
		return result;
	}

	public List<Feature> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<Feature> featureList) {
		this.featureList = featureList;
	}
	
	public FeatureModelConfiguration findAlternateConfigurationNF(DomainModels dm,ContextStateModel contextStateModel, List<NonfunctionalConstraint> nonFunctionalConstraintList,  Map<FeatureModelConfiguration, FlowComponentNode> trainingFMCServiceMashupMap, FeatureAtomicSetMap fasm ,Holder<Integer> noOfTries) throws Exception{
		
		
		//find features contribution values
		
		Set<NonfunctionalMetricType> nmtList = new HashSet<NonfunctionalMetricType>();
		
		List<AtomicSet> atomicSetList = fasm.getAllAtomicSets(false); 
		
		AtomicSetNFAnnotationMap asnfam = new AtomicSetNFAnnotationMap(atomicSetList);
		
		
		
		for(NonfunctionalConstraint nfc: nonFunctionalConstraintList)
			nmtList.add(nfc.getType());
		
		for(NonfunctionalMetricType nmt: nmtList){
			
			dm.findAtomicSetContributionValueOLS(fasm, contextStateModel.getServiceNonfunctionalMap(), nmt, trainingFMCServiceMashupMap, asnfam);
			
		}
		
		
		
		
		List<FeatureModelConfiguration> failedConfigurations= new ArrayList<FeatureModelConfiguration>();
		failedConfigurations.add(this);
		
		boolean foundConfiguration = false;
		FeatureModelConfiguration fmc = null;
		
		int cntr=0;
		
		while(!foundConfiguration){
			fmc = serializeOptimizationForNaPS(failedConfigurations,dm,contextStateModel,atomicSetList, asnfam,nonFunctionalConstraintList);
			if(fmc==null)
				break;
			
			//check a mashup can be built for this configuration
			try{
				FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc,contextStateModel);
				FlowComponentNode fcn = fmcmg.buildServiceMashup();
				
				boolean satisfiesAllConstraints =true;
				
				for(NonfunctionalConstraint nfc : nonFunctionalConstraintList){
					Double nfv = nfc.getType().getAggregatedValue(contextStateModel.getServiceNonfunctionalMap().getAnnotationMap(), fcn);
					if(!nfc.satisfy(nfv) )
					{
						satisfiesAllConstraints = false;
						break;
					}
					
				}
				
				if(satisfiesAllConstraints)
					foundConfiguration=true;
				else{
					failedConfigurations.add(fmc);
					cntr++;
				}
			}
			catch(UnsuccessfulMashupGeneration ex){
				failedConfigurations.add(fmc);
			}
			
			cntr++;
			
		}
		
		noOfTries.value = cntr;
		
		return fmc;
	}
	
	public FeatureModelConfiguration findAlternateConfiguration(DomainModels dm,ContextStateModel contextStateModel, Holder<Integer> noOfTries) throws Exception{
		
		List<FeatureModelConfiguration> failedConfigurations= new ArrayList<FeatureModelConfiguration>();
		failedConfigurations.add(this);
		
		boolean foundConfiguration = false;
		FeatureModelConfiguration fmc = null;
		
		int cntr=0;
		
		while(!foundConfiguration){
			fmc = serializeOptimizationForNaPS(failedConfigurations,dm,contextStateModel,null, null,new ArrayList<NonfunctionalConstraint>());
			if(fmc==null)
				break;
			
			//check a mashup can be built for this configuration
			try{
				FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc,contextStateModel);
				fmcmg.convertToPDDL();
				fmcmg.Callplanner();
				foundConfiguration=true;
			}
			catch(UnsuccessfulMashupGeneration ex){
				failedConfigurations.add(fmc);
			}
			
			cntr++;
			
		}
		
		noOfTries.value = cntr;
		
		return fmc;
	}
	
	
	public FeatureModelConfiguration serializeOptimizationForNaPS(List<FeatureModelConfiguration> failedConfigurations,DomainModels dm, ContextStateModel contextStateModel,List<AtomicSet> atomicSetList, AtomicSetNFAnnotationMap fnfm, List<NonfunctionalConstraint> nonFunctionalConstraintList) throws IOException, InterruptedException{
		
		
		StringBuilder sb = new StringBuilder();
		
		// Create minimization serialization
		sb.append(serializeMinimizationFunction(dm.getFeatureModel()));
		
		// Critical Features
		sb.append(serializeCriticalFeatures());
		
		
		//Non-functional Requirements
		for(NonfunctionalConstraint nfc: nonFunctionalConstraintList)
			sb.append(serializeNFConstraints(atomicSetList, fnfm,nfc));
		
		// Create feature model
		sb.append(serializedFeatureModel(dm.getFeatureModel()));
				
		// Create those features which change the preconditions
		sb.append(serializePreconditionConstraints(dm.getFeatureModel(),dm.getFeatureModelAnnotation()));
		
		// Create those feature which affect the failed service
		sb.append(serializeFailedConfiguration(failedConfigurations,dm.getFeatureModel()));
		
		String serializedProblem = sb.toString();
		
		//Check if result found  and create new feature model based on result
		FeatureModelConfiguration fmc = callNaPS(dm.getFeatureModel(), serializedProblem);
		
		
		return fmc;
		
		
	}
	
	
//	private FeatureModelConfiguration serializeOptimizationForNaPS(List<FeatureModelConfiguration> failedConfigurations,DomainModels dm) throws IOException, InterruptedException{
//		StringBuilder sb = new StringBuilder();
//		
//		// Create minimization serialization
//		sb.append(serializeMinimizationFunction(dm.getFeatureModel()));
//		
//		// Create feature model
//		sb.append(serializedFeatureModel(dm.getFeatureModel()));
//		
//		// Create those features which change the preconditions
//		sb.append(serializePreconditionConstraints(dm.getFeatureModel(),dm.getFeatureModelAnnotation()));
//		
//		// Create those feature which affect the failed service
//		sb.append(serializeFailedConfiguration(failedConfigurations,dm.getFeatureModel()));
//		
//		String serializedProblem = sb.toString();
//		
//		//Check if result found  and create new feature model based on result
//		FeatureModelConfiguration fmc = callNaPS(dm.getFeatureModel(), serializedProblem);
//		
//		
//		return fmc;
//	}
	
	private FeatureModelConfiguration callNaPS(FeatureModel featureModel, String serializedProblem) throws IOException, InterruptedException {
		
		String problemAddress = PlannerConfiguration.tempFolder+ "adaptation.pb";
		

		UtilityClass.writeFile(new File(problemAddress),serializedProblem);
		
		Process p = Runtime.getRuntime().exec(PlannerConfiguration.plannerAddress+ "naps-1.02b/naps "+problemAddress);
		p.waitFor();

		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";			
		boolean alternateConfigurationFound =false;
		String resultLine =null;
		float distance =0;
		
		while ((line = reader.readLine())!= null) {
			//System.out.println(line);
			if(line.startsWith("s")){
				if(line.startsWith("s OPTIMUM FOUND"))
					alternateConfigurationFound = true;
				continue;
			}
			if(line.startsWith("o")&& !alternateConfigurationFound){
				try{
					distance = Float.valueOf(line.substring(2));
				}
				catch(Exception ex){
					throw new UnexpectedException("Unexpected output from NaPS. Line didn't contained the optimum.");
				}
			}
			if(line.startsWith("v")){
				resultLine = line.substring(2);
			}
			

		}
		if(!alternateConfigurationFound)
			return null;
		if(resultLine==null)
			throw new UnexpectedException("NaPS found optimum but cannot find the optimum assignment.");
		
		String[] featureUUIDs = resultLine.split(" ");
		
		Map<String, Feature> mp = featureModel.getFeaturesVarNameUUIDMap();
		List<Feature> selectedFeatures = new ArrayList<Feature>();
		for(String curfID: featureUUIDs ){
			
			if(curfID.startsWith("-"))
				continue;
			Feature curf =mp.get(curfID);
			if(curf==null)
				throw new UnexpectedException("NaPS returns a feature UUID that does not exist.");
			selectedFeatures.add(curf);
		}
		
		FeatureModelConfiguration fmc = new FeatureModelConfiguration(selectedFeatures);
		fmc.setDistance(distance);
		
		return fmc;
		
			
	}
	private Object serializeFailedConfiguration(List<FeatureModelConfiguration> failedConfigurations, FeatureModel fm) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(System.lineSeparator());
		sb.append("* FAILED CONFIGURATIONS ");
		sb.append(System.lineSeparator());
		
		for(FeatureModelConfiguration fmc: failedConfigurations){
			sb.append("* CONF: ");
			for(Feature f: fm.getFeatureList()){
				if(fmc.getFeatureList().contains(f)){
					sb.append(" "+f.getName() );
				}
				
				
			}
			
			sb.append(System.lineSeparator());
		}
		
		for(FeatureModelConfiguration fmc: failedConfigurations){
			for(Feature f: fm.getFeatureList()){
				if(fmc.getFeatureList().contains(f)){
					sb.append(" 1 "+f.getUuidVarName() );
				}
				else
				{
					sb.append(" 1 ~"+f.getUuidVarName() );
				}
				
			}
			sb.append(" < "+fm.getFeatureList().size()+" ;");
			sb.append(System.lineSeparator());
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}
	private Object serializePreconditionConstraints(FeatureModel fm, FeatureAnnotationSet fma) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(System.lineSeparator());
		sb.append("* PRECONDITION LOCKED FEATURE REPRESENTATION ");
		sb.append(System.lineSeparator());
		
		Map<StateFactInstanceS,List<Feature>> factFeatureMap = fma.findAllAnnotatedPrecondition(fm);
		List<StateFactInstanceS> factsFmc= fma.findPrecondition(this).getConditions();
		
		
		for(StateFactInstanceS sfis: factFeatureMap.keySet()){
			if(factsFmc.contains(sfis)){
				
				sb.append("* one of these features should be selected: ");
				for(Feature f: factFeatureMap.get(sfis)){
					sb.append(" "+f.getName() );
				}
				sb.append(System.lineSeparator());
				
				for(Feature f: factFeatureMap.get(sfis)){
					sb.append(" 1 "+f.getUuidVarName() );
				}
				sb.append(" >= 1 ;");
				sb.append(System.lineSeparator());
			}
			else{
				sb.append("* none of these features should be selected: ");
				for(Feature f: factFeatureMap.get(sfis)){
					sb.append(" "+f.getName() );
				}
				sb.append(System.lineSeparator());
				
				for(Feature f: factFeatureMap.get(sfis)){
					sb.append(" 1 "+f.getUuidVarName() );
				}
				sb.append(" = 0 ;");
				sb.append(System.lineSeparator());
			}
		}
		
		
		return sb.toString();
	}
	
	private String serializedFeatureModel(FeatureModel fm) {
		
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(System.lineSeparator());
		sb.append("* FEATURE MODEL REPRESENTATION ");
		sb.append(System.lineSeparator());
		
		
		//serialize root condition
		sb.append("* root feature should be selected: " + fm.getRootFeature().getName());
		
		sb.append(System.lineSeparator());
		
		
		sb.append("1 "+fm.getRootFeature().getUuidVarName() +" = 1 ;");
		sb.append(System.lineSeparator());
		
		sb.append(System.lineSeparator());
		sb.append("* Child to parent representation ");
		sb.append(System.lineSeparator());
		
		sb.append(System.lineSeparator());
		//serialize child to parent relationship
		List<Feature> allFeatures = fm.getFeatureList();
		
		for(Feature f:allFeatures){
			for(Feature cf:f.getChildren()){
				sb.append("* " + cf.getName() +" => "+f.getName());
				
				sb.append(System.lineSeparator());
				
				sb.append("1 ~"+cf.getUuidVarName() + " 1 "+f.getUuidVarName()+" >= 1 ;");
				sb.append(System.lineSeparator());
			}
		}
		
		sb.append(System.lineSeparator());
		sb.append("* Mandatory relation representation ");
		sb.append(System.lineSeparator());
		
		//serialize mandatory relationship
		for(Feature f:allFeatures){
			if(!f.isOrGroup() && !f.isAlternative()){
				for(Feature cf:f.getChildren()){
					if( !cf.isOptional()){
						sb.append("* " + f.getName() +" => "+cf.getName());
						sb.append(System.lineSeparator());
						
						sb.append("1 ~"+f.getUuidVarName() + " 1 "+cf.getUuidVarName()+" >= 1 ;");
						sb.append(System.lineSeparator());
					}
				}
			}
		}
		
		sb.append(System.lineSeparator());
		sb.append("* Alternative relation representations ");
		sb.append(System.lineSeparator());
		
		//serialize alternative relationship
		for(Feature f:allFeatures){
			if(f.isAlternative() && (f.getChildren().size()>0)){
				
				sb.append("* " + f.getName() + " => ");
				for(Feature cf:f.getChildren()){
					sb.append(cf.getName()+" xor " );
				}
				sb.append(System.lineSeparator());
				
				sb.append("1 ~"+f.getUuidVarName());
				for(Feature cf:f.getChildren()){
						sb.append(" 1 "+cf.getUuidVarName() );
				}
				sb.append(" = 1 ;");
				sb.append(System.lineSeparator());
			}
		}
		
		sb.append(System.lineSeparator());
		sb.append("* Or relation representations ");
		sb.append(System.lineSeparator());
		//serialize or relationship
		for(Feature f:allFeatures){
			if(f.isOrGroup() && (f.getChildren().size()>0)){
				sb.append("* " + f.getName() + " => ");
				for(Feature cf:f.getChildren()){
					sb.append(cf.getName()+" v " );
				}
				sb.append(System.lineSeparator());
				
				
				sb.append("1 ~"+f.getUuidVarName());
				for(Feature cf:f.getChildren()){
						sb.append(" 1 "+cf.getUuidVarName() );
				}
				sb.append(" >= 1 ;");
				sb.append(System.lineSeparator());
			}
		}
		
		sb.append(System.lineSeparator());
		sb.append("* Integrity constraints ");
		sb.append(System.lineSeparator());
		//serialize integrity constraints
		for(IntegrityConstraint ic: fm.getIntegrityConstraints().getIntegrityConstraints()){
			if(ic.getType().equals(IntegrityConstraint.REQUIRES)){
				sb.append("* "+ic.getSourceFeature().getName() + " requires "+ic.getTargetFeature().getName());
				sb.append(System.lineSeparator());
				
				sb.append("1 ~"+ic.getSourceFeature().getUuidVarName() + " 1 "+ic.getTargetFeature().getUuidVarName()+" >= 1 ;");
				sb.append(System.lineSeparator());				
			}
			if(ic.getType().equals(IntegrityConstraint.EXCLUDES)){
				sb.append("* "+ic.getSourceFeature().getName() + " excludes "+ic.getTargetFeature().getName());
				sb.append(System.lineSeparator());
				
				sb.append("1 ~"+ic.getSourceFeature().getUuidVarName() + " 1 ~"+ic.getTargetFeature().getUuidVarName()+" >= 1 ;");
				sb.append(System.lineSeparator());				
			}
		}
		
		
		return sb.toString();
	}
	
	// This function serialize the minimization function for psuedo-boolean planner for self-healing functional 
	private String serializeMinimizationFunction(FeatureModel fm) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("* minimize distace from ");
		
		for(Feature f: fm.getFeatureList()){
			if(getCriticalFeatureSet().contains(f))
				continue;
			
			String notStr ="";
			if(getFeatureList().contains(f)){
				notStr="~";
			}
			sb.append(""+notStr+ f.getName()+" ");
		}
		sb.append(System.lineSeparator());
		
		sb.append("min: ");
		
		for(Feature f: fm.getFeatureList()){
			if(getCriticalFeatureSet().contains(f))
				continue;
			
			String notStr ="";
			if(getFeatureList().contains(f)){
				notStr="~";
			}
			sb.append("+1 "+notStr+ f.getUuidVarName()+" ");
		}
		
		sb.append(";");
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	
	
	private String serializeCriticalFeatures(){
		
		StringBuilder sb = new StringBuilder();
		
		for(Feature f:getCriticalFeatureSet()){
				sb.append("* 1 =>  " + f.getName());
				sb.append(System.lineSeparator());
				sb.append(" 1 "+f.getUuidVarName() );
				sb.append(" = 1 ;");
				sb.append(System.lineSeparator());
		}
		
		return sb.toString();
		
	}
	
	private String serializeNFConstraints(List<AtomicSet> atomicSetList, AtomicSetNFAnnotationMap fnfm, NonfunctionalConstraint nfc){
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(System.lineSeparator());
		sb.append("* NON FUNCTIONAL REQUIREMENT REPRESENTATION ");
		sb.append(System.lineSeparator());

		for(AtomicSet as: atomicSetList){
			sb.append(" "+String.valueOf( (int) (1000* fnfm.getMap().get(as).getAnnotation().get(nfc.getType()).getAverage())) +" "+as.getMainFeatureList().toArray(new Feature[0])[0].getUuidVarName() );
		}
		
		if(nfc.isLower()){
			sb.append(" < ");
		}
		else
		{
			sb.append(" > ");
		}
		
		sb.append(String.valueOf( (int) (1000* nfc.getThreshold())) );			
		sb.append(" ;");
		sb.append(System.lineSeparator());
		
		
		
		
		
		return sb.toString();
	}
	
	
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public boolean hasValidIntegrityConstraints(FeatureModel featureModel) {
		
		
		for(IntegrityConstraint ic: featureModel.getIntegrityConstraints().getIntegrityConstraints()){
			if(ic.getType().equals(IntegrityConstraint.REQUIRES)){
				if(getFeatureList().contains(ic.getSourceFeature())&&!getFeatureList().contains(ic.getTargetFeature()))
						return false;
			}
			if(ic.getType().equals(IntegrityConstraint.EXCLUDES)){
				if(getFeatureList().contains(ic.getSourceFeature())&&getFeatureList().contains(ic.getTargetFeature()))
						return false;
			}
		}
		
		
		return true;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String sep ="";
		sb.append("{");
		for(Feature f: getFeatureList()){
			sb.append(sep+f.getName());
			sep=", ";
		}
		sb.append("}");	
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object i){
		
		if(i.getClass() != FeatureModelConfiguration.class)
			return false;
		FeatureModelConfiguration s = (FeatureModelConfiguration) i;
		
		if(getFeatureList().size()!=s.getFeatureList().size())
			return false;
		
		for(Feature f1: getFeatureList()){
			boolean found = false;
			for(Feature f2:s.getFeatureList()){
				if(f1.equals(f2)){
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		return true;

		
		
	}
	
	@Override
    public int hashCode() {
		int result =0;
		for(Feature f: featureList)
			result+=f.hashCode();
		
		
		return result;
	}
	
	
	public boolean getFeatureAtomicSetStatus(Set<Feature> featureSet) {
		
		Feature[] fl =featureSet.toArray(new Feature[0]);
		
		for(Feature f: fl)
			if(!getFeatureStatus( f))
				return false;
		
		return true;
		
	
	}
	public boolean getFeatureStatus(Feature feature) {
		
		return featureList.contains(feature);
	}
	public double estimateNonfunctionalValue(FeatureAtomicSetMap fasm, Map<AtomicSet, Double> cv,
			NonfunctionalMetricType instance) {
		
		List<AtomicSet> selectedAtomicSets = findSelectedAtomicSet(fasm);
		
		double[] contributionValues = new double[selectedAtomicSets.size()];
		
		for(int cntr=0; cntr< selectedAtomicSets.size(); cntr++)
			contributionValues[cntr]= cv.get(selectedAtomicSets.get(cntr));
		
		double aggregatedValue = instance.findFeatureAggregatedValue(contributionValues);
		
		return aggregatedValue;
		
		
	}
	private List<AtomicSet> findSelectedAtomicSet(FeatureAtomicSetMap fasm) {
		
		List<AtomicSet> result= new ArrayList<AtomicSet>();
		
		for(Feature f: getFeatureList())
			if(!result.contains( fasm.getFasMap().get(f)))
				result.add(fasm.getFasMap().get(f));
		return result;
				
				
		
	}
	public Set<Feature> getCriticalFeatureSet() {
		return criticalFeatureSet;
	}
	
	
	
	
}
