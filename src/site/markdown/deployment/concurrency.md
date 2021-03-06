#Concurrency and Optimistic Locking
The Imixs Workflow engine supports the optimistic locking mechanism supported by Java EE. Optimistic locking is based on the assumption that most transactions dont't conflict with other transactions, allowing concurrency to be as permissive as possible when  allowning transactions to execute. There for the Imixs Workflow engine holds an attribute '$version' providing the version  number of the corresponding entity. 
 
So when two users open the same workitem, change data and call the save() or the processWorkitem() method optimistic locking will be activated. This means that  an OptimisticLockException is thrown when the second user tries to save the workitem.
 
##Disabling optimistic locking  
There are two mechanisms to disable the optimistic locking. Both mechanisms guarantee that both users can save the workitem. The last call of the save() method wins. This behavior  is different to the default behavior as explained before.  To disable the build in optimistic locking mechanism you can either remove the $version  property from your workitem,
 
	...
	workitem.removeItem("$version");
	workitem=entityService.save(workitem);
    ....
 
or you can set the global property "DISABLE_OPTIMISTIC_LOCKING"  in the ejb-jar.xml deplyoment descriptor.
 
	...
	<session>
		<ejb-name>EntityService</ejb-name>
		<env-entry>
			<description>disable optimistic locking</description>
			<env-entry-name>DISABLE_OPTIMISTIC_LOCKING</env-entry-name>
			<env-entry-type>java.lang.Boolean</env-entry-type>
			<env-entry-value>true</env-entry-value>
		</env-entry>
	</session>
	...


