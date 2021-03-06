package org.imixs.workflow.bpmn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.exceptions.ModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import junit.framework.Assert;

/**
 * Test class test the Imixs BPMNParser in case of shared events (one event used
 * by two different task elements)
 * 
 * @author rsoika
 */
public class TestBPMNParserSharedEvent {

	@Before
	public void setup() {

	}

	@After
	public void teardown() {

	}

	/**
	 * Simple shared event
	 * 
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testSharedEvent() throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/shared_event1.bpmn");

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

		// test task 1000
		ItemCollection task = model.getProcessEntity(1000, VERSION);
		Assert.assertNotNull(task);

		// test activity for task 1000
		List<ItemCollection> activities = model.getActivityEntityList(1000, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(2, activities.size());

		// test activity for task 1100
		activities = model.getActivityEntityList(1100, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(1, activities.size());

		// test activity 1000.10 submit
		ItemCollection activity = model.getActivityEntity(1000, 10, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1100, activity.getItemValueInteger("numNextProcessID"));

		// now test shared activity...

		// test activity 1100.90 archive
		activity = model.getActivityEntity(1100, 90, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1200, activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("archive", activity.getItemValueString("txtname"));

		// test activity 1000.90 archive
		activity = model.getActivityEntity(1000, 90, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(1200, activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("archive", activity.getItemValueString("txtname"));

	}

	/**
	 * Shared event with an follow up event
	 * 
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testSharedEventWithFollowUp()
			throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/shared_event2.bpmn");

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

		// test task 2000
		ItemCollection task = model.getProcessEntity(2000, VERSION);
		Assert.assertNotNull(task);
		List<ItemCollection> activities = model.getActivityEntityList(2000, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(3, activities.size());

		// test activity for task 2100
		activities = model.getActivityEntityList(2100, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(2, activities.size());

		// test activity 2000.10 submit
		ItemCollection activity = model.getActivityEntity(2000, 10, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(2100, activity.getItemValueInteger("numNextProcessID"));

		// now test shared activity...

		// test activity 2000.80 archive
		activity = model.getActivityEntity(2000, 80, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("1", activity.getItemValueString("keyFollowUp"));
		Assert.assertEquals(90, activity.getItemValueInteger("numNextActivityID"));
		Assert.assertEquals("archive", activity.getItemValueString("txtname"));

		// test activity 2000.90 archive
		activity = model.getActivityEntity(2000, 90, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals(2200, activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("followup", activity.getItemValueString("txtname"));

	}

	/**
	 * Shared event with an follow up event
	 * 
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testSharedLinkedEventWithFollowUp()
			throws ParseException, ParserConfigurationException, SAXException, IOException {

		String VERSION = "1.0.0";

		InputStream inputStream = getClass().getResourceAsStream("/bpmn/shared_event3.bpmn");

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

		// test task 3000
		ItemCollection task = model.getProcessEntity(3000, VERSION);
		Assert.assertNotNull(task);
		List<ItemCollection> activities = model.getActivityEntityList(3000, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(3, activities.size());

		// test task 3100
		task = model.getProcessEntity(3100, VERSION);
		Assert.assertNotNull(task);
		activities = model.getActivityEntityList(3100, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(2, activities.size());

		// test task 3200
		task = model.getProcessEntity(3200, VERSION);
		Assert.assertNotNull(task);
		activities = model.getActivityEntityList(3200, VERSION);
		Assert.assertNotNull(activities);
		Assert.assertEquals(0, activities.size());

		// test follow up 3000.20
		ItemCollection activity = model.getActivityEntity(3000, 20, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("1", activity.getItemValueString("keyFollowUp"));
		Assert.assertEquals(30, activity.getItemValueInteger("numNextActivityID"));
		Assert.assertEquals("archive", activity.getItemValueString("txtname"));

		// test follow up 3100.20
		activity = model.getActivityEntity(3100, 20, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertEquals("1", activity.getItemValueString("keyFollowUp"));
		Assert.assertEquals(30, activity.getItemValueInteger("numNextActivityID"));
		Assert.assertEquals("archive", activity.getItemValueString("txtname"));

		// test follow up 3000.30
		activity = model.getActivityEntity(3000, 30, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertFalse("1".equals(activity.getItemValueString("keyFollowUp")));
		Assert.assertEquals(3200, activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("followup", activity.getItemValueString("txtname"));
		Assert.assertEquals(3000, activity.getItemValueInteger("numProcessID"));

		// test follow up 3100.30
		activity = model.getActivityEntity(3100, 30, VERSION);
		Assert.assertNotNull(activity);
		Assert.assertFalse("1".equals(activity.getItemValueString("keyFollowUp")));
		Assert.assertEquals(3200, activity.getItemValueInteger("numNextProcessID"));
		Assert.assertEquals("followup", activity.getItemValueString("txtname"));
		Assert.assertEquals(3100, activity.getItemValueInteger("numProcessID"));

	}

}
