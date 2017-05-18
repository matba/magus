package edu.ls3.magus.cl.fmconfigurator;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.planning.Problem;
import edu.ls3.magus.eval.generators.owls.Range;
import edu.ls3.magus.utility.UtilityClass;

public class FeatureGen2 {

	private static final int[] reqNoList = { 3, 6, 9 };
	private static final int[] reqNoListFeature = { 2, 8, 16 };
	static final String homeAddress="/home/mbashari/";
	//private static final Integer[] nos = { 100, 200, 400, 500, 600, 800, 1000 ,5000,10000};
	//private static final Integer[] nos1 = { 100, 200, 400, 500, 600, 800, 1000 ,5000,10000};
	private static final Integer[] nos = {1000, 2000,3000,4000,5000, 6000,7000,8000,9000,10000};
	//private static final int numOfFeaturesOld[] = { 10, 20, 40, 60, 80 ,100,250,500};
	private static final int numOfFeatures[] = { 50,100,150,200,250,300,350,400,450,500};
	// private static final int premids[] ={1,2,4,8,10,12};
	private static final int premids1[][] ={{2,3,5,7,8,10,11,13,14,16},{5,10,15,20,25,30,35,40,45,50},{8,16,24,32,40,48,56,64,72,80}};
	//private static final int premids2[][] ={{1,2,3,4,5,6,12,16},{4,7,10,12,14,16,30,50},{7,12,16,19,22,25,50,80}};
	private static final int premids[] = { 2, 4, 9, 14, 19, 23 ,46,90};
	// private static final int premids[] ={14,29,62,93,124,153};
	private static final int predevs[] = { 2, 2, 2, 2, 2, 2 ,2,2,2,2};
	private static final int effemids[] = { 7, 13, 24, 37, 50, 60 ,110,210};
	private static final int effemids1[][] ={ {8,16,24,32,40,48,56,64,72,80},{25,50,75,100,125,150,175,200,225,250},{40,80,120,160,200,240,280,320,360,400}};
	//private static final int effemids2[][] ={ { 6, 10, 17, 21, 25, 30 ,60,80},{ 21, 38, 52, 62, 70, 80 ,150,250},{ 37, 66, 80, 96, 110, 125 ,250,400}};
	//private static final int effemids1[][] ={ { 5, 31, 61, 92, 123, 150 },{ 30, 62, 120, 184, 246, 265 },{ 45, 80, 150, 210, 290, 320 }};
	// private static final int effemids[] ={21,43,84,126,167,207};
	// private static final int effemids[] ={14,29,62,93,124,153};
	private static final int effdevs[] = { 4, 4, 4, 4, 4, 4,4,4,4,4 };

	private static List<Integer> isInRange(List<Range> rl, int no) {
		List<Integer> result = new ArrayList<Integer>();
		for (int cnt = 0; cnt < rl.size(); cnt++)
			if (rl.get(cnt).isInRange(no))
				result.add(cnt);
		return result;

	}

	public static void main(String[] args) throws Exception {

//		for(int cnt=0; cnt<3;cnt++)
//			GenerateFeatures();
		//GenerateFeatureModelConfigurationFSUbuntu(1);
		
//		DomainModels dm = DomainModels
//				.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
//		dm.getContextModel().createSimpleContext();
//		DoAWalk2(dm, 1000);
		
		
		//GenerateFeaturesNewUbuntu(1000,1,2,0,9,15,150);
		GenerateFeatureModelConfigurationFSUbuntu(0);
	}

	public static void GenerateFeatureModelConfigurationFS(int no)
			throws Exception {
		List<Range> rl = new ArrayList<Range>();

		Map<Integer, Range> preMap = new HashMap<Integer, Range>();
		Map<Integer, Range> effMap = new HashMap<Integer, Range>();

		Range r = new Range(numOfFeatures[no], true, premids[no], predevs[no]);
		rl.add(r);
		preMap.put(numOfFeatures[no], r);

		r = new Range(numOfFeatures[no], false, effemids[no], effdevs[no]);
		rl.add(r);
		effMap.put(numOfFeatures[no], r);

		for (int rcnt = 0; rcnt < reqNoList.length; rcnt++) {

			UtilityClass
					.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFS\\r"
							+ reqNoList[rcnt]);

			for (int scnt = 0; scnt < nos.length; scnt++) {
				List<Condition> interestingConditions = new ArrayList<Condition>();
				List<Integer> iterationList = new ArrayList<Integer>();
				UtilityClass
						.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFS\\r"
								+ reqNoList[rcnt] + "\\s" + nos[scnt]);
				
				DomainModels dm = DomainModels
						.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r"
								+ reqNoList[rcnt] + "\\s" + nos[scnt]);
				dm.getContextModel().createSimpleContext();
				List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();

