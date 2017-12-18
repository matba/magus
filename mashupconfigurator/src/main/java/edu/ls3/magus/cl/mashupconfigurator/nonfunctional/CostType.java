package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class CostType extends NonfunctionalMetricType {

	private static CostType instance = null;

	private CostType() {

	}

	public static CostType getInstance() {
		if (instance == null) {
			instance = new CostType();
		}
		return instance;
	}

	@Override
	public String getCode() {
		return "COST";
	}

	@Override
	public double getAggregatedValue(Map<Service, ServiceNonfunctionalAnnotation> map, FlowComponentNode fcn)
			throws Exception {
		double result = 0d;
		if (fcn.getNodes().isEmpty())
			return result;

		List<OperationNode> graph = fcn.convertToWorkflow();

		for (OperationNode on : graph) {
			if (on.getCalledService() != null && on.getCalledService().getCalledService() != null
					&& map.containsKey(on.getCalledService().getCalledService()))
				result += map.get(on.getCalledService().getCalledService()).getAnnotation().get(this).getAverage();
		}

		return result;
	}

	@Override
	public double findFeatureAggregatedValue(double[] contributionValues) {
		double sum = 0d;

		for (double val : contributionValues)
			sum += val;

		return sum;
	}

	@Override
	public double getRegressionValue(double actualValue) {
		return actualValue;
	}

	@Override
	public double getInverseRegressionValue(double regressionValue) {

		return regressionValue;

	}

	@Override
	public double getNeutralValue() {

		return 0d;
	}

}
