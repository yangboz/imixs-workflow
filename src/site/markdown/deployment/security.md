# Security Configuration
Each method call to the Imixs Workflow System have to possess an applicable authentication process to grant the demands of the Imixs-Workflow technology according security. In the security concept of the workflow there are 5 default roles defined:

  * org.imixs.ACCESSLEVEL.NOACCESS  
  * org.imixs.ACCESSLEVEL.READACCESS
  * org.imixs.ACCESSLEVEL.AUTHORACCESS
  * org.imixs.ACCESSLEVEL.EDITORACCESS
  * org.imixs.ACCESSLEVEL.MANAGERACCESS

Each user accessing the Imixs-Workflow engine should be mapped to one of these roles. The user roles can be mapped in an application to corresponding groups based on a authentication realm.
You will find more information about the general ACL concept of the Imixs-Workflow engine in the [section ACL](../engine/acl.html). 

See the following section for details

## GlassFish
The following section holds deployment strategies for the GlassFish platform.
The security roles defined by the Imixs-Workflow Engine need to be mapped in an application to corresponding groups defined by a authentication realm.

The following example shows the glassfish-web.xml deployment descriptor for GlassFish Server which maps these roles to corresponding groups:
 
	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE glassfish-web-app PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN" "http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd">
	<glassfish-web-app>
		<context-root>/imixs-workflow</context-root>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.NOACCESS</role-name>
			<group-name>Noaccess</group-name>
			<group-name>IMIXS-WORKFLOW-Noaccess</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.READERACCESS</role-name>
			<group-name>Reader</group-name>
			<group-name>IMIXS-WORKFLOW-Reader</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.AUTHORACCESS</role-name>
			<group-name>Author</group-name>
			<group-name>IMIXS-WORKFLOW-Author</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.EDITORACCESS</role-name>
			<group-name>Editor</group-name>
			<group-name>IMIXS-WORKFLOW-Editor</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.MANAGERACCESS</role-name>
			<group-name>Manager</group-name>
			<group-name>IMIXS-WORKFLOW-Manager</group-name>
		</security-role-mapping>
	</glassfish-web-app>


This mapping can also be done in the glassfish-ejb-jar.xml inside an EJB module or the glassfish-application.xml for a EAR deployment. See the following example shows an glassfish-application.xml with a corresponding roles mapping for the realm 'imixsrealm':

	 <?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE glassfish-application PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Java EE Application 6.0//EN" "http://glassfish.org/dtds/glassfish-application_6_0-1.dtd">
	<glassfish-application>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.NOACCESS</role-name>
			<group-name>IMIXS-WORKFLOW-Noaccess</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.READERACCESS</role-name>
			<group-name>IMIXS-WORKFLOW-Reader</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.AUTHORACCESS</role-name>
			<group-name>IMIXS-WORKFLOW-Author</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.EDITORACCESS</role-name>
			<group-name>IMIXS-WORKFLOW-Editor</group-name>
		</security-role-mapping>
		<security-role-mapping>
			<role-name>org.imixs.ACCESSLEVEL.MANAGERACCESS</role-name>
			<group-name>IMIXS-WORKFLOW-Manager</group-name>
			<principal-name>IMIXS-WORKFLOW-Service</principal-name>
		</security-role-mapping>	
		<realm>imixsrealm</realm>
	</glassfish-application>


### security-role-ref 
To map these roles in a web application directly without the deployment descriptors above use  the security-role-ref in the web.xml and ejb-jar.xml as defined in the JEE specification.

	  ...  
	   <security-role-ref>
	    <role-name>author</role-name>
	    <role-link>org.imixs.ACCESSLEVEL.AUTHORACCESS</role-link>
	  </security-role-ref>
	...

In this example the name in the tag "role-link" must match the name of the imixs security role and the role-name to a group or role in your security context.


The deployment is similar to other application servers. 
  