				String cond1 = "p7f9d1560";
				String cond2 = "pfc4299ba";

//				String postCond1 = dm
//						.getContextModel()
//						.getFactTypes()
//						.getStateFactTypeList()
//						.get(edu.ls3.magus.eval.generators.owls.UtilityClass
//								.randInt(0, dm.getContextModel().getFactTypes()
//										.getStateFactTypeList().size() - 1))
//						.getTypeName();
//				String postCond2 = dm
//						.getContextModel()
//						.getFactTypes()
//						.getStateFactTypeList()
//						.get(edu.ls3.magus.eval.generators.owls.UtilityClass
//								.randInt(0, dm.getContextModel().getFactTypes()
//										.getStateFactTypeList().size() - 1))
//						.getTypeName();
				// String postCond1 = "p3e2f7d26";
				// String postCond2 = "pc550de6b";
				// System.out.println("*****"+postCond1);
				// System.out.println("*****"+postCond2);

				Instance[] params = {
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond1)[0]
														.getParams()[0]
														.getTypeName())[0],
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond1)[0]
														.getParams()[1]
														.getTypeName())[0] };
				isl.add(new StateFactInstanceS(new StateFactInstance(dm
						.getContextModel().getInstaceFactTypeByName(cond1)[0],
						params), false));
				Instance[] params2 = {
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond2)[0]
														.getParams()[0]
														.getTypeName())[0],
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond2)[0]
														.getParams()[1]
														.getTypeName())[0] };
				isl.add(new StateFactInstanceS(new StateFactInstance(dm
						.getContextModel().getInstaceFactTypeByName(cond2)[0],
						params2), false));

				Condition curCondition = new Condition(isl);

				int itcnt = 0;
				while ((itcnt < 500)
						&& (curCondition.getConditions().size() < 150)) {
					System.out.println("Size of facts: "
							+ curCondition.getConditions().size());
					if (isInRange(rl, curCondition.getConditions().size())
							.size() > 0) {
						interestingConditions.add(curCondition);
						iterationList.add(itcnt);
					}
					Map<Service, Map<URI, Instance>> vs = dm
							.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
					System.out.println("Iteration " + itcnt
							+ " possible services: " + vs.size());
					if (vs.size() == 0) {
						System.out.println("No more services!");
						break;

					}

					Service[] ss = vs.keySet().toArray(new Service[0]);
					Service selectedService = ss[UtilityClass.randInt(0,
							ss.length - 1)];
					curCondition = selectedService.getContextAfterExc(
							curCondition, vs.get(selectedService));

					itcnt++;

				}

				// for(int cnt=0; cnt<interestingConditions.size(); cnt++)
				// {
				// System.out.println("Iteration: "+ iterationList.get(cnt));
				// System.out.println(interestingConditions.get(cnt).getConditions().size());
				// }

				// for(int cnt=0; cnt<numOfFeatures.length; cnt++){
				// UtilityClass.createFolder("D:\\fmproblems\\f"+numOfFeatures[cnt]);
				// }
				// for(int cnt=0; cnt<numOfFeatures.length; cnt++){
				Range preRange = preMap.get(numOfFeatures[no]);
				Range effRange = effMap.get(numOfFeatures[no]);

				for (int precnt = 0; precnt < interestingConditions.size(); precnt++) {
					// int fcnt=1;
					Condition preCondition = interestingConditions.get(precnt);
					if (!preRange
							.isInRange(preCondition.getConditions().size()))
						continue;

					for (int effcnt = interestingConditions.size() - 1; effcnt > precnt; effcnt--) {
						Condition effCondition = interestingConditions
								.get(effcnt);
						if (!effRange.isInRange(effCondition.getConditions()
								.size()))
							continue;

						if (iterationList.get(effcnt)
								- iterationList.get(precnt) > 20)
							continue;

						Problem p = new Problem(dm.getContextModel(),
								preCondition, effCondition);
						// System.out.println("Found one problem for "+numOfFeatures[cnt]
						// + " Pre size: "+ preCondition.getConditions().size()+
						// " Eff Size: "+ effCondition.getConditions().size() +
						// " Iteration difference: "+
						// Integer.toString(iterationList.get(effcnt)-iterationList.get(precnt)));

						int fcnt = findnextnoTXT("D:\\Development\\BPLECONS\\ds\\featureModelFS\\r"
								+ reqNoList[rcnt] + "\\s" + nos[scnt]);

						UtilityClass.writeFile(new File(
								"D:\\Development\\BPLECONS\\ds\\featureModelFS\\r"
										+ reqNoList[rcnt] + "\\s" + nos[scnt]
										+ "\\n" + fcnt++ + ".txt"), p
								.Serialize());
					}

					// }
				}
			}
		}

	}

	
	public static void GenerateFeatureModelConfigurationFSUbuntu(int no)
			throws Exception {
		List<Range> rl = new ArrayList<Range>();

		Map<Integer, Range> preMap = new HashMap<Integer, Range>();
		Map<Integer, Range> effMap = new HashMap<Integer, Range>();

		Range r = new Range(numOfFeatures[no], true, premids[no], predevs[no]);
		rl.add(r);
		preMap.put(numOfFeatures[no], r);

		r = new Range(numOfFeatures[no], false, effemids[no], effdevs[no]);
		rl.add(r);
		effMap.put(numOfFeatures[no], r);

		for (int rcnt = 0; rcnt < reqNoList.length; rcnt++) {

			UtilityClass
					.createFolder(homeAddress+ "BPLECONS/ds/featureModelFS/r"
							+ reqNoList[rcnt]);

			for (int scnt = 0; scnt < nos.length; scnt++) {
				List<Condition> interestingConditions = new ArrayList<Condition>();
				List<Integer> iterationList = new ArrayList<Integer>();
				UtilityClass
						.createFolder(homeAddress+"BPLECONS/ds/featureModelFS/r"
								+ reqNoList[rcnt] + "/s" + nos[scnt]);
				
				DomainModels dm = DomainModels
						.ReadModels(homeAddress+"BPLECONS/ds/services/r"
								+ reqNoList[rcnt] + "/s" + nos[scnt]);
				dm.getContextModel().createSimpleContext();
				List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();

				String cond1 = "p7f9d1560";
				String cond2 = "pfc4299ba";

//				String postCond1 = dm
//						.getContextModel()
//						.getFactTypes()
//						.getStateFactTypeList()
//						.get(edu.ls3.magus.eval.generators.owls.UtilityClass
//								.randInt(0, dm.getContextModel().getFactTypes()
//										.getStateFactTypeList().size() - 1))
//						.getTypeName();
//				String postCond2 = dm
//						.getContextModel()
//						.getFactTypes()
//						.getStateFactTypeList()
//						.get(edu.ls3.magus.eval.generators.owls.UtilityClass
//								.randInt(0, dm.getContextModel().getFactTypes()
//										.getStateFactTypeList().size() - 1))
//						.getTypeName();
				// String postCond1 = "p3e2f7d26";
				// String postCond2 = "pc550de6b";
				// System.out.println("*****"+postCond1);
				// System.out.println("*****"+postCond2);

				Instance[] params = {
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond1)[0]
														.getParams()[0]
														.getTypeName())[0],
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond1)[0]
														.getParams()[1]
														.getTypeName())[0] };
				isl.add(new StateFactInstanceS(new StateFactInstance(dm
						.getContextModel().getInstaceFactTypeByName(cond1)[0],
						params), false));
				Instance[] params2 = {
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond2)[0]
														.getParams()[0]
														.getTypeName())[0],
						dm.getContextModel()
								.getInstanceByName(
										"v"
												+ dm.getContextModel()
														.getInstaceFactTypeByName(
																cond2)[0]
														.getParams()[1]
														.getTypeName())[0] };
				isl.add(new StateFactInstanceS(new StateFactInstance(dm
						.getContextModel().getInstaceFactTypeByName(cond2)[0],
						params2), false));

				Condition curCondition = new Condition(isl);

				int itcnt = 0;
				while ((itcnt < 500)
						&& (curCondition.getConditions().size() < 150)) {
					System.out.println("Size of facts: "
							+ curCondition.getConditions().size());
					if (isInRange(rl, curCondition.getConditions().size())
							.size() > 0) {
						interestingConditions.add(curCondition);
						iterationList.add(itcnt);
					}
					Map<Service, Map<URI, Instance>> vs = dm
							.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
					System.out.println("Iteration " + itcnt
							+ " possible services: " + vs.size());
					if (vs.size() == 0) {
						System.out.println("No more services!");
						break;

					}

					Service[] ss = vs.keySet().toArray(new Service[0]);
					Service selectedService = ss[UtilityClass.randInt(0,
							ss.length - 1)];
					curCondition = selectedService.getContextAfterExc(
							curCondition, vs.get(selectedService));

					itcnt++;

				}

				// for(int cnt=0; cnt<interestingConditions.size(); cnt++)
				// {
				// System.out.println("Iteration: "+ iterationList.get(cnt));
				// System.out.println(interestingConditions.get(cnt).getConditions().size());
				// }

				// for(int cnt=0; cnt<numOfFeatures.length; cnt++){
				// UtilityClass.createFolder("D:/fmproblems/f"+numOfFeatures[cnt]);
				// }
				// for(int cnt=0; cnt<numOfFeatures.length; cnt++){
				Range preRange = preMap.get(numOfFeatures[no]);
				Range effRange = effMap.get(numOfFeatures[no]);

				for (int precnt = 0; precnt < interestingConditions.size(); precnt++) {
					// int fcnt=1;
					Condition preCondition = interestingConditions.get(precnt);
					if (!preRange
							.isInRange(preCondition.getConditions().size()))
						continue;

					for (int effcnt = interestingConditions.size() - 1; effcnt > precnt; effcnt--) {
						Condition effCondition = interestingConditions
								.get(effcnt);
						if (!effRange.isInRange(effCondition.getConditions()
								.size()))
							continue;

						if (iterationList.get(effcnt)
								- iterationList.get(precnt) > 20)
							continue;

						Problem p = new Problem(dm.getContextModel(),
								preCondition, effCondition);
						// System.out.println("Found one problem for "+numOfFeatures[cnt]
						// + " Pre size: "+ preCondition.getConditions().size()+
						// " Eff Size: "+ effCondition.getConditions().size() +
						// " Iteration difference: "+
						// Integer.toString(iterationList.get(effcnt)-iterationList.get(precnt)));

						int fcnt = findnextnoTXTUbuntu(homeAddress+"BPLECONS/ds/featureModelFS/r"
								+ reqNoList[rcnt] + "/s" + nos[scnt]);

						UtilityClass.writeFile(new File(
								homeAddress+"BPLECONS/ds/featureModelFS/r"
										+ reqNoList[rcnt] + "/s" + nos[scnt]
										+ "/n" + fcnt++ + ".txt"), p
								.Serialize());
					}

					// }
				}
			}
		}

	}
	public static void GenerateFeatures() throws Exception {

		

		DomainModels dm = DomainModels
				.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
		dm.getContextModel().createSimpleContext();

		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();

		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		String postCond2 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));

		for (int rcnt = 1; rcnt <= 3; rcnt++) {

			UtilityClass
					.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"
							+ Integer.toString(reqNoList[ rcnt-1]));

			List<Range> rl = new ArrayList<Range>();
			List<Condition> interestingConditions = new ArrayList<Condition>();
			List<Integer> iterationList = new ArrayList<Integer>();
			Map<Integer, Range> preMap = new HashMap<Integer, Range>();
			Map<Integer, Range> effMap = new HashMap<Integer, Range>();

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], true, premids1[rcnt-1][cnt],
						predevs[cnt]*rcnt);
				rl.add(r);
				preMap.put(numOfFeatures[cnt], r);

			}

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], false, effemids[cnt]*rcnt,
						effdevs[cnt]*rcnt);
				rl.add(r);
				effMap.put(numOfFeatures[cnt], r);
			}
			Condition curCondition = new Condition(isl);

			int itcnt = 0;
			while ((itcnt < 1500) && (curCondition.getConditions().size() < 150*rcnt)) {
				
				
				System.out.println("Size of facts: "
						+ curCondition.getConditions().size());
				if (isInRange(rl, curCondition.getConditions().size()).size() > 0) {
					interestingConditions.add(curCondition);
					iterationList.add(itcnt);
				}
				
				Map<Service, Map<URI, Instance>> vs = dm
						.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
				System.out.println("Iteration " + itcnt
						+ " possible services: " + vs.size());
				if (vs.size() == 0) {
					System.out.println("No more services!");
					break;

				}

				Service[] ss = vs.keySet().toArray(new Service[0]);
				Service selectedService = ss[UtilityClass.randInt(0,
						ss.length - 1)];
				curCondition = selectedService.getContextAfterExc(curCondition,
						vs.get(selectedService));
				

				itcnt++;

			}

			// for(int cnt=0; cnt<interestingConditions.size(); cnt++)
			// {
			// System.out.println("Iteration: "+ iterationList.get(cnt));
			// System.out.println(interestingConditions.get(cnt).getConditions().size());
			// }
			int fcnt=0;
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				UtilityClass
						.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
								+ numOfFeatures[cnt]);
			}
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range preRange = preMap.get(numOfFeatures[cnt]);
				Range effRange = effMap.get(numOfFeatures[cnt]);

				for (int precnt = 0; precnt < interestingConditions.size(); precnt++) {
					// int fcnt=1;
					Condition preCondition = interestingConditions.get(precnt);
					if (!preRange
							.isInRange(preCondition.getConditions().size()))
						continue;

					for (int effcnt = interestingConditions.size() - 1; effcnt > precnt; effcnt--) {
						Condition effCondition = interestingConditions
								.get(effcnt);
						if (!effRange.isInRange(effCondition.getConditions()
								.size()))
							continue;

						 if(iterationList.get(effcnt)-iterationList.get(precnt)>110)
						 continue;

						Problem p = new Problem(dm.getContextModel(),
								preCondition, effCondition);
						System.out.println("Found one problem for "
								+ numOfFeatures[cnt]
								+ " Pre size: "
								+ preCondition.getConditions().size()
								+ " Eff Size: "
								+ effCondition.getConditions().size()
								+ " Iteration difference: "
								+ Integer.toString(iterationList.get(effcnt)
										- iterationList.get(precnt)));

//						 fcnt = findnextnoTXT("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
//								+ numOfFeatures[cnt]);
//						if(fcnt>500)
//							continue;
						File tf = new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
								+ numOfFeatures[cnt]);
						if(tf.listFiles().length>500)
							continue;
						
						
						DecimalFormat df= new DecimalFormat("000");
						
						UtilityClass.writeFile(
								new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
										+ numOfFeatures[cnt] + "\\n" +df.format(iterationList.get(effcnt)-iterationList.get(precnt)) +"-"+ fcnt
										+ ".txt"), p.Serialize());
						fcnt++;
					}

				}
			}
		}
	}
	
	
	public static void GenerateFeaturesUbuntu() throws Exception {

		

		DomainModels dm = DomainModels
				.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
		dm.getContextModel().createSimpleContext();

		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();

		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		String postCond2 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));

		for (int rcnt = 1; rcnt <= 3; rcnt++) {

			UtilityClass
					.createFolder(homeAddress+ "BPLECONS/ds/featureModelFF/r"
							+ Integer.toString(reqNoList[ rcnt-1]));

			List<Range> rl = new ArrayList<Range>();
			List<Condition> interestingConditions = new ArrayList<Condition>();
			List<Integer> iterationList = new ArrayList<Integer>();
			Map<Integer, Range> preMap = new HashMap<Integer, Range>();
			Map<Integer, Range> effMap = new HashMap<Integer, Range>();

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], true, premids1[rcnt-1][cnt],
						predevs[cnt]*rcnt);
				rl.add(r);
				preMap.put(numOfFeatures[cnt], r);

			}

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], false, effemids[cnt]*rcnt,
						effdevs[cnt]*rcnt);
				rl.add(r);
				effMap.put(numOfFeatures[cnt], r);
			}
			Condition curCondition = new Condition(isl);

			int itcnt = 0;
			while ((itcnt < 1500) && (curCondition.getConditions().size() < 150*rcnt)) {
				
				
				System.out.println("Size of facts: "
						+ curCondition.getConditions().size());
				if (isInRange(rl, curCondition.getConditions().size()).size() > 0) {
					interestingConditions.add(curCondition);
					iterationList.add(itcnt);
				}
				
				Map<Service, Map<URI, Instance>> vs = dm
						.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
				System.out.println("Iteration " + itcnt
						+ " possible services: " + vs.size());
				if (vs.size() == 0) {
					System.out.println("No more services!");
					break;

				}

				Service[] ss = vs.keySet().toArray(new Service[0]);
				Service selectedService = ss[UtilityClass.randInt(0,
						ss.length - 1)];
				curCondition = selectedService.getContextAfterExc(curCondition,
						vs.get(selectedService));
				

				itcnt++;

			}

			// for(int cnt=0; cnt<interestingConditions.size(); cnt++)
			// {
			// System.out.println("Iteration: "+ iterationList.get(cnt));
			// System.out.println(interestingConditions.get(cnt).getConditions().size());
			// }
			int fcnt=0;
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				UtilityClass
						.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
								+ numOfFeatures[cnt]);
			}
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range preRange = preMap.get(numOfFeatures[cnt]);
				Range effRange = effMap.get(numOfFeatures[cnt]);

				for (int precnt = 0; precnt < interestingConditions.size(); precnt++) {
					// int fcnt=1;
					Condition preCondition = interestingConditions.get(precnt);
					if (!preRange
							.isInRange(preCondition.getConditions().size()))
						continue;

					for (int effcnt = interestingConditions.size() - 1; effcnt > precnt; effcnt--) {
						Condition effCondition = interestingConditions
								.get(effcnt);
						if (!effRange.isInRange(effCondition.getConditions()
								.size()))
							continue;

						 if(iterationList.get(effcnt)-iterationList.get(precnt)>110)
						 continue;

						Problem p = new Problem(dm.getContextModel(),
								preCondition, effCondition);
						System.out.println("Found one problem for "
								+ numOfFeatures[cnt]
								+ " Pre size: "
								+ preCondition.getConditions().size()
								+ " Eff Size: "
								+ effCondition.getConditions().size()
								+ " Iteration difference: "
								+ Integer.toString(iterationList.get(effcnt)
										- iterationList.get(precnt)));

//						 fcnt = findnextnoTXT("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
//								+ numOfFeatures[cnt]);
//						if(fcnt>500)
//							continue;
						File tf = new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
								+ numOfFeatures[cnt]);
						if(tf.listFiles().length>500)
							continue;
						
						
						DecimalFormat df= new DecimalFormat("000");
						
						UtilityClass.writeFile(
								new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(reqNoList[ rcnt-1])+"\\"+
										+ numOfFeatures[cnt] + "\\n" +df.format(iterationList.get(effcnt)-iterationList.get(precnt)) +"-"+ fcnt
										+ ".txt"), p.Serialize());
						fcnt++;
					}

				}
			}
		}
	}
	
	public static void GenerateFeaturesNew(int noOfServices, int reqSt, int reqEnd, int fchSt,int fchEnd,int iteration) throws Exception {

		

		
		DomainModels dm = DomainModels
				.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s"+noOfServices);
		dm.getContextModel().createSimpleContext();

		

		UtilityClass
		.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF"+noOfServices);
						
		for(int reqcnt=reqSt; reqcnt<=reqEnd; reqcnt++){
			UtilityClass
			.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF"+noOfServices+"\\r"
					+ Integer.toString(reqNoList[ reqcnt]));
			for(int fcnt=fchSt; fcnt<=fchEnd ; fcnt++){
				UtilityClass
				.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF"+noOfServices+"\\r"+Integer.toString(reqNoList[ reqcnt])+"\\"+
						+ numOfFeatures[fcnt]);
				for(int itcnt=0;itcnt<iteration ;itcnt++){
					
					System.out.println("Iteration: "+itcnt +"Trying to find a fmc for reqno: "+reqNoList[ reqcnt]+" and feature model configuration size: "+numOfFeatures[fcnt]);
					Problem pr  = DoAWalk(dm,reqcnt,fcnt,110);
					
					int trycntr=0;
					while((pr==null) && (trycntr<25)){
						  pr= DoAWalk(dm,reqcnt,fcnt,110);
						  System.out.println("Trying Again!");
						  trycntr++;
					}
					if(pr!=null){
					DecimalFormat df= new DecimalFormat("000");
					UtilityClass.writeFile(
							new File("D:\\Development\\BPLECONS\\ds\\featureModelFF"+noOfServices+"\\r"+Integer.toString(reqNoList[ reqcnt])+"\\"+
									+ numOfFeatures[fcnt] + "\\n" +df.format(pr.getIterationNo()) +"-"+ itcnt
									+ ".txt"), pr.Serialize());
					}
				}
			}
		}

			
			
	}
	
	public static void GenerateFeaturesNewUbuntu(int noOfServices, int reqSt, int reqEnd, int fchSt,int fchEnd,int iteration,int walkIteration) throws Exception {

		

		
		DomainModels dm = DomainModels
				.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s"+noOfServices);
		dm.getContextModel().createSimpleContext();

		

		UtilityClass
		.createFolder(homeAddress+ "BPLECONS/ds/featureModelFF"+noOfServices);
						
		for(int reqcnt=reqSt; reqcnt<=reqEnd; reqcnt++){
			UtilityClass
			.createFolder(homeAddress+ "BPLECONS/ds/featureModelFF"+noOfServices+"/r"
					+ Integer.toString(reqNoListFeature[ reqcnt]));
			for(int fcnt=fchSt; fcnt<=fchEnd ; fcnt++){
				UtilityClass
				.createFolder(homeAddress+ "BPLECONS/ds/featureModelFF"+noOfServices+"/r"+Integer.toString(reqNoListFeature[ reqcnt])+"/"+
						+ numOfFeatures[fcnt]);
				for(int itcnt=0;itcnt<iteration ;itcnt++){
					
					System.out.println("Iteration: "+itcnt +"Trying to find a fmc for reqno: "+reqNoListFeature[ reqcnt]+" and feature model configuration size: "+numOfFeatures[fcnt]);
					Problem pr  = DoAWalk(dm,reqcnt,fcnt,walkIteration);
					
					int trycntr=0;
					while((pr==null) && (trycntr<25)){
						  pr= DoAWalk(dm,reqcnt,fcnt,walkIteration*(fcnt+1));
						  System.out.println("Trying Again!");
						  trycntr++;
					}
					if(pr!=null){
					DecimalFormat df= new DecimalFormat("000");
					UtilityClass.writeFile(
							new File(homeAddress+ "BPLECONS/ds/featureModelFF"+noOfServices+"/r"+Integer.toString(reqNoListFeature[ reqcnt])+"/"+
									+ numOfFeatures[fcnt] + "/m" +df.format(pr.getIterationNo()) +"-"+ itcnt
									+ ".txt"), pr.Serialize());
					}
				}
			}
		}

			
			
	}
	
	private static Problem DoAWalk(DomainModels dm, int ReqIndex, int featureIndex,int iterations ) {
		
		
		
		List<Service> alreadySelectedService = new ArrayList<Service>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		String postCond2 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));


		Condition curCondition = new Condition(isl);

		Range preRange = new Range(numOfFeatures[featureIndex], true, premids1[ReqIndex][featureIndex],
				predevs[featureIndex]);
		Range effRange = new Range(numOfFeatures[featureIndex], false, effemids1[ReqIndex][featureIndex],
				effdevs[featureIndex]);
		List<Condition> preConditionList =new ArrayList<Condition>();
		List<Condition> effConditionList =new ArrayList<Condition>();
		List<Integer> preIterationList = new ArrayList<Integer>();
		List<Integer> effIterationList = new ArrayList<Integer>();
		int itcnt=0;
		boolean done =false;
		boolean startCounting =false;
		
		while(itcnt<iterations){
			int sp = preRange.relationToRange(curCondition.getConditions().size());
			int ef = effRange.relationToRange(curCondition.getConditions().size());
			//(ef==1)||
			if((itcnt==iterations-1)||done){
				if((preConditionList.size()==0)||(effConditionList.size()==0))
					return null;
				int rndPreIndex = UtilityClass.randInt(0, preConditionList.size()-1);
				int rndEffIndex =UtilityClass.randInt(0, effConditionList.size()-1);
				Problem pr= new Problem(dm.getContextModel(),preConditionList.get(rndPreIndex),effConditionList.get(rndEffIndex));
				pr.setIterationNo(effIterationList.get(rndEffIndex)-preIterationList.get(rndPreIndex));
				return pr;
			}
			if((sp==1)&&(preConditionList.size()==0))
				return null;
			if(sp==0)
			{
				preConditionList.add(curCondition);
				preIterationList.add(itcnt);
			}else{
			if(ef==0)
			{
				effConditionList.add(curCondition);
				effIterationList.add(itcnt);
			}}
			Map<Service, Map<URI, Instance>> vs = dm
					.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
			
			for(Service sv: alreadySelectedService)
				if(vs.containsKey(sv))
					vs.remove(vs);
			if(vs.size()==0){
				done =true;
				continue;
			}
			Service[] ss = vs.keySet().toArray(new Service[0]);
			Service selectedService = ss[UtilityClass.randInt(0,
					ss.length - 1)];
			alreadySelectedService.add(selectedService);
			curCondition = selectedService.getContextAfterExc(curCondition,
					vs.get(selectedService));
			if(sp==0)
				startCounting=true;
			if(startCounting)
				itcnt++;
				
		}
		
		return null;
	}
	
