package org.imixs.workflow.bpmn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.exceptions.ModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test class test the Imixs BPMNParser
 * 
 * Special cases with collaboration diagrams
 * 
 * @author rsoika
 */
public class TestBPMNParserCollaboration {

	@Before
	public void setup() {

	}

	@After
	public void teardown() {

	}

	// @Ignore
	@Test
	public void testSimple() throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/collaboration.bpmn");

		BPMNModel model = null;
		try {
			model = BPMNParser.parseModel(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ModelException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertNotNull(model);

		// Test Environment
		ItemCollection profile = model.getProfile();
		Assert.assertNotNull(profile);
		Assert.assertEquals("environment.profile", profile.getItemValueString("txtname"));
		Assert.assertEquals("WorkflowEnvironmentEntity", profile.getItemValueString("type"));
		Assert.assertEquals(VERSION, profile.getItemValueString("$ModelVersion"));

		List<String> groups = model.workflowGroups;
		// Test Groups
		Assert.assertFalse(groups.contains("Collaboration"));
		Assert.assertTrue(groups.contains("WorkflowGroup1"));
		Assert.assertTrue(groups.contains("WorkflowGroup2"));

		// test count of elements
		Assert.assertEquals(2, model.getProcessEntityList(VERSION).size());

		// test task 1000
		ItemCollection task = model.getProcessEntity(1000, VERSION);
		Assert.assertNotNull(task);
		Assert.assertEquals("1.0.0", task.getItemValueString("$ModelVersion"));
		Assert.assertEquals("WorkflowGroup1", task.getItemValueString("txtworkflowgroup"));

		// test activity for task 1000
		Collection<ItemCollection> activities = model.getActivityEntityList(1000, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(1, activities.size());

		// test activity 1000.10 submit
		ItemCollection activity = model.getActivityEntity(1000, 10, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("submit", activity.getItemValueString("txtname"));
		Assert.assertEquals(1100, activity.getItemValueInteger("numNextProcessID"));

		// test task 1100
		task = model.getProcessEntity(1100, VERSION);
		Assert.assertNotNull(task);
		Assert.assertEquals("1.0.0", task.getItemValueString("$ModelVersion"));
		Assert.assertEquals("WorkflowGroup2", task.getItemValueString("txtworkflowgroup"));

	}

}