##Defining application specific access roles
In addition to the standard security model of the Imixs Workflow System it is also possible to define application  specific roles. These roles can be used in a Workflow application to restrict the access in a more fine grained way. An application specific role is typical mapped to a workitem by using the [Imixs-BPMN Modeler](../modelling/index.html).  You can add such a Role to the Read or Write Access configuration of an Activity Entity.  The Workflow Manager will map the application specific role later directly into a workitem.

<strong>Note:</strong> Users must have at least the general AccessRole 
org.imixs.ACCESSLEVEL.READACCESS to access a workitem. Also if you define application specific roles. Otherwise a uses is not allowed to access the workitems with an application specific role restriction in the WorkfloManager.
 
 
###Example
For example you may add an additional application specific role named "PROJECTMANAGER" to your workflow application. You can restrict the access (read and write access) to that new role for all workitems in a specific workflow status by defining this role in your model.
To use such an application specific role like "PROJECTMANAGER" you need to define this role in the ejb deployment  descriptor (ejb-jar.xml) and map this role also to a UserGroup (sun-ejb-jar.xml) as described above. The following code example illustrates how to define an application specific role in the ejb-jar.xml deployment descriptor:

	  <enterprise-beans>
	 .....
	 <session>
		<ejb-name>EntityService</ejb-name>
		<env-entry>
			<description>Additional Access Rolls</description>
			<env-entry-name>ACCESS_ROLES</env-entry-name>
			<env-entry-type>java.lang.String</env-entry-type>
			<env-entry-value>com.prosamed.ISESS.ARTIKELMODIFIER</env-entry-value>
		</env-entry>
		<security-role-ref>
			<role-name>PROJECTMANAGER</role-name>
			<role-link>PROJECTMANAGER</role-link>
		</security-role-ref>
	 </session>
	 ...
	 .....
	  <assembly-descriptor>
			<security-role>
				<role-name>PROJECTMANAGER</role-name>
			</security-role>
	  </assembly-descriptor>


To link the new Role to the Imixs Workflow components it is adequate to define the role to the  EntiyServiceBean. There you need to add two additional entries. The env-entry defines the new role to be used by the access control of the EntityServiceBean. The security-role-ref adds the role to the EJB. You need to specify the role-name and also the role-link
for an additional role. If you need to access those role definitions also from other EJBs like the WorkflowService ore from a WorkflowPlugins, you need to add the additional security-role-ref tags also to the corresponding ejb definitions.

##Working with dynamic user groups
Imixs Workflow also allows the mapping of user groups from an application. Through this mechanism it is possible to inject user groups into the  security layer from the EntityService.  For example an application can lookup dynamic created groups from a ldap directory or a  database and inject this groups into the security mechanism from Imixs Workflow. The EntityService EJB test if dynamic user groups are provided by testing the EJB ContextData named "org.imixs.USER.GROUPLIST". The expected data is a  String array containing the users group names.   
 
The typically implementation of this mechanism is done by an interceptor Class attached to the EntityService. This is an example of a simple interceptor:
 
	 public class UserGroupInterceptor {
		@Resource
		SessionContext ejbCtx;
	
		@AroundInvoke
		public Object intercept(InvocationContext ctx) throws Exception {
			ctx.getContextData().put(EntityService.USER_GROUP_LIST, "MyGroup");
			return ctx.proceed();
		}
	 }
 
The intercepter can be configured using the ejb-jar.xml deployment descriptor

	 ....
		<!-- adding interceptor -->
		<interceptors>
			<interceptor>
				<interceptor-class>org.imixs.business.ejb.UserGroupInterceptor</interceptor-class>
			</interceptor>
		</interceptors>
	
		<assembly-descriptor>
			<interceptor-binding>
				<description>Intercepter to add project-role mapping into EJB Context Data</description>
				<ejb-name>EntityService</ejb-name>
				<interceptor-class>org.imixs.business.ejb.UserGroupInterceptor</interceptor-class>
			</interceptor-binding>
		</assembly-descriptor>
	 ...
 