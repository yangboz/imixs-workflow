#The ResultPlugin

The Imixs-Workflow Result Plug-In is used to provide model based processing information. These information is provided by additional property values assigned to an Imixs BPMN Event. The plug-in evaluates these processing information which can be used by different modules. 

The Plug-in: 

    org.imixs.workflow.plugins.ResultPlugin

The Result Plug-in should run in the first place so that processing information can be provided to following plug-ins.

##The Item Tag

The processing information to be evaluated is stored in the extension property 'txtActivityResult' of an Imixs BPMN Event. The values are stored in the following XML format: 
 
    <item name="[NAME]">[VALUE]</item> 

See the following examples:
 
	<item name="txtName">Some Title</item> 
	<item name="numAccount" type="integer">500</item> 
	<item name="type">workitemarchive</item> 


This example will set the property 'txtName' to the value 'Some Title' and change the value 
 of the field 'numAccount' to 500. The value will be of type 'integer'. The last will change the type of the workitem to 'workitemarchive'.
 
<strong>Note:</strong> It is not possible to update any workflow processing properties beginning with an  '$' character in the item name. 

An Item value can also be evaluated by the tag 'itemValue' to assign a value form any existing processinstance. See the following example which computes the value of the property 'namCreator' to the item with the name 'responsible':
 
    <item name="responsible"><itemvalue>namCreator</itemvalue></item> 

 If the result message can not be evaluated, it will be stored into the attribute 
 "txtworkflowresultmessage". 
 

## Item Attributes
A item definiton can also contain optional attributes : 

    <item name="[NAME]" [OPTION]="[OPTION-VALUE]">[VALUE]</item> 

See the following example:
 
	<item name="comment" ignore="true">some data</item> 

The Result Plug-in provides the static method evaluateWorkflowResult() returning a ItemCollection with all item names and there attributes. In the example the ItemCollection will contain a item with the name 'comment' storing the value 'some data' and also a item value with the name 'comment.ignore' storing the value 'true'	
	
	
	ItemCollection result = ResultPlugin.evaluateWorkflowResult(activityEntity, workitem);
	Assert.assertNotNull(result);
	Assert.assertTrue(result.hasItem("comment"));
	Assert.assertEquals("some data", result.getItemValueString("comment"));
	Assert.assertEquals("true", result.getItemValueString("comment.ignore"));

## The Value Type

With the optional attribute 'type' the item value type can be specified. The following types are supported:

* boolean - results in type Boolean
* integer - results in type Integer