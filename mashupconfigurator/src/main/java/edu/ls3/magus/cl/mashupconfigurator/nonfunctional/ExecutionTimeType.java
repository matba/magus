package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class ExecutionTimeType extends NonfunctionalMetricType {

	private static ExecutionTimeType instance = null;

	private ExecutionTimeType() {

	}

	public static ExecutionTimeType getInstance() {
		if (instance == null) {
			instance = new ExecutionTimeType();
		}
		return instance;
	}

	@Override
	public String getCode() {

		return "EXECUTION_TIME";
	}

	@Override
	public double getAggregatedValue(Map<Service, ServiceNonfunctionalAnnotation> map, FlowComponentNode fcn)
			throws Exception {

		if (fcn.getNodes().isEmpty())
			return 0d;

		List<OperationNode> graph = fcn.convertToWorkflow();

		List<OperationNode> doneNodes = new ArrayList<OperationNode>();
		Map<OperationNode, Double> executionTime = new HashMap<OperationNode, Double>();
		OperationNode startNode = null;
		for (OperationNode gn : graph)
			if (gn.isStartNode()) {
				startNode = gn;
				break;
			}
		executionTime.put(startNode, 0d);
		doneNodes.add(startNode);

		Queue<OperationNode> q = new LinkedList<OperationNode>();
		q.add(startNode);

		while (!q.isEmpty()) {
			OperationNode curNode = q.remove();
			for (OperationNode gn : curNode.getEdges()) {
				if (doneNodes.contains(gn))
					continue;
				List<OperationNode> preNodes = OperationNode.GetPreNodes(graph, gn);
				boolean allDone = true;
				for (OperationNode gn2 : preNodes)
					if (!doneNodes.contains(gn2))
						allDone = false;

				if (allDone) {
					doneNodes.add(gn);
					q.add(gn);
					double timeAfterExecution = -1d;
					for (OperationNode gn2 : preNodes)
						if ((timeAfterExecution == -1d) || (timeAfterExecution < executionTime.get(gn2)))
							timeAfterExecution = executionTime.get(gn2);
					if (!gn.isEndNode())
						timeAfterExecution += map.get(gn.getCalledService().getCalledService()).getAnnotation()
								.get(this).getAverage();
					executionTime.put(gn, timeAfterExecution);

				}
			}
		}

		return executionTime.get(OperationNode.getEndNode(graph));
	}

	public static double findExecutionTime(Map<Service, ServiceNonfunctionalAnnotation> map,
			List<OperationNode> graph) {
		List<OperationNode> doneNodes = new ArrayList<OperationNode>();
		Map<OperationNode, Double> executionTime = new HashMap<OperationNode, Double>();
		OperationNode startNode = null;
		for (OperationNode gn : graph)
			if (gn.isStartNode()) {
				startNode = gn;
				break;
			}
		executionTime.put(startNode, 0d);
		doneNodes.add(startNode);

		Queue<OperationNode> q = new LinkedList<OperationNode>();
		q.add(startNode);

		while (!q.isEmpty()) {
			OperationNode curNode = q.remove();
			for (OperationNode gn : curNode.getEdges()) {
				if (doneNodes.contains(gn))
					continue;
				List<OperationNode> preNodes = OperationNode.GetPreNodes(graph, gn);
				boolean allDone = true;
				for (OperationNode gn2 : preNodes)
					if (!doneNodes.contains(gn2))
						allDone = false;

				if (allDone) {
					doneNodes.add(gn);
					q.add(gn);
					double timeAfterExecution = -1d;
					for (OperationNode gn2 : preNodes)
						if ((timeAfterExecution == -1d) || (timeAfterExecution < executionTime.get(gn2)))
							timeAfterExecution = executionTime.get(gn2);
					if (!gn.isEndNode())
						timeAfterExecution += map.get(gn.getCalledService().getCalledService()).getAnnotation()
								.get(ExecutionTimeType.getInstance()).getExecutionValue();
					executionTime.put(gn, timeAfterExecution);

				}
			}
		}

		return executionTime.get(OperationNode.getEndNode(graph));
	}

	@Override
	public double findFeatureAggregatedValue(double[] contributionValues) {
		double sum = 0;

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
