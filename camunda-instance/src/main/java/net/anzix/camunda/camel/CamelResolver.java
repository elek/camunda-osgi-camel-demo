package net.anzix.camunda.camel;

import org.apache.camel.CamelContext;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Resolve camel variable in the process engine.
 */
public class CamelResolver extends ELResolver {

    private static final Logger LOGGER = Logger.getLogger(CamelResolver.class.getName());

    private ProcessEngine processEngine;

    private CamelContext camelContext;

    public Object getValue(ELContext context, Object base, Object property) {
        if ("camel".equals(property)) {
            context.setPropertyResolved(true);
            return new CamelAdapter(camelContext, processEngine);
        }
        return null;

    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    public Class<?> getCommonPropertyType(ELContext context, Object arg) {
        return Object.class;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
                                                             Object arg) {
        return null;
    }

    public Class<?> getType(ELContext context, Object arg1, Object arg2) {
        return Object.class;
    }

    public void bindService(CamelContext camel) {
        this.camelContext = camel;
        LOGGER.info("adding camel context");
    }

    public void unbindService(CamelContext camel) {
        LOGGER.info("removing camel context");
        this.camelContext = null;
    }

}
