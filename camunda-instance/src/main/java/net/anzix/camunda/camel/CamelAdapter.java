package net.anzix.camunda.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.InOut;
import org.apache.camel.ProducerTemplate;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import java.util.*;


/**
 * Calling camel route FROM camunda process.
 */
public class CamelAdapter {

    private CamelContext camelContext;

    private ProcessEngine engine;

    public CamelAdapter(CamelContext camelContext, ProcessEngine engine) {
        this.camelContext = camelContext;
        this.engine = engine;
    }

    public Object sendTo(String endpointUri) {
        ActivityExecution execution = Context.getExecutionContext().getExecution();
        return sendTo(endpointUri, execution.getVariableNames(), ExchangePattern.OutOnly);
    }


    public Object sendTo(String endpointUri, String processVariables) {
        List<String> vars = Arrays.asList(processVariables.split("\\s*,\\s*"));
        return sendTo(endpointUri, vars, ExchangePattern.OutOnly);
    }

    public Object call(String endpointUri, String processVariables) {
        List<String> vars = Arrays.asList(processVariables.split("\\s*,\\s*"));
        return sendTo(endpointUri, vars, ExchangePattern.InOut);
    }

    private Object sendTo(String endpointUri, Collection<String> variables, ExchangePattern pattern) {
        ActivityExecution execution = (ActivityExecution) Context.getExecutionContext().getExecution();
        Map<String, Object> variablesToSend = new HashMap<String, Object>();
        for (String var : variables) {
            Object value = execution.getVariable(var);
            if (value == null) {
                throw new IllegalArgumentException("Process variable '" + var + "' no found!");
            }
            variablesToSend.put(var, value);
        }


        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Object routeResult = producerTemplate.sendBodyAndProperty(endpointUri, pattern,
                variablesToSend, org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_PROCESS_INSTANCE_ID,
                execution.getProcessInstanceId());

        if (routeResult instanceof Map) {
            for (Object key : ((Map) routeResult).keySet()) {
                execution.setVariable(key.toString(), ((Map) routeResult).get(key));
            }
        }
        return routeResult;
    }
}
