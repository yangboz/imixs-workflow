package org.imixs.workflow.plugins;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import junit.framework.Assert;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.plugins.RulePlugin;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for RulePlugin
 * 
 * @author rsoika
 */
public class TestRulePlugin {
	RulePlugin rulePlugin = null;

	@Before
	public void setup() throws PluginException {
		rulePlugin = new RulePlugin();
		rulePlugin.init(null);
	}

	/**
	 * This test verifies the evaluation of a simple script.
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testBasicScript() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		ItemCollection adocumentActivity = new ItemCollection();

		// set a business rule
		String script = "var a=1;var b=2;var test = ((a<b) && 'Anna'==txtname[0]);";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("Anna",
				adocumentContext.getItemValueString("txtName"));

	}

	/**
	 * This test verifies the isValid cases
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testIsValid() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		ItemCollection adocumentActivity = new ItemCollection();

		// 1) test without any script:
		adocumentActivity.replaceItemValue("txtBusinessRUle", null);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// 2) test with an empty script:
		adocumentActivity.replaceItemValue("txtBusinessRUle", "");
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// 3) test script without isValid variable
		String script = "var a=1;var b=2;";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// 2) test true case
		script = "var a=1;var b=2;var isValid = ((a<b) && 'Anna'==txtname[0]);";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// 2) test false case
		script = "var a=1;var b=2;var isValid = ((a>b) && 'Anna'==txtname[0]);";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertFalse(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

	}

	/**
	 * This test verifies if in case of isValid==false a PluginExeption is
	 * thrown
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test(expected = PluginException.class)
	public void testSimplePluginException() throws ScriptException,
			PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// set a business rule
		String script = "var a=1;var b=2;var isValid = (a>b);";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		rulePlugin.run(adocumentContext, adocumentActivity);
		Assert.fail();

	}

	/**
	 * This test verifies if in case of isValid==false a PluginExeption is
	 * thrown and evalues the data contained in the Exception. There for the
	 * script adds an errorCode and a errorMessage
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testComplexPluginException() throws ScriptException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// 1) invalid returning one messsage
		String script = "var a=1;var b=2;var isValid = (a>b);"
				+ " var errorCode='MY_ERROR';"
				+ " var errorMessage='Somehing go wrong!';";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		try {
			rulePlugin.run(adocumentContext, adocumentActivity);
			Assert.fail();
		} catch (PluginException e) {
			// test excption
			Assert.assertEquals("MY_ERROR", e.getErrorCode());
			Object[] params = e.getErrorParameters();
			Assert.assertEquals(1, params.length);
			Assert.assertEquals("Somehing go wrong!", params[0].toString());
		}

		// 2) invalid returning 2 messages in an array
		script = "var a=1;var b=2;var isValid = (a>b);"
				+ " var errorMessage = new Array();"
				+ " errorMessage[0]='Somehing go wrong!';"
				+ " errorMessage[1]='Somehingelse go wrong!';";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		try {
			rulePlugin.run(adocumentContext, adocumentActivity);
			Assert.fail();
		} catch (PluginException e) {
			// e.printStackTrace();
			// test exception
			Assert.assertEquals(RulePlugin.VALIDATION_ERROR, e.getErrorCode());
			Object[] params = e.getErrorParameters();
			Assert.assertEquals(2, params.length);
			Assert.assertEquals("Somehing go wrong!", params[0].toString());
			Assert.assertEquals("Somehingelse go wrong!", params[1].toString());
		}

	}

	/**
	 * This test verifies the follUp behavior. If set then keyFollowUp and
	 * numNextActivity should be overwritten by the RulePlugin
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testFollowUpActivity() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// set a business rule
		String script = "var a=1.0;var b=2;var followUp =a+b;";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		String sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		int followUp = adocumentActivity
				.getItemValueInteger("numNextActivityID");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("1", sFllowUp);

		Assert.assertEquals(followUp, 3);

	}
	
	
	
	
	
	/**
	 * This test verifies the nextTask behavior. If set then keyFollowUp and
	 * numnextprocessid should be overwritten by the RulePlugin
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testNextTask() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// set a business rule
		String script = "var a=1.0;var b=2;var nextTask =a+b;";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		String sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		int followUp = adocumentActivity
				.getItemValueInteger("numNextActivityID");
		
		
		int nextTask = adocumentActivity
				.getItemValueInteger("numNextprocessiD");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(!"1".equals(sFllowUp));

		Assert.assertEquals(followUp, 0);
		Assert.assertEquals(3, nextTask);

	}

	
	
	/**
	 * This test verifies a combination of nextActivity and nextTask behavior.
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testFollowUpAndNextTask() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		// set a business rule
		String script = "var a=1.0;var b=2;var nextTask =a+b;var followUp =a+b;";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

	
		
		int nextTask = adocumentActivity
				.getItemValueInteger("numNextprocessiD");
		
		
		String sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		int followUp = adocumentActivity
				.getItemValueInteger("numNextActivityID");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("1", sFllowUp);

		Assert.assertEquals(followUp, 3);

		Assert.assertEquals(3, nextTask);

	}

	
	
	
	
	
	
	/**
	 * This Test tests if we can evaluate any value provided in the script
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testValueEvaluation() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		ItemCollection adocumentActivity = new ItemCollection();

		adocumentContext.replaceItemValue("txtName", "Anna");
		adocumentContext.replaceItemValue("type", "workitem");

		// set a business rule
		String script = "if ('Anna'==txtname[0]) txtname[0]='Manfred';"
				+ "var someData='Eddy';";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		ScriptEngine engine = rulePlugin.evaluateBusinessRule(adocumentContext,
				adocumentActivity);

		Object[] result = rulePlugin.evaluateScriptObject(engine, "txtname");
		if (result != null) {
			Assert.assertEquals("Manfred", result[0]);
		} else
			Assert.fail();

		result = rulePlugin.evaluateScriptObject(engine, "someData");
		if (result != null) {
			Assert.assertEquals("Eddy", result[0]);
		} else
			Assert.fail();
	}

	/**
	 * only to evaluate some behavior
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void simpleApprovalTest() throws ScriptException, PluginException {

		// set a business rule
		String script = " var followUp=null;" + " if (_amount_brutto[0]>5000)"
				+ "    followUp=90;";
		System.out.println("Script=" + script);

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("_amount_brutto", new Double(6000));
		ItemCollection adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		String sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		int followUp = adocumentActivity
				.getItemValueInteger("numNextActivityID");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("1", sFllowUp);

		Assert.assertEquals(90, followUp);

		/*
		 * Case 2
		 */

		adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("_amount_brutto", new Double(3000));
		adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		result = rulePlugin.run(adocumentContext, adocumentActivity);

		sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		followUp = adocumentActivity.getItemValueInteger("numNextActivityID");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("", sFllowUp);

		Assert.assertEquals(0, followUp);

	}

	/**
	 * This test verifies the BigDecimal support of the RulePlugin
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void bigDecimalTest() throws ScriptException, PluginException {

		// set a business rule
		String script = " var followUp=null;"
				+ " if (_amount_brutto[0]>5000.50)" + "    followUp=90;";
		System.out.println("Script=" + script);

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("_amount_brutto", new BigDecimal(
				5000.51));
		ItemCollection adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		String sFllowUp = adocumentActivity.getItemValueString("keyFollowUp");
		int followUp = adocumentActivity
				.getItemValueInteger("numNextActivityID");

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertEquals("1", sFllowUp);

		Assert.assertEquals(90, followUp);

	}

	/**
	 * See: http://www.rgagnon.com/javadetails/java-0640.html
	 */
	@Test
	public void testArray() {

		ScriptEngineManager mgr = new ScriptEngineManager();
		// we are using the rhino javascript engine
		ScriptEngine engine = mgr.getEngineByName("javascript");

		// pass a Java collection to javascript
		List<String> list1 = Arrays.asList("Homer", "Bart", "Marge", "Maggie",
				"Lisa");
		engine.put("list1", list1);
		

		// Nashorn: check for importClass function and then load if missing ...
		String jsNashorn=" if (typeof importClass != 'function') { load('nashorn:mozilla_compat.js');}";
	
		
		String jsCode = "var index; " + "var values =list1.toArray();"
				+ "print('*** Java object to Javascript');"
				+ "for(index in values) {" + "  print(values[index]);" + "}";
		try {
			engine.eval(jsNashorn+jsCode);
		} catch (ScriptException se) {
			se.printStackTrace();
		}

		
		
		// pass a collection from javascript to java
		jsCode =  "importPackage(java.util);"
				+ "var list2 = Arrays.asList(['Moe', 'Barney', 'Ned']); ";
		try {
			engine.eval(jsNashorn+jsCode);
		} catch (ScriptException se) {
			se.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		List<String> list2 = (List<String>) engine.get("list2");
		System.out.println("*** Javascript object to Java");
		for (String val : list2) {
			System.out.println(val);
		}

	}

	/**
	 * This test test if a the properties of an activity entity can be evaluated
	 * by a script
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testSimpleActivityScript() throws ScriptException,
			PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");

		// simulate an activity
		ItemCollection adocumentActivity = new ItemCollection();
		adocumentActivity.replaceItemValue("keyMailEnabled", "1");

		// set a business rule
		String script = "var isValid =  '1'==activity.get('keymailenabled')[0];";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

	}

	/**
	 * This test test if a the properties of an workitem entity can be evaluated
	 * by a script
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testSimpleWorkitemScript() throws ScriptException,
			PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		adocumentContext.replaceItemValue("$ProcessID", 1000);
		// simulate an activity
		ItemCollection adocumentActivity = new ItemCollection();
		adocumentActivity.replaceItemValue("keyMailEnabled", "1");

		// set a business rule
		String script = "var isValid =  1000==workitem.get('$processid')[0];";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

	}

	/**
	 * This test test if a the properties of an workitem entity can be evaluated
	 * by a script. In addition the test verifies if the workitem itself can be
	 * manipulated by the script. This may not happen!
	 * 
	 * But manipulation for an Activity should be possible!
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testComplexWorkitemScript() throws ScriptException,
			PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		adocumentContext.replaceItemValue("$ProcessID", 1000);
		// simulate an activity
		ItemCollection adocumentActivity = new ItemCollection();
		adocumentActivity.replaceItemValue("keyMailEnabled", "1");

		// set a business rule
		String script = "";
		script += "var isValid =  1000==workitem.get('$processid')[0];";
		// now add a manipulation!
		script += " workitem.put('$processid',[99]);";
		// add maniputlation to itemCollection
		script += " activity.put('keymailenabled',['0']);";

		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);

		int result = rulePlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		Assert.assertEquals(1000,
				adocumentContext.getItemValueInteger("$ProcessID"));

		// test manipulation of activity
		Assert.assertEquals("0",
				adocumentActivity.getItemValueString("keyMailEnabled"));
		// test for integer value
		Assert.assertEquals(0,
				adocumentActivity.getItemValueInteger("keyMailEnabled"));

	}

	/**
	 * This test verifies the isValid case for date fields
	 * 
	 * @throws ScriptException
	 * @throws PluginException
	 */
	@Test
	public void testIsValidDate() throws ScriptException, PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("datDate", new Date());
		ItemCollection adocumentActivity = new ItemCollection();

		// 2) test true case
		String script = " var refField1=\"_contact\";"
				+ " var refField2=\"datdate\";"
				+ " var isValid=true;"
				+ " if (   ( workitem.get(refField2) == null)   ) {"
				+ "     isValid=false;"
				+ "     var errorMessage='Bitte geben Sie ein Datum fuer das Zahlungsziel an!';"
				+ " }  ";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// 2) test false case
		adocumentContext = new ItemCollection();
		adocumentActivity = new ItemCollection();
		script = " var refField1=\"_contact\";"
				+ " var refField2=\"datdate\";"
				+ " var isValid=true;"
				+ " if (   ( workitem.get(refField2) == null)   ) {"
				+ "     isValid=false;"
				+ "     var errorMessage='Bitte geben Sie ein Datum fuer das Zahlungsziel an!';"
				+ " }  ";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertFalse(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		// calendar test
		adocumentContext = new ItemCollection();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		adocumentContext.replaceItemValue("datDate", cal);
		adocumentActivity = new ItemCollection();

		// 2a) test true case
		script = " var refField1=\"_contact\";"
				+ " var refField2=\"datdate\";"
				+ " var isValid=true;"
				+ " if (   ( workitem.get(refField2) == null)   ) {"
				+ "     isValid=false;"
				+ "     var errorMessage='Bitte geben Sie ein Datum fuer das Zahlungsziel an!';"
				+ " }  ";
		System.out.println("Script=" + script);
		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

	}

	/**
	 * 
	 * <code>
	 *   var refField1="txtbetrag"; 
		 var refField2="txtgutschift"; 
		 var isValid=true;
		 if ( ( workitem.get(refField1) == null || ''==workitem.get(refField1)[0]) || ( workitem.get(refField2) == null || ''==workitem.get(refField2)[0])) {
		     isValid=false;
		     var errorMessage='Bitte geben Sie den Betrag an! ';
		  }
	 * </code>
	 */
	@Test
	public void testIsValidTwoFields() throws ScriptException, PluginException {
		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtbetrag", "5,55");
		adocumentContext.replaceItemValue("txtgutschift", "5,55");
		ItemCollection adocumentActivity = new ItemCollection();

		// 2) test true case
		String script = " var refField1=\"txtbetrag\";"
				+ "		 var refField2=\"txtgutschift\";"
				+ "		 var isValid=true;"
				+ "		 if ( ( workitem.get(refField1) == null || ''==workitem.get(refField1)[0]) || ( workitem.get(refField2) == null || ''==workitem.get(refField2)[0])) {"
				+ "		     isValid=false;"
				+ "		     var errorMessage='Bitte geben Sie den Betrag an! ';"
				+ "		  } ";

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtbetrag", "");
		adocumentContext.replaceItemValue("txtgutschift", "5,55");
		adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertFalse(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtbetrag", "55");
		adocumentContext.replaceItemValue("txtgutschift", "");
		adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertFalse(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtbetrag", "");
		adocumentContext.replaceItemValue("txtgutschift", "");
		adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertFalse(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

		adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtbetrag", "1,5");
		adocumentContext.replaceItemValue("txtgutschift", "4");
		adocumentActivity = new ItemCollection();

		adocumentActivity.replaceItemValue("txtBusinessRUle", script);
		Assert.assertTrue(rulePlugin.isValid(adocumentContext,
				adocumentActivity));

	}

	/**
	 * following script should not throw an exception because of the fact that
	 * the errorCode is undefined.
	 * 
	 * @see issue #108
	 * 
	 */
	@Test
	public void testUndefinedErrorCode() throws ScriptException,
			PluginException {
		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("_subject", "test");
		ItemCollection adocumentActivity = new ItemCollection();

		System.out.println("testUndefineErroCode - test case 1:");
		// 2) test undefined case
		String script = " var isValid=true;"
				+ " var errorCode,errorMessage;"
				+ "refField='_contact';"
				+ "if ( workitem.get(refField) == null || workitem.get(refField)[0] == ''  ) {"
				+ "     isValid=false;"
				+ "     errorMessage='Please enter subject';" + " }";

		adocumentActivity.replaceItemValue("txtBusinessRule", script);

		try {
			rulePlugin.run(adocumentContext, adocumentActivity);

			Assert.fail();
		} catch (PluginException pe) {
			// PluginException expected
			System.out.println(pe.getMessage());
			Assert.assertEquals(pe.getErrorCode(), RulePlugin.VALIDATION_ERROR);

			Object[] errorParams = pe.getErrorParameters();

			Assert.assertEquals(1, errorParams.length);
			Assert.assertEquals("Please enter subject", errorParams[0]);

		}

		System.out.println("testUndefineErroCode - test case 2:");
		
		// test the same case if errorCode is defined
		// 2) test true case
		script = " var isValid=true;"
				+ " var errorCode,errorMessage;"
				+ "refField='_contact';"
				+ "if ( workitem.get(refField) == null || workitem.get(refField)[0] == ''  ) {"
				+ "     isValid=false;" + "     errorCode='SOME_ERROR';" + " }";

		adocumentActivity.replaceItemValue("txtBusinessRule", script);

		try {
			rulePlugin.run(adocumentContext, adocumentActivity);

			Assert.fail();
		} catch (PluginException pe) {
			// PluginException expected
			System.out.println(pe.getMessage());
			Assert.assertEquals(pe.getErrorCode(), "SOME_ERROR");
			Object[] errorParams = pe.getErrorParameters();
			Assert.assertTrue(errorParams==null);

		}

	}

}
