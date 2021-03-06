package org.imixs.workflow.bpmn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
 * Test class test the Imixs BPMNParser.
 * 
 * This test verifies the linking an imixs-event with an imixs-task using a
 * intermediate catch and intermediate throw link-event.
 * 
 * @author rsoika
 */
public class TestBPMNParserLinkEvent {

	@Before
	public void setup() {

	}

	@After
	public void teardown() {

	}

	/**
	 * This test test intermediate link events and also loop throw events
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testLinkEventSimple() throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/link-event.bpmn");

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

		Assert.assertTrue(model.workflowGroups.contains("Simple"));

		// test count of elements
		Assert.assertEquals(2, model.getProcessEntityList(VERSION).size());

		// test task 1000
		ItemCollection task = model.getProcessEntity(1000, VERSION);
		Assert.assertNotNull(task);
		Assert.assertEquals("1.0.0", task.getItemValueString("$ModelVersion"));
		Assert.assertEquals("Simple", task.getItemValueString("txtworkflowgroup"));

		// test activity for task 1000
		List<ItemCollection> activities = model.getActivityEntityList(1000, VERSION);
		Assert.assertEquals(3, activities.size());
		ItemCollection activity = model.getActivityEntity(1000, 10, VERSION);
		Assert.assertNotNull(activity);
 
		// test activity for task 1100
		activities = model.getActivityEntityList(1100, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(1, activities.size());
		

		/* Test confirm1 Event 1000.10 */
		activity = model.getActivityEntity(1000, 10, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("confirm1", activity.getItemValueString("txtName"));

		
		/* Test save Event 1100.10 */
		activity = model.getActivityEntity(1100,10, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("save", activity.getItemValueString("txtName"));

		
		
		
		
		/* Test Link Event 1000.20 */
		activity = model.getActivityEntity(1000, 20, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("confirm2", activity.getItemValueString("txtName"));
		
		
		
		/* Test Link Event 1000.30 */
		activity = model.getActivityEntity(1000, 30, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1000,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("update1", activity.getItemValueString("txtName"));

	}
	
	
	
	
	
	

	/**
	 * This test test intermediate link events with a follow up activity
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testLinkEventFollowup() throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/link-event_followup.bpmn");

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

		Assert.assertTrue(model.workflowGroups.contains("Simple"));

		// test count of elements
		Assert.assertEquals(2, model.getProcessEntityList(VERSION).size());

		// test task 1000
		ItemCollection task = model.getProcessEntity(1000, VERSION);
		Assert.assertNotNull(task);
		Assert.assertEquals("1.0.0", task.getItemValueString("$ModelVersion"));
		Assert.assertEquals("Simple", task.getItemValueString("txtworkflowgroup"));

		// test activity for task 1000
		List<ItemCollection> activities = model.getActivityEntityList(1000, VERSION);
		Assert.assertEquals(3, activities.size());
 
		
		/* Test confirm1 Event 1000.20 with follow up*/
		ItemCollection activity = model.getActivityEntity(1000, 20, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("1.0.0",
				activity.getItemValueString("$ModelVersion"));

		Assert.assertEquals("confirm2", activity.getItemValueString("txtName"));
		Assert.assertEquals("1", activity.getItemValueString("keyFollowUp"));
		Assert.assertEquals(99,
				activity.getItemValueInteger("numNextActivityID"));

		
	

		// test followup event
		activity = model.getActivityEntity(1000, 99, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("1.0.0",
				activity.getItemValueString("$ModelVersion"));

		Assert.assertEquals("followup1", activity.getItemValueString("txtName"));
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		
		
		
		// cross-test followup event
		activity = model.getActivityEntity(1100, 99, VERSION);
		Assert.assertNull(activity);
	}
	
	
	
	
	@Test
	public void testComplexParserTest() throws ParseException,
			ParserConfigurationException, SAXException, IOException {

		InputStream inputStream = getClass().getResourceAsStream(
				"/bpmn/link-event-complex.bpmn");

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
	}

}
