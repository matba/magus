package edu.ls3.magus.cl.fmconfigurator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import java.util.List;
import java.util.UUID;


import edu.ls3.magus.cl.contextmanager.basic.*;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.fmconfigurator.model.Feature;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAnnotation;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAnnotationSet;

import edu.ls3.magus.eval.generators.owls.UtilityClass;


public class FeatureGen {
//	private  final String templateAddress ="D:\\Development\\BPLECONS\\features\\ontology";
	
	public static void main(String[] args) throws Exception {
		
		//generateFeatures();
		readAndPrintFeatures();
	}
	
	public static void generateFeatures()  throws Exception{
		DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset3\\s1000");
		dm.getContextModel().createSimpleContext();
		FeatureAnnotationSet fas = generateFeature(dm, 100, 2, 4);
		FileOutputStream fileOut =
         new FileOutputStream("D:\\fas.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(fas);
         out.close();
         fileOut.close();
	}
	
	public static void readAndPrintFeatures() throws ClassNotFoundException, IOException{
//		FeatureAnnotationSet fas = FeatureAnnotationSet.readAnnotationSet("D:\\fas.ser");
//		for(Feature f: fas.getAnnotationMap().keySet()){
//			System.out.println("Feature: "+f.getName());
//			System.out.print("P+: ");
//			for(StateFactInstanceS s: fas.getAnnotationMap().get(f).getPreconditionAdd().getConditions()){
//				if(s.isNot())
//					System.out.println("!");
//				System.out.print(s.getStateFactInstance().getType().getTypeName());
//				System.out.print(", ");
//			}
//			System.out.println();
//			System.out.print("P-: ");
//			for(StateFactInstanceS s: fas.getAnnotationMap().get(f).getPreconditionRem().getConditions()){
//				if(s.isNot())
//					System.out.println("!");
//				System.out.print(s.getStateFactInstance().getType().getTypeName());
//				System.out.print(", ");
//			}
//			System.out.println();
//			
//			System.out.print("E+: ");
//			for(StateFactInstanceS s: fas.getAnnotationMap().get(f).getPostconditionAdd().getConditions()){
//				if(s.isNot())
//					System.out.println("!");
//				System.out.print(s.getStateFactInstance().getType().getTypeName());
//				System.out.print(", ");
//			}
//			System.out.println();
//			
//			System.out.print("E-: ");
//			for(StateFactInstanceS s: fas.getAnnotationMap().get(f).getPostconditionRem().getConditions()){
//				if(s.isNot())
//					System.out.println("!");
//				System.out.print(s.getStateFactInstance().getType().getTypeName());
//				System.out.print(", ");
//			}
			System.out.println();
			
			System.out.println();
//		}
	}
	
	public static FeatureAnnotationSet generateFeature(DomainModels m,int noOfFeatures, int numberOfReqsMin, int numberOfReqsMax ){
		FeatureAnnotationSet result = new FeatureAnnotationSet();
		for(int cnt=0; cnt<noOfFeatures; cnt++){
			String featureName="f" + UUID.randomUUID().toString().substring(1,10).replaceAll("-", "");
			Feature feature = new Feature(featureName,false,false,false, UUID.randomUUID().toString());
			FeatureAnnotation featureAnnotation = CreateFeatureAnnotation(m.getContextModel(),feature, UtilityClass.randInt(numberOfReqsMin, numberOfReqsMax));
			result.getAnnotationMap().put(feature, featureAnnotation);
		}
		
		return result;
	}
	private static FeatureAnnotation CreateFeatureAnnotation(ContextModel cm, Feature feature,
			int numOfReqs) {
		
		FeatureAnnotation result = null;
		
		
		List<StateFactInstanceS> sfpa = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> sfpr = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> sfsa = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> sfsr = new ArrayList<StateFactInstanceS>();
		
		
//		StateFactType type =  cm.getFactTypes().getStateFactTypeList().get(UtilityClass.randInt(0,  cm.getFactTypes().getStateFactTypeList().size()-1));
//		List<Instance> params = new ArrayList<Instance>();
//		for(int cnt=0; cnt< type.getParams().length; cnt++)
//		{
//			InstanceType it = type.getParams()[cnt];
//			Instance i = cm.filterVarsByType(it).get(0);
//			params.add(i);
//		}
//		StateFactInstance x = new StateFactInstance(type, params.toArray(new Instance[0]));
//		sfsa.add(new StateFactInstanceS(x, false));
		
		for(int reqCnt=0; reqCnt<numOfReqs; reqCnt++){
			StateFactType type =  cm.getFactTypes().getStateFactTypeList().get(UtilityClass.randInt(0,  cm.getFactTypes().getStateFactTypeList().size()-1));
			List<Instance>  params = new ArrayList<Instance>();
			for(int cnt=0; cnt< type.getParams().length; cnt++)
			{
				InstanceType it = type.getParams()[cnt];
				Instance i = cm.filterVarsByType(it).get(0);
				params.add(i);
			}
			StateFactInstance x = new StateFactInstance(type, params.toArray(new Instance[0]));
			 StateFactInstanceS xs = new StateFactInstanceS(x, false);
			 int d = Distribution();
			 
			 if(d==1)
				 sfpa.add(xs);
			 if(d==2)
				 sfpr.add(xs);
			 if(d==3)
				 sfsa.add(xs);
			 if(d==4)
				 sfsr.add(xs);
			 
		}
		
		
		 
//		Condition preconditionAdd = new Condition(sfpa);
//		Condition preconditionRem= new Condition(sfpr);
//		Condition postconditionAdd= new Condition(sfsa);
//		Condition postconditionRem= new Condition(sfsr);
		
		//result = new FeatureAnnotation(feature, preconditionAdd, preconditionRem, postconditionAdd, postconditionRem);
		result = null;
		return result;
	}
	private static int Distribution(){
		int r = UtilityClass.randInt(0, 100);
		System.out.println(r);
		if(r<40)
			return 1;
		if(r<50)
			return 2;
		if(r<90)
			return 3;
		return 4;
	}
	
	
	

}
