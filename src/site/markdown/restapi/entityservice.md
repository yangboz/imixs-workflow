#The Entity Service
 The main resource /entity is uses to read entities and collections through  the Imixs Rest Service Interface.
 
 
## The /entity GET resources
The /entity resources are used to get direct access to the entity service EJB:

| URI                                     | Description                               | 
|-----------------------------------------|-------------------------------------------|
| /entity/query/{query}          | a collection of entities specified by a JPQL phrase    |
| /entity/count/query/{query}    | the count of entities returned by a JPQL phrase      |

 
## Resource Options
You can specify additional URI paramters to filter the resultset  or to navigate through a sub list of entities. Append optional arguments to define the number of entities returned by a URL, the starting point inside the list or the sort order. Combine any of the following arguments for the desired result. 

| option      | description                                         | example               |
|-------------|-----------------------------------------------------|-----------------------|
| count       | number of workitems returned by a collection        | ..?count=10           |
| start       | position to start  workitems returned by a collection         | ..?start=5&count=10   |

 
<strong>Note:</strong> Imixs-Workflow controls the access for each entity by individual access lists.  The result of a collection of entities contains only entities which are not read protected   or the current user has sufficien read access. 
        

##Administrative resource URIs

The Entity Rest Service provides resource URIs for administrative purpose. To access these resources, the caller  must at least be in role "org.imixs.ACCESSLEVEL.MANAGERACCESS". These administrative URIs should not be used in  general business logic.  For more information about the Entity Service see the [EntityService interface description](../engine/entityservice.htm).
 
| METHOD |URI                           | Description                                                                               | 
|--------|------------------------------|-------------------------------------------------------------------------------------------|
| GET    | /entity/indexlist            | Returns the list of existing Imixs-Entity-Index entries in XML or JSON format             |
| PUT    | /entity/index/{name}/{type}  | Adds an Imixs-Entity-Index for properties provided by ItemCollection objects. An Imixs-Entity-Index can be used to select ItemCollections using a JPQL statement. @see findEntitesByQuery  |
| DELETE | /entity/index/{name}         | Removes an existing Imixs-Entity-Index for a property provided by ItemCollection objects. | 
| POST   | /entity                      | Post an entity to be stored by the EntityService in the database. NOTE: The content of the entity will be merged into an existing instance.     |
| DELETE | /entity/{name}               | Removes an existing Imixs-Entity-Index for a property provided by ItemCollection objects. | 




  
   