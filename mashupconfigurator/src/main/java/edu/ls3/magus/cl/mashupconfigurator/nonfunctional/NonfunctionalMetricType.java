package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public abstract class NonfunctionalMetricType {
    public abstract String getCode();

    public abstract double getAggregatedValue(Map<Service, ServiceNonfunctionalAnnotation> map, FlowComponentNode fcn)
            throws Exception;

    public abstract double findFeatureAggregatedValue(double[] contributionValues);

    public abstract double getRegressionValue(double actualValue);

    public abstract double getInverseRegressionValue(double regressionValue);

    public abstract double getNeutralValue();

    public static Set<NonfunctionalMetricType> getAllSupportedTypes() {
        return ImmutableSet.<NonfunctionalMetricType>builder().add(ExecutionTimeType.getInstance())
                .add(ReliabilityType.getInstance()).build();
    }

	public static Optional<NonfunctionalMetricType> getMetricByName(String nfmtString) {
		return NonfunctionalMetricType.getAllSupportedTypes().stream().filter(value -> value.getCode().equals(nfmtString)).findAny();
	}
}
