package org.imixs.workflow.plugins;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Test class for ResultPugin
 * 
 * @author rsoika
 */
public class TestResultPlugin {
	ResultPlugin resultPlugin = null;
	private static Logger logger = Logger.getLogger(TestResultPlugin.class.getName());

	@Before
	public void setup() throws PluginException {
		resultPlugin = new ResultPlugin();
		resultPlugin.init(null);
	}

	/**
	 * This test verifies the evaluation of a item tag
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testBasic() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name=\"txtName\">Manfred</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);
		int result = resultPlugin.run(adocumentContext, adocumentActivity);
		Assert.assertTrue(result == Plugin.PLUGIN_OK);
		Assert.assertEquals("Manfred", adocumentContext.getItemValueString("txtName"));

		// test with ' instead of "
		sResult = "<item name='txtName'>Manfred</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);
		result = resultPlugin.run(adocumentContext, adocumentActivity);
		Assert.assertTrue(result == Plugin.PLUGIN_OK);
		Assert.assertEquals("Manfred", adocumentContext.getItemValueString("txtName"));
	}

	@Test
	public void testBasicWithTypeBoolean() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='txtName' type='boolean'>true</item>";

		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = resultPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.getItemValueBoolean("txtName"));

	}

	@Test
	public void testBasicWithTypeInteger() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='numValue' type='integer'>47</item>";

		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = resultPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals(47, adocumentContext.getItemValueInteger("numValue"));

	}

	/**
	 * This test verifies if the 'type' property can be changed...
	 * 
	 * @throws PluginException
	 */
	@Test
	public void testTypeProperty() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='type' >workitemdeleted</item>";

		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = resultPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("workitemdeleted", adocumentContext.getItemValueString("Type"));

	}

	/**
	 * This test verifies if a pluginException is thronw if the format was
	 * invalid
	 * 
	 * @throws PluginException
	 */
	@SuppressWarnings("unused")
	@Test
	public void testInvalidFormatException() {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// wrong format
		String sResult = "<item name='txtName' >Anna<item>";

		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result;
		try {
			// exception expected
			result = resultPlugin.run(adocumentContext,adocumentActivity);

			Assert.fail();

		} catch (PluginException e) {
			logger.info(e.getMessage());
		}

		// wrong format missing "
		sResult = "<item name=\"txtName >Anna</itemxxxxx>";

		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		try {
			// exception expected
			result = resultPlugin.run(adocumentContext,adocumentActivity);

			Assert.fail();

		} catch (PluginException e) {
			logger.info(e.getMessage());
		}

	}

	/**
	 * This test verifies if multiple item tags with the same name will be added
	 * into one single property
	 * 
	 * @throws PluginException
	 */
	@Test
	public void testMultiValueEvaluation() throws PluginException {
		String sResult = "<item name=\"txtName\">Manfred</item>";
		sResult += "\n<item name=\"txtName\">Anna</item>";
		sResult += "\n<item name=\"test\">XXX</item>";
		sResult += "\n<item name=\"txtname\">Sam</item>";

		ItemCollection activityEntity = new ItemCollection();
			activityEntity.replaceItemValue("txtActivityResult",sResult);
			
		// expeced txtname= Manfred,Anna,Sam
		ItemCollection evalItemCollection = new ItemCollection();
		evalItemCollection=ResultPlugin.evaluateWorkflowResult(activityEntity,new ItemCollection());

		Assert.assertTrue(evalItemCollection.hasItem("txtName"));

		List<?> result = evalItemCollection.getItemValue("txtname");

		Assert.assertEquals(3, result.size());

		Assert.assertTrue(result.contains("Manfred"));
		Assert.assertTrue(result.contains("Sam"));
		Assert.assertTrue(result.contains("Anna"));

		// test test item
		Assert.assertEquals("XXX", evalItemCollection.getItemValueString("test"));
	}

	/**
	 * Test the itemParser method or activity
	 ***/
	@Test
	public void testItemXMLContent() {

		// create test result.....
		String activityResult = "<modelversion>1.0.0</modelversion>" + "<processid>1000</processid>"
				+ "<activityid>10</activityid>" + "<items>namTeam</items>";

		try {
			ItemCollection result = ResultPlugin.parseItemStructure(activityResult);

			Assert.assertEquals("1.0.0", result.getItemValueString("modelversion"));
			Assert.assertEquals("1000", result.getItemValueString("processID"));
			Assert.assertEquals("10", result.getItemValueString("activityID"));
		} catch (PluginException e) {

			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testevaluateWorkflowRestult() {
		ItemCollection activityEntity = new ItemCollection();

		try {
			activityEntity.replaceItemValue("txtActivityResult",
					"<item ignore=\"true\" name=\"comment\" >some data</item>");
			ItemCollection result = ResultPlugin.evaluateWorkflowResult(activityEntity, new ItemCollection());
			Assert.assertNotNull(result);
			Assert.assertTrue(result.hasItem("comment"));
			Assert.assertEquals("some data", result.getItemValueString("comment"));
			Assert.assertEquals("true", result.getItemValueString("comment.ignore"));
		} catch (PluginException e) {
			e.printStackTrace();
			Assert.fail();
		}

		// test an empty item tag
		try {
			activityEntity.replaceItemValue("txtActivityResult", "<item ignore=\"true\" name=\"comment\" />");
			ItemCollection result = ResultPlugin.evaluateWorkflowResult(activityEntity, new ItemCollection());
			Assert.assertNotNull(result);
			Assert.assertTrue(result.hasItem("comment"));
			Assert.assertEquals("", result.getItemValueString("comment"));
			Assert.assertEquals("true", result.getItemValueString("comment.ignore"));
		} catch (PluginException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	
	/**
	 * testing invalid item tag formats
	 */
	@Test
	public void testevaluateWorkflowRestultInvalidFormat() {
		ItemCollection activityEntity = new ItemCollection();

		try {
			// test no name attribute
			activityEntity.replaceItemValue("txtActivityResult",
					"<item ignore=\"true\" noname=\"comment\" >some data</item>");
			ResultPlugin.evaluateWorkflowResult(activityEntity, new ItemCollection());
			Assert.fail();
		} catch (PluginException e) {
			// ok
		}
		
		try {
			// test wrong closing tag
			activityEntity.replaceItemValue("txtActivityResult",
					"<item ignore=\"true\" name=\"comment\" >some data</xitem>");
			ResultPlugin.evaluateWorkflowResult(activityEntity, new ItemCollection());
			Assert.fail();
		} catch (PluginException e) {
			// ok
		}

		
	}

	/*
	 * Just for development
	 */
	@Ignore
	@Test
	public void manualTestRegex() {

		// pattern = <(item)(.*?)>(.*?)</item>
		Pattern pattern = Pattern.compile("<item(.*?)>(.*?)</item>|<item(.*?)./>");

		String yourString = "<dummy>nix</dummy> " + " <item ignore=\"true\" name=\"item1\" >content2</item> "
				+ " <item name=\"item2\">content2</item> <item>dummy</item> "
				+ " <item name=\"empty\" ignore=\"true\" />";
		Matcher matcher = pattern.matcher(yourString);
		while (matcher.find()) {
			System.out.println("Tag Only   : " + matcher.group(0));
			System.out.println("Attributes : " + matcher.group(1));
			System.out.println("Content    : " + matcher.group(2));
			System.out.println("Content2    : " + matcher.group(3));
		}
	}

	@Ignore
	@Test
	public void manualTestAttributesRegex() {
		String spattern = "(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?";
		Pattern attributePattern = Pattern.compile(spattern);
		Matcher attributeMatcher = attributePattern.matcher(" ignore=\"true\" name=\"comment\"");
		while (attributeMatcher.find()) {
			System.out.println(attributeMatcher.group(0));
			System.out.println(attributeMatcher.group(1));
			System.out.println(attributeMatcher.group(2));
		}

	}

}