@SuppressWarnings("unused")
private static Problem DoAWalkNew(DomainModels dm, int size,int iterations ) {
		
		
		
		List<Service> alreadySelectedService = new ArrayList<Service>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

		String postCond1 = dm
				.getContextModel()
				.getFactTypes()
				.getStateFactTypeList()
				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
						dm.getContextModel().getFactTypes()
								.getStateFactTypeList().size() - 1))
				.getTypeName();
		String postCond2 = dm
				.getContextModel()
				.getFactTypes()
				.getStateFactTypeList()
				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
						dm.getContextModel().getFactTypes()
								.getStateFactTypeList().size() - 1))
				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));


		Condition curCondition = new Condition(isl);
		Condition preCondition = new Condition(isl);
		
		List<Condition> preConditionList =new ArrayList<Condition>();
		List<Condition> effConditionList =new ArrayList<Condition>();
		List<Integer> preIterationList = new ArrayList<Integer>();
		List<Integer> effIterationList = new ArrayList<Integer>();
		int itcnt=0;
		boolean done =false;
		boolean startCounting =false;
		
		while(itcnt<iterations){
			
			//(ef==1)||
			if((itcnt==iterations-1)||done){
				
				
				Problem pr= new Problem(dm.getContextModel(), preCondition,curCondition);
				
				return pr;
			}
			
		
			Map<Service, Map<URI, Instance>> vs = dm
					.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
			
			for(Service sv: alreadySelectedService)
				if(vs.containsKey(sv))
					vs.remove(vs);
			if(vs.size()==0){
				done =true;
				continue;
			}
			Service[] ss = vs.keySet().toArray(new Service[0]);
			Service selectedService = ss[UtilityClass.randInt(0,
					ss.length - 1)];
			alreadySelectedService.add(selectedService);
			curCondition = selectedService.getContextAfterExc(curCondition,
					vs.get(selectedService));
			
				itcnt++;
				
		}
		
		Problem pr= new Problem(dm.getContextModel(), preCondition,curCondition);
		
		return pr;
	}
	@SuppressWarnings("unused")
	private static void DoAWalk2(DomainModels dm,int iterations ) {
		
		List<Service> alreadySelectedService = new ArrayList<Service>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

		String postCond1 = dm
				.getContextModel()
				.getFactTypes()
				.getStateFactTypeList()
				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
						dm.getContextModel().getFactTypes()
								.getStateFactTypeList().size() - 1))
				.getTypeName();
		String postCond2 = dm
				.getContextModel()
				.getFactTypes()
				.getStateFactTypeList()
				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
						dm.getContextModel().getFactTypes()
								.getStateFactTypeList().size() - 1))
				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));


		Condition curCondition = new Condition(isl);

		
	
		int itcnt=0;
		boolean done =false;
		boolean startCounting =false;
		
		while(itcnt<iterations){

			//(ef==1)||
			

			Map<Service, Map<URI, Instance>> vs = dm
					.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
			
			for(Service sv: alreadySelectedService)
				if(vs.containsKey(sv))
					vs.remove(vs);
			if(vs.size()==0){
				done =true;
				continue;
			}
			Service[] ss = vs.keySet().toArray(new Service[0]);
			Service selectedService = ss[UtilityClass.randInt(0,
					ss.length - 1)];
			alreadySelectedService.add(selectedService);
			curCondition = selectedService.getContextAfterExc(curCondition,
					vs.get(selectedService));
			
			System.out.println("Size of facts: "+curCondition.getConditions().size());
		
				itcnt++;
				
		}
		
	
	}
	public static void GenerateFeaturesTemp() throws Exception {

		

		DomainModels dm = DomainModels
				.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
		dm.getContextModel().createSimpleContext();

		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();

		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		String postCond2 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//								.getStateFactTypeList().size() - 1))
//				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
										.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));

		for (int rcnt = 2; rcnt <= 2; rcnt++) {

			UtilityClass
					.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"
							+ Integer.toString(rcnt*3));

			List<Range> rl = new ArrayList<Range>();
			List<Condition> interestingConditions = new ArrayList<Condition>();
			List<Integer> iterationList = new ArrayList<Integer>();
			Map<Integer, Range> preMap = new HashMap<Integer, Range>();
			Map<Integer, Range> effMap = new HashMap<Integer, Range>();

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], true, premids1[rcnt-1][cnt],
						predevs[cnt]*rcnt);
				rl.add(r);
				preMap.put(numOfFeatures[cnt], r);

			}

			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range r = new Range(numOfFeatures[cnt], false, effemids[cnt]*rcnt,
						effdevs[cnt]*rcnt);
				rl.add(r);
				effMap.put(numOfFeatures[cnt], r);
			}
			Condition curCondition = new Condition(isl);

			int itcnt = 0;
			while ((itcnt < 350) && (curCondition.getConditions().size() < 150*rcnt)) {
				
				
				System.out.println("Size of facts: "
						+ curCondition.getConditions().size());
				
				Problem p = new Problem(dm.getContextModel(),
						new Condition(isl), curCondition);
				System.out.println("Found one problem for "
						+ 2
						+ " Pre size: "
						+ 2
						+ " Eff Size: "
						+ curCondition.getConditions().size()
						+ " Iteration difference: "
						+ itcnt);
				DecimalFormat df= new DecimalFormat("000");
				
				UtilityClass.writeFile(
						new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
								+ numOfFeatures[3] + "\\n" +df.format(itcnt)
								+ ".txt"), p.Serialize());
				
				Map<Service, Map<URI, Instance>> vs = dm
						.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
				System.out.println("Iteration " + itcnt
						+ " possible services: " + vs.size());
				if (vs.size() == 0) {
					System.out.println("No more services!");
					break;

				}

				Service[] ss = vs.keySet().toArray(new Service[0]);
				Service selectedService = ss[UtilityClass.randInt(0,
						ss.length - 1)];
				curCondition = selectedService.getContextAfterExc(curCondition,
						vs.get(selectedService));
				
				
				

				itcnt++;

			}

			// for(int cnt=0; cnt<interestingConditions.size(); cnt++)
			// {
			// System.out.println("Iteration: "+ iterationList.get(cnt));
			// System.out.println(interestingConditions.get(cnt).getConditions().size());
			// }
			int fcnt=0;
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				UtilityClass
						.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
								+ numOfFeatures[cnt]);
			}
			for (int cnt = 0; cnt < numOfFeatures.length; cnt++) {
				Range preRange = preMap.get(numOfFeatures[cnt]);
				Range effRange = effMap.get(numOfFeatures[cnt]);

				for (int precnt = 0; precnt < interestingConditions.size(); precnt++) {
					// int fcnt=1;
					Condition preCondition = interestingConditions.get(precnt);
					if (!preRange
							.isInRange(preCondition.getConditions().size()))
						continue;

					for (int effcnt = interestingConditions.size() - 1; effcnt > precnt; effcnt--) {
						Condition effCondition = interestingConditions
								.get(effcnt);
						if (!effRange.isInRange(effCondition.getConditions()
								.size()))
							continue;

						 if(iterationList.get(effcnt)-iterationList.get(precnt)>200)
						 continue;

						Problem p = new Problem(dm.getContextModel(),
								preCondition, effCondition);
						System.out.println("Found one problem for "
								+ numOfFeatures[cnt]
								+ " Pre size: "
								+ preCondition.getConditions().size()
								+ " Eff Size: "
								+ effCondition.getConditions().size()
								+ " Iteration difference: "
								+ Integer.toString(iterationList.get(effcnt)
										- iterationList.get(precnt)));

//						 fcnt = findnextnoTXT("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
//								+ numOfFeatures[cnt]);
//						if(fcnt>500)
//							continue;
						File tf = new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
								+ numOfFeatures[cnt]);
						if(tf.listFiles().length>500)
							continue;
						
						
						DecimalFormat df= new DecimalFormat("000");
						
						UtilityClass.writeFile(
								new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+Integer.toString(rcnt*3)+"\\"+
										+ numOfFeatures[cnt] + "\\n" +df.format(iterationList.get(effcnt)-iterationList.get(precnt)) +"-"+ fcnt
										+ ".txt"), p.Serialize());
						fcnt++;
					}

				}
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private static int findnextnoPDDL(String str) {
		
		int retvalue = 1;
		File f = new File(str + "\\n" + retvalue + ".pddl");
		while (f.exists()) {
			retvalue++;
			f = new File(str + "\\n" + retvalue + ".pddl");
		}
		return retvalue;
	}

	private static int findnextnoTXT(String str) {
		
		int retvalue = 1;
		File f = new File(str + "\\n" + retvalue + ".txt");
		while (f.exists()) {
			retvalue++;
			f = new File(str + "\\n" + retvalue + ".txt");
		}
		return retvalue;
	}
	private static int findnextnoTXTUbuntu(String str) {
		
		int retvalue = 1;
		File f = new File(str + "/n" + retvalue + ".txt");
		while (f.exists()) {
			retvalue++;
			f = new File(str + "/n" + retvalue + ".txt");
		}
		return retvalue;
	}

}
