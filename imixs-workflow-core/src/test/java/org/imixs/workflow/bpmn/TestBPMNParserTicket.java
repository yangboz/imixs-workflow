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
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test class test the Imixs BPMNParser
 * 
 * @author rsoika
 */
public class TestBPMNParserTicket {

	
	@Before
	public void setup() {

	}

	@After
	public void teardown() {

	}

	// @Ignore
	@SuppressWarnings("rawtypes")
	@Test
	public void testSimple() throws ParseException,
			ParserConfigurationException, SAXException, IOException {

		String VERSION="1.0.0";
		
		InputStream inputStream = getClass().getResourceAsStream(
				"/bpmn/ticket.bpmn");

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
		Assert.assertEquals("environment.profile",
				profile.getItemValueString("txtname"));
		Assert.assertEquals("WorkflowEnvironmentEntity",
				profile.getItemValueString("type"));
		Assert.assertEquals(VERSION,
				profile.getItemValueString("$ModelVersion"));
		List plugins = profile.getItemValue("txtplugins");
		Assert.assertNotNull(plugins);
		Assert.assertEquals(4, plugins.size());
		Assert.assertEquals("org.imixs.workflow.plugins.AccessPlugin",plugins.get(0));
		Assert.assertEquals("org.imixs.workflow.plugins.OwnerPlugin",plugins.get(1));
		Assert.assertEquals("org.imixs.workflow.plugins.HistoryPlugin",plugins.get(2));
		Assert.assertEquals("org.imixs.workflow.plugins.ResultPlugin",plugins.get(3));

		Assert.assertTrue(model.workflowGroups.contains("Ticket"));
		
		// test count of elements
		Assert.assertEquals(4, model.getProcessEntityList(VERSION).size());

		// test task 1000
		ItemCollection task = model.getProcessEntity(1000,VERSION);
		Assert.assertNotNull(task);
		Assert.assertEquals("1.0.0",
				task.getItemValueString("$ModelVersion"));
		Assert.assertEquals("Ticket",
				task.getItemValueString("txtworkflowgroup"));
		
		Assert.assertEquals("<b>Create</b> a new ticket",
				task.getItemValueString("rtfdescription"));
		
		
		


		// test activity for task 1000 
		Collection<ItemCollection> activities = model
				.getActivityEntityList(1000,VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(1, activities.size());

		// test activity for task 1100
		activities = model.getActivityEntityList(1100,VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(3, activities.size());

		// test activity for task 1200
		activities = model.getActivityEntityList(1200,VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(4, activities.size());

		
		
		// test activity 1000.10 submit
		ItemCollection activity = model.getActivityEntity(1000, 10,VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("<b>Submitt</b> new ticket",
				activity.getItemValueString("rtfdescription"));
		
		
		// test activity 1100.20 accept
		activity = model.getActivityEntity(1100, 20,VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1200,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("accept", activity.getItemValueString("txtName"));

		// test activity 1200.20 - follow-up activity solve =>40
		activity = model.getActivityEntity(1200, 20,VERSION);
		Assert.assertNotNull(activity); 
		Assert.assertEquals("1.0.0",
				activity.getItemValueString("$ModelVersion"));

		Assert.assertEquals("reopen", activity.getItemValueString("txtName"));
		Assert.assertEquals("1", activity.getItemValueString("keyFollowUp"));
		Assert.assertEquals(40,
				activity.getItemValueInteger("numNextActivityID"));

		// test activity 1200.40 - follow-up activity message
		activity = model.getActivityEntity(1200, 40,VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("message", activity.getItemValueString("txtName"));
		Assert.assertEquals(1000,
				activity.getItemValueInteger("numNextProcessID"));

		// test activity 100.10
		activity = model.getActivityEntity(1000, 10,VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100,
				activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("submit", activity.getItemValueString("txtName"));
		// Test Owner
		Assert.assertTrue(activity.getItemValueBoolean("keyupdateacl"));

		List owners = activity.getItemValue("keyownershipfields");
		Assert.assertNotNull(owners);
		Assert.assertEquals(2, owners.size());
		Assert.assertTrue(owners.contains("namTeam"));
		Assert.assertTrue(owners.contains("namManager"));
	}

	@Ignore
	@Test(expected = ParseException.class)
	public void testCorrupted() throws ParseException,
			ParserConfigurationException, SAXException, IOException {

		InputStream inputStream = getClass().getResourceAsStream(
				"/bpmn/corrupted.bpmn");

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

		Assert.assertNull(model);
	}

}
