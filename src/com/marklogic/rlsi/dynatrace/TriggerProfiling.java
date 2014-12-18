
 /**
  * This template file was generated by dynaTrace client.
  * The dynaTrace community portal can be found here: http://community.compuwareapm.com/
  * For information how to publish a plugin please visit http://community.compuwareapm.com/plugins/contribute/
  **/

package com.marklogic.rlsi.dynatrace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Action;
import com.dynatrace.diagnostics.pdk.ActionEnvironment;
import com.dynatrace.diagnostics.pdk.Incident;
import com.dynatrace.diagnostics.pdk.Plugin;
import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.TaskEnvironment;
import com.dynatrace.diagnostics.pdk.Violation;


public class TriggerProfiling implements Action {

	private static final Logger log = Logger.getLogger(TriggerProfiling.class.getName());

	/**
	 * Initializes the Action Plugin. This method is always
	 * called before <tt>execute</tt>.
	 * If the returned status is <tt>null</tt> or the status code is a
	 * non-success code then <tt>execute</tt> will not be called.
	 *
	 * @param env the configured <tt>ActionEnvironment</tt> for this Plugin;
	 *            <b>does not contain any incidents</b>
	 * @see Plugin#teardown()
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 * @throws Exception
	 */
	@Override
	public Status setup(ActionEnvironment env) throws Exception {
		// TODO
		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Executes the Action Plugin to process incidents.
	 *
	 * <p>
	 * This method is called at the scheduled intervals, but only if incidents
	 * occurred in the meantime. If the Plugin execution takes longer than the
	 * schedule interval, subsequent calls to
	 * {@link #execute(ActionEnvironment)} will be skipped until this method
	 * returns. After the execution duration exceeds the schedule timeout,
	 * {@link TaskEnvironment#isStopped()} will return <tt>true</tt>. In this
	 * case execution should be stopped as soon as possible. If the Plugin
	 * ignores {@link TaskEnvironment#isStopped()} or fails to stop execution in
	 * a reasonable timeframe, the execution thread will be stopped ungracefully
	 * which might lead to resource leaks!
	 *
	 * @param env
	 *            a <tt>ActionEnvironment</tt> object that contains the Plugin
	 *            configuration and incidents
	 * @return a <tt>Status</tt> object that describes the result of the
	 *         method call
	 */	@Override
	public Status execute(ActionEnvironment env) throws Exception {
		// this sample shows how to receive and act on incidents
		Collection<Incident> incidents = env.getIncidents();

        List<String> watchingURI = new ArrayList<String>();

        for (Incident incident : incidents) {
            String message = incident.getMessage();

            log.info("----- Incident " + message + " triggered. ------");
            log.info(String.format("\tDescription : %s", incident.getIncidentRule().getDescription()));
            log.info(String.format("\tRule        : %s", incident.getIncidentRule().getName()));
            for (Violation violation : incident.getViolations()) {
                log.info("Measure " + violation.getViolatedMeasure().getName() + " violated threshold.");
                log.info(String.format("\t\tApplication : %s", violation.getViolatedMeasure().getApplication()));
                log.info(String.format("\t\tMeasure     : %s", violation.getViolatedMeasure().getDescription()));
                log.info(String.format("\t\tSource      : %s", violation.getViolatedMeasure().getSource().toString()));
                log.info(String.format("\t\tType        : %s", violation.getViolatedThreshold().getType().name()));
                log.info(String.format("\t\tThreshold   : %s", violation.getViolatedThreshold().getValue().toString()));

                for (Violation.TriggerValue tv : violation.getTriggerValues()) {
                    log.info(String.format("\t\t\tTriggerValue Value '%s'", tv.getValue()));
                }

                for (String sp : violation.getViolatedMeasure().getSplittings()) {
                    log.info(String.format("\t\t\tSplitting '%s'", sp));
                    //if (sp.matches("/[a-zA-Z0-9].*")) {
                    //	log.info("\t\tWATCHING URI = " + sp);
                    watchingURI.add(sp);
                    //}
                }
            }
        }

		log.info("Executing on " + env.getHost().getAddress() + " !!!");
		log.info("Execute inside of " + this.getClass().getCanonicalName());

		String protocol = env.getConfigString("protocol");
		String host = env.getConfigString("serverHostname");
		String path = env.getConfigString("path");
		Integer port = 8080;//env.getConfigLong("port").intValue();
		
		log.info("PROTOCOL = " + protocol);
		log.info("HOST = " + host);
		log.info("PATH = " + path);
		log.info("PORT = " + port);
		
		if (! watchingURI.isEmpty()) {
			ProfilingURLManager manager = new ProfilingURLManager(protocol, host, port, path);
			for (String url: watchingURI) {
				manager.addUrl(url);
			}
		}

		return new Status(Status.StatusCode.Success);
	}
	/**
	 * Shuts the Plugin down and frees resources. This method is called either way
	 * if the Action setup/execution has failed or was successful.
	 *
	 * @see Action#setup(ActionEnvironment)
	 */
	@Override
	public void teardown(ActionEnvironment env) throws Exception {
		// TODO
	}
}
