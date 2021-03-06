/*******************************************************************************
 *  Imixs Workflow 
 *  Copyright (C) 2001, 2011 Imixs Software Solutions GmbH,  
 *  http://www.imixs.com
 *  
 *  This program is free software; you can redistribute it and/or 
 *  modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You can receive a copy of the GNU General Public
 *  License at http://www.gnu.org/licenses/gpl.html
 *  
 *  Project: 
 *  	http://www.imixs.org
 *  	http://java.net/projects/imixs-workflow
 *  
 *  Contributors:  
 *  	Imixs Software Solutions GmbH - initial API and implementation
 *  	Ralph Soika - Software Developer
 *******************************************************************************/

package org.imixs.workflow.plugins;

import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.exceptions.PluginException;

/**
 * This Plugin updates application specific settings.
 * 
 * <ul>
 * <li>txtWorkflowEditorID - optional EditorID to be used by an application
 * <li>txtWorkflowImageURL - visual image can be displayed by an application
 * <li>txtWorkflowStatus - Status of current process
 * <li>txtWorkflowGroup - Workflow Group of current process
 * <li>txtWorkflowAbstract - Abstract text
 * <li>txtWorkflowSummary - Summary
 * 
 * 
 * These settings can be configured by the imixs modeler on the Application
 * Property Tab on a ProcessEntity.
 * 
 * The Plugin determines the new settings by fetching the next ProcessEntity.
 * The Next ProcessEntity is defined by the ActivityEntity attribute
 * 'numNextProcessID'
 * 
 * 
 * Version 1.1
 * 
 * The Plugin will test if the provided Model supports ExtendedModels. If so the
 * Plugin will fetch the next ProcessEntity by the current used modelVersion of
 * the workitem.
 * 
 * Version 1.2 The plugin submits the new settings directly in the run() method,
 * so other plugins can access the new properties for further operations
 * http://java.net/jira/browse/IMIXS_WORKFLOW-81
 * 
 * @author Ralph Soika
 * @version 1.2
 * @see org.imixs.workflow.WorkflowManager
 * 
 */
public class ApplicationPlugin extends AbstractPlugin {

	public static final String PROCESS_UNDEFINED = "PROCESS_UNDEFINED";

	ItemCollection documentContext;

	private String sEditorID;
	private String sType;
	private String sImageURL;
	private String sStatus;
	private String sGroup;
	private String sAbstract;
	private String sSummary;
	private static Logger logger = Logger.getLogger(ApplicationPlugin.class
			.getName());

	public int run(ItemCollection adocumentContext,
			ItemCollection adocumentActivity) throws PluginException {

		documentContext = adocumentContext;

		sEditorID = null;
		sImageURL = null;
		sAbstract = null;
		sSummary = null;

		// try to get next ProcessEntity
		// check if keyFollowUp <> '1'
		if (!"1".equals(adocumentActivity.getItemValueString("keyFollowUp"))) {

			// get numNextProcessID and modelVersion
			int iNextProcessID = adocumentActivity
					.getItemValueInteger("numNextProcessID");

			// now get the next ProcessEntity from ctx
			ItemCollection itemColNextProcess = null;
			// get from model version
			String aModelVersion = adocumentActivity
					.getItemValueString("$modelVersion");
			itemColNextProcess = ctx.getModel().getProcessEntity(
					iNextProcessID, aModelVersion);

			// if the processEntity was not found cancel processing now!
			if (itemColNextProcess == null) {
				logger.warning("[ApplicationPlugin] Warning - processEntity '"
						+ iNextProcessID + "' was not found in the model! ");
				return Plugin.PLUGIN_WARNING;
			}

			// fetch Editor and Image
			sEditorID = itemColNextProcess.getItemValueString("txtEditorID");
			sImageURL = itemColNextProcess.getItemValueString("txtImageURL");

			// fetch Status and Group
			sStatus = itemColNextProcess.getItemValueString("txtname");
			sGroup = itemColNextProcess.getItemValueString("txtworkflowgroup");
			sType = itemColNextProcess.getItemValueString("txttype");

			// fetch workflow Abstract
			sAbstract = itemColNextProcess
					.getItemValueString("txtworkflowabstract");
			if (!"".equals(sAbstract))
				sAbstract = this.replaceDynamicValues(sAbstract,
						documentContext);

			// fetch workflow Abstract
			sSummary = itemColNextProcess
					.getItemValueString("txtworkflowsummary");
			if (!"".equals(sSummary))
				sSummary = this.replaceDynamicValues(sSummary, documentContext);

			// submit data now into documentcontext
			// set Status and Group
			documentContext.replaceItemValue("txtWorkflowStatus", sStatus);
			documentContext.replaceItemValue("txtworkflowgroup", sGroup);

			// set Editor if value is defined
			if (sEditorID != null && !"".equals(sEditorID))
				documentContext.replaceItemValue("txtWorkflowEditorID",
						sEditorID);

			// set ImageURl if one is defined
			if (sImageURL != null && !"".equals(sImageURL))
				documentContext.replaceItemValue("txtWorkflowImageURL",
						sImageURL);

			// set Type if one is defined
			if (sType != null && !"".equals(sType))
				documentContext.replaceItemValue("type", sType);

			// set Abstract
			if (sAbstract != null)
				documentContext.replaceItemValue("txtworkflowabstract",
						sAbstract);

			// set Summary
			if (sSummary != null)
				documentContext
						.replaceItemValue("txtworkflowsummary", sSummary);

		}

		return Plugin.PLUGIN_OK;
	}

	public void close(int status) throws PluginException {
		// no action necessary
	}

}
