package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class ReliabilityType extends NonfunctionalMetricType {

    private static ReliabilityType instance = null;

    private ReliabilityType() {

    }

    public static ReliabilityType getInstance() {
        if (instance == null) {
            instance = new ReliabilityType();
        }
        return instance;
    }

    @Override
    public String getCode() {
        // TODO Auto-generated method stub
        return "RELIABILITY";
    }

    @Override
    public double getAggregatedValue(Map<Service, ServiceNonfunctionalAnnotation> map, FlowComponentNode fcn)
            throws Exception {
        // TODO Auto-generated method stub
        double result = 1d;
        if (fcn.getNodes().isEmpty())
            return result;

        List<OperationNode> graph = fcn.convertToWorkflow();

        for (OperationNode on : graph) {
            if (on.getCalledService() != null && on.getCalledService().getCalledService() != null
                    && map.containsKey(on.getCalledService().getCalledService()))
                result *= map.get(on.getCalledService().getCalledService()).getAnnotation().get(this).getAverage();
        }

        return result;
    }

    @Override
    public double findFeatureAggregatedValue(double[] contributionValues) {
        double sum = 1d;

        for (double val : contributionValues)
            sum *= val;

        return sum;
    }

    @Override
    public double getRegressionValue(double actualValue) {
        // TODO Auto-generated method stub
        return Math.log(actualValue);
    }

    @Override
    public double getInverseRegressionValue(double regressionValue) {
        // TODO Auto-generated method stub

        // return regressionValue;
        return Math.pow(Math.E, regressionValue);
    }

    @Override
    public double getNeutralValue() {

        return 1d;
    }

}
