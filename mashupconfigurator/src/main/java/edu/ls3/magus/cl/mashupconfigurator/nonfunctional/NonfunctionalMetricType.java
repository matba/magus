package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public interface NonfunctionalMetricType {
	public String getCode();
	
	public double getAggregatedValue(Map<Service, ServiceNonfunctionalAnnotation> map, FlowComponentNode fcn ) throws Exception;

	public double findFeatureAggregatedValue(double[] contributionValues);
}
