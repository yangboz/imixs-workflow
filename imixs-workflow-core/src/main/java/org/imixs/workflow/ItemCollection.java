/*******************************************************************************
 *  Imixs Workflow 
 *  Copyright (C) 2001, 2011 Imixs Software Solutions GmbH,  
 *  http://www.imixs.com
 *  
 *  This program is free software; you can redistribute it and/or 
 *  modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You can receive a copy of the GNU General Public
 *  License at http://www.gnu.org/licenses/gpl.html
 *  
 *  Project: 
 *  	http://www.imixs.org
 *  	http://java.net/projects/imixs-workflow
 *  
 *  Contributors:  
 *  	Imixs Software Solutions GmbH - initial API and implementation
 *  	Ralph Soika - Software Developer
 *******************************************************************************/

package org.imixs.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This Class defines a ValueObject to be used to exchange data structures used
 * by the org.imixs.workflow Framework. Most components of this framework use
 * this wrapper class to easy transport workflow data between the different
 * workflow modules. ValueObjects, particular in J2EE Applications, have the
 * advantage to improve performance of remote method calls. The Imixs
 * ItemCcollection enables a very flexibly and easy to use data structure.
 * 
 * A ItemCollection contains various Items (attributes). Every Item exist of a
 * Name (String) and a list of values (List of Object). Internal every Value is
 * stored inside a Vector Class. All values are stored internally in a Map
 * containing key values pairs.
 * 
 * NOTE: An ItemCollection is not serializable and can not be stored into
 * another ItemCollection. To serialize a ItemCollection use the
 * XMLItemCollection. @see XMLItemCollectionAdapter.
 * 
 * 
 * @author Ralph Soika
 * @version 2.1
 * @see org.imixs.workflow.WorkflowManager
 */

public class ItemCollection {
	// NOTE: ItemCollection is not serializable

	private static Logger logger = Logger.getLogger(ItemCollection.class.getName());

	private Map<String, List<Object>> hash = new Hashtable<String, List<Object>>();

	/**
	 * Creates a empty ItemCollection
	 * 
	 */
	public ItemCollection() {
		super();
	}

	/**
	 * Creates a new ItemCollection and transfers all Objects of map as new
	 * values.
	 * 
	 * @param map
	 */
	public ItemCollection(Map<String, List<Object>> map) {
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, List<Object>> entry = (Entry<String, List<Object>>) it.next();
			this.replaceItemValue(entry.getKey().toString(), entry.getValue());
		}
	}

	/**
	 * Creates a new ItemCollection by copy values from another ItemCollection
	 * 
	 * @param map
	 */
	public ItemCollection(ItemCollection aItemCol) {
		super();
		this.replaceAllItems(aItemCol.getAllItems());
	}

	/**
	 * This method clones the current ItemCollection with a minimum of
	 * attributes.
	 * 
	 * @param itemNames
	 *            - list of properties to be copied into the clone
	 * @return new ItemCollection
	 */
	public ItemCollection clone(final List<String> itemNames) {
		ItemCollection clone = new ItemCollection();
		if (itemNames != null && itemNames.size() > 0) {
			for (String aField : itemNames) {
				clone.replaceItemValue(aField, this.getItemValue(aField));
			}
		}
		return clone;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ItemCollection))
			return false;
		return hash.equals(((ItemCollection) o).getAllItems());
	}

	/**
	 * returns the Value of a single Item inside the ItemCollection. If the item
	 * has no value, this method returns an empty vector. If no item with the
	 * specified name exists, this method returns an empty vector. It does not
	 * throw an exception. The ItemName is not case sensitive. Use hasItem to
	 * verify the existence of an item.
	 * 
	 * @param aName
	 *            The name of an item.
	 * @return The value or values contained in the item. The data type of the
	 *         value depends on the data type of the item.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public List getItemValue(String aName) {
		aName = aName.toLowerCase();
		List<Object> o = hash.get(aName);
		if (o == null)
			return new Vector<Object>();
		else {
			List<Object> v = o;
			// scan vector for null values
			for (int i = 0; i < v.size(); i++) {
				if (v.get(i) == null)
					v.remove(i);
			}
			return v;
		}
	}

	/**
	 * Returns the value of an item with a single text value. If the item has no
	 * value or the value is numeric or non text, this method returns an empty
	 * String. If no item with the specified name exists, this method returns an
	 * empty String. It does not throw an exception. If the item has multiple
	 * values, this method returns the first value. The ItemName is not case
	 * sensitive. Use hasItem to verify the existence of an item.
	 * 
	 * @param aName
	 *            The name of an item.
	 * @return The value of the item
	 * 
	 */
	public String getItemValueString(String aName) {

		aName = aName.toLowerCase();
		List<?> v = (List<?>) getItemValue(aName);
		if (v.size() == 0)
			return "";
		else {
			// verify if value is null
			Object o = v.get(0);
			if (o == null)
				return "";
			else
				// changed from (String)o -> o.toString()
				return o.toString();
		}

	}

	/**
	 * Returns the value of an item with a single numeric value. If the item has
	 * no value or the value is no Integer, or empty, this method returns 0. If
	 * no item with the specified name exists, this method returns 0. It does
	 * not throw an exception. If the item has multiple values, this method
	 * returns the first value. The ItemName is not case sensitive. Use hasItem
	 * to verify the existence of an item.
	 * 
	 * @param aName
	 * @return integer value
	 * 
	 */
	public int getItemValueInteger(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return 0;

			String sValue = v.get(0).toString();
			return new Double(sValue).intValue();
		} catch (NumberFormatException e) {
			return 0;
		} catch (ClassCastException e) {
			return 0;
		}
	}

	/**
	 * Returns the value of an item with a long value. If the item has no value
	 * or the value is no number, or empty, this method returns 0. If no item
	 * with the specified name exists, this method returns 0. It does not throw
	 * an exception. If the item has multiple values, this method returns the
	 * first value. The ItemName is not case sensitive. Use hasItem to verify
	 * the existence of an item.
	 * 
	 * @param aName
	 * @return integer value
	 * 
	 */
	public long getItemValueLong(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return 0;

			String sValue = v.get(0).toString();
			return new Long(sValue).longValue();
		} catch (NumberFormatException e) {
			return 0;
		} catch (ClassCastException e) {
			return 0;
		}
	}

	/**
	 * Returns the value of an item with a single Date value. If the item has no
	 * value or the value is no Date, or empty, this method returns null. If no
	 * item with the specified name exists, this method returns null. It does
	 * not throw an exception. If the item has multiple values, this method
	 * returns the first value. The ItemName is not case sensitive. Use hasItem
	 * to verify the existence of an item.
	 * 
	 * @param aName
	 * @return Date value
	 * 
	 */
	public Date getItemValueDate(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return null;

			Object o = v.get(0);
			if (!(o instanceof Date))
				return null;

			return (Date) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * Returns the value of an item with a single numeric value. If the item has
	 * no value, this method returns 0.0. If no item with the specified name
	 * exists, this method returns 0.0. It does not throw an exception. If the
	 * item has multiple values, this method returns the first value. The
	 * Itemname is not case sensetive. Use hasItem to verify the existence of an
	 * item.
	 * 
	 * @param aName
	 * @return double value
	 * 
	 */
	public double getItemValueDouble(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return 0.0;
			else {
				// test for object type...
				Object o = v.get(0);
				if (o instanceof Double)
					return (Double) o;

				if (o instanceof Float)
					return (Float) o;

				if (o instanceof Long)
					return (Long) o;

				if (o instanceof Integer)
					return (Integer) o;

				// try to parse string.....
				try {
					return Double.valueOf(v.get(0).toString());
				} catch (ClassCastException e) {
					return 0;
				}
			}
		} catch (ClassCastException e) {
			return 0.0;
		}
	}

	/**
	 * Returns the value of an item with a single numeric value. If the item has
	 * no value, this method returns 0.0. If no item with the specified name
	 * exists, this method returns 0.0. It does not throw an exception. If the
	 * item has multiple values, this method returns the first value. The
	 * Itemname is not case sensetive. Use hasItem to verify the existence of an
	 * item.
	 * 
	 * @param aName
	 * @return float value
	 * 
	 */
	public float getItemValueFloat(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return (float) 0.0;
			else {
				// test for object type...
				Object o = v.get(0);

				if (o instanceof Float)
					return (Float) o;

				if (o instanceof Double)
					return (Float) o;

				if (o instanceof Long)
					return (Long) o;

				if (o instanceof Integer)
					return (Integer) o;

				// try to parse string.....
				try {
					return Float.valueOf(v.get(0).toString());
				} catch (ClassCastException e) {
					return 0;
				}

			}
		} catch (ClassCastException e) {
			return (float) 0.0;
		}
	}

	/**
	 * Returns the boolean value of an item. If the item has no value or the
	 * value is no boolean, or empty, this method returns false. If no item with
	 * the specified name exists, this method returns false. It does not throw
	 * an exception. If the item has multiple values, this method returns the
	 * first value. The Itemname is not case sensitive. Use hasItem to verify
	 * the existence of an item.
	 * 
	 * @param aName
	 * @return boolean value
	 * 
	 */
	public boolean getItemValueBoolean(String aName) {
		try {
			aName = aName.toLowerCase();
			List<?> v = getItemValue(aName);
			if (v.size() == 0)
				return false;
			Object sValue = v.get(0);// .firstElement().toString();
			// return new Boolean(sValue).booleanValue();
			return Boolean.valueOf(sValue.toString());
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Indicates whether an item exists in the document.
	 * 
	 * @param aName
	 *            The name of an item.
	 * @return true if an item with name exists in the document, false if no
	 *         item with name exists in the document
	 * 
	 */
	public boolean hasItem(String aName) {
		aName = aName.toLowerCase();
		return (hash.get(aName) != null);
	}

	/**
	 * Returns true if the value of an item with a single numeric value is from
	 * type 'Integer'
	 * 
	 * @param aName
	 * @return boolean true if object is from type Double
	 * 
	 */
	public boolean isItemValueInteger(String aName) {

		aName = aName.toLowerCase();
		List<?> v = getItemValue(aName);
		if (v.size() == 0)
			return false;
		else {
			// test for object type...
			Object o = v.get(0);
			return (o instanceof Integer);
		}
	}

	/**
	 * Returns true if the value of an item with a single numeric value is from
	 * type 'Long'
	 * 
	 * @param aName
	 * @return boolean true if object is from type Double
	 * 
	 */
	public boolean isItemValueLong(String aName) {

		aName = aName.toLowerCase();
		List<?> v = getItemValue(aName);
		if (v.size() == 0)
			return false;
		else {
			// test for object type...
			Object o = v.get(0);
			return (o instanceof Long);
		}
	}

	/**
	 * Returns true if the value of an item with a single numeric value is from
	 * type 'Double'
	 * 
	 * @param aName
	 * @return boolean true if object is from type Double
	 * 
	 */
	public boolean isItemValueDouble(String aName) {

		aName = aName.toLowerCase();
		List<?> v = getItemValue(aName);
		if (v.size() == 0)
			return false;
		else {
			// test for object type...
			Object o = v.get(0);
			return (o instanceof Double);
		}
	}

	/**
	 * Returns true if the value of an item with a single numeric value is from
	 * type 'Float'
	 * 
	 * @param aName
	 * @return boolean true if object is from type Double
	 * 
	 */
	public boolean isItemValueFloat(String aName) {

		aName = aName.toLowerCase();
		List<?> v = getItemValue(aName);
		if (v.size() == 0)
			return false;
		else {
			// test for object type...
			Object o = v.get(0);
			return (o instanceof Float);
		}
	}

	/**
	 * Returns true if the value of an item is from type 'Date'
	 * 
	 * @param aName
	 * @return boolean true if object is from type Double
	 * 
	 */
	public boolean isItemValueDate(String aName) {

		aName = aName.toLowerCase();
		List<?> v = getItemValue(aName);
		if (v.size() == 0)
			return false;
		else {
			// test for object type...
			Object o = v.get(0);
			return (o instanceof Date);
		}
	}

	/**
	 * returns all Items of the Collection as a Map
	 * 
	 * @return Map with all Items
	 */
	public Map<String, List<Object>> getAllItems() {
		return hash;

	}

	/**
	 * replaces the current map object. In different to the method
	 * replaceAllItems this method overwrites the hash object and did not copy
	 * the values
	 * 
	 * @param aHash
	 */
	public void setAllItems(Map<String, List<Object>> aHash) {
		hash = aHash;

	}

	/**
	 * Replaces the value of an item. If the ItemCollection does not contain an
	 * item with the specified name, the method creates a new item and adds it
	 * to the ItemCollection. The ItemName is not case sensitive. Use hasItem to
	 * verify the existence of an item. All item names will be lower cased.
	 * 
	 * Each item can contain a list of values (multivalue item). If a single
	 * value is provided the method creates a List with one single value
	 * (singlevalue item).
	 * 
	 * If the value is null the method will remove the item. This is equal to
	 * the method call removeItem()
	 * 
	 * If the ItemValue is not serializable the item will be removed.
	 * 
	 * 
	 * @param itemName
	 *            The name of the item or items you want to replace.
	 * @param itemValue
	 *            The value of the new item. The data type of the item depends
	 *            upon the data type of value, and does not need to match the
	 *            data type of the old item.
	 */
	public void replaceItemValue(String itemName, Object itemValue) {
		setItemValue(itemName, itemValue, false);
	}

	/**
	 * Appends a value to an existing item. If the ItemCollection does not
	 * contain an item with the specified name, the method creates a new item
	 * and adds it to the ItemCollection. The ItemName is not case sensitive.
	 * Use hasItem to verify the existence of an item. All item names will be
	 * lower cased.
	 * 
	 * If a value list is provided the method appends each single value.
	 * 
	 * If the value is null the method will remove the item. This is equal to
	 * the method call removeItem()
	 * 
	 * If the ItemValue is not serializable the item will be removed.
	 * 
	 * 
	 * @param itemName
	 *            The name of the item or items you want to replace.
	 * @param itemValue
	 *            The value of the new item. The data type of the item depends
	 *            upon the data type of value, and does not need to match the
	 *            data type of the old item.
	 */
	public void appendItemValue(String itemName, Object itemValue) {
		setItemValue(itemName, itemValue, true);
	}

	/**
	 * Helper method to replace an ItemValue.
	 * 
	 * @param itemName
	 *            - name of the value
	 * @param itemValue
	 *            - value
	 * @param append
	 *            - true if the value should be appended to an existing list
	 */
	@SuppressWarnings("unchecked")
	private void setItemValue(String itemName, Object itemValue, boolean append) {
		List<Object> itemValueList = null;

		if (itemName == null)
			return;
		// lower case itemname
		itemName = itemName.toLowerCase();

		// test if value is null
		if (itemValue == null) {
			// remove the item
			this.removeItem(itemName);
			return;
		}

		// test if value is ItemCollection
		if (itemValue instanceof ItemCollection) {
			// just warn - do not remove
			logger.warning("[ItemCollection] replaceItemValue '" + itemName
					+ "': ItemCollection can not be stored into an existing ItemCollection - use XMLItemCollection instead.");
		}

		// test if value is serializable
		if (!(itemValue instanceof java.io.Serializable)) {
			logger.warning("[ItemCollection] replaceItemValue '" + itemName + "': Object no Serializable!");
			this.removeItem(itemName);
			return;
		}

		// test if value is a list and remove null values
		if (itemValue instanceof List) {
			itemValueList = (List<Object>) itemValue;
			itemValueList.removeAll(Collections.singleton(null));
			// scan List for null values and remove them
			for (int i = 0; i < itemValueList.size(); i++) {
				// test if ItemCollection
				if (itemValueList.get(i) instanceof ItemCollection) {
					// just warn - do not remove
					logger.warning("[ItemCollection] replaceItemValue '" + itemName
							+ "': ItemCollection can not be stored into an existing ItemCollection - use XMLItemCollection instead.");
				}
			}
		} else {
			// create an instance of Vector
			itemValueList = new Vector<Object>();
			itemValueList.add(itemValue);
		}

		// now itemValue is of instance List

		// replace item value?
		if (append) {
			// append item value
			List<Object> oldValueList = (List<Object>) getItemValue(itemName);

			oldValueList.addAll(itemValueList);

			// for (Object o : itemValueList) {
			// oldValueList.add(o);
			// }
			hash.put(itemName, (List<Object>) oldValueList);
		} else
			hash.put(itemName, itemValueList);

	}

	/**
	 * Replaces all items specified in the map with new items, which are
	 * assigned to the specified values inside the map
	 * 
	 * @param map
	 */
	public void replaceAllItems(Map<String, List<Object>> map) {
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, List<Object>> entry = (Map.Entry<String, List<Object>>) it.next();
			replaceItemValue(entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ItemCollection itemColClone = new ItemCollection();
		itemColClone.replaceAllItems(this.getAllItems());
		return itemColClone;
	}

	/**
	 * removes a attribute from the item collection
	 * 
	 * @param name
	 */
	public void removeItem(String name) {
		name = name.toLowerCase();
		this.getAllItems().remove(name);
	}

	/**
	 * This method adds a single file to the ItemCollection. files will be
	 * stored into the property $file.
	 * 
	 * @param data
	 *            - byte array with file data
	 * @param fileName
	 *            - name of the file attachment
	 * @param contentType
	 *            - the contenttype (e.g. 'Text/HTML')
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void addFile(byte[] data, String fileName, String contentType) {
		if (data != null) {
			Vector<Object> vectorFileInfo = null;

			// IE includes '\' characters! so remove all these characters....
			if (fileName.indexOf('\\') > -1)
				fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);
			if (fileName.indexOf('/') > -1)
				fileName = fileName.substring(fileName.lastIndexOf('/') + 1);

			if (contentType == null || "".equals(contentType))
				contentType = "application/unknown";

			// Store files using a map....
			HashMap<String, List<Object>> mapFiles = null;
			List<?> vFiles = getItemValue("$file");
			if (vFiles != null && vFiles.size() > 0)
				mapFiles = (HashMap<String, List<Object>>) vFiles.get(0);
			else
				mapFiles = new HashMap<String, List<Object>>();

			// existing file will be overridden!
			vectorFileInfo = new Vector<Object>();
			// put file in a vector containing the byte array and also the
			// content type
			vectorFileInfo.add(contentType);
			vectorFileInfo.add(data);
			mapFiles.put(fileName, vectorFileInfo);
			replaceItemValue("$file", mapFiles);
		}
	}

	/**
	 * This method removes a single file attachment from the BlobWorkitem
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void removeFile(String aFilename) {
		/* delete attachment */
		Map<String, List<Object>> mapFiles = null;
		List<?> vFiles = getItemValue("$file");
		if (vFiles != null && vFiles.size() > 0) {
			mapFiles = (Map<String, List<Object>>) vFiles.get(0);
			mapFiles.remove(aFilename);
			replaceItemValue("$file", mapFiles);
		}

	}

	/**
	 * Returns files stored in the property '$file'. The files are returned in a
	 * Map interface where the key is the filename and the value is a list with
	 * two elements - the ContenType and the file content (byte[]). s Files can
	 * be added into a ItemCollection using the method addFile().
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Object>> getFiles() {
		List<?> vFiles = getItemValue("$file");
		if (vFiles != null && vFiles.size() > 0) {
			// test if the value part is a List or an Object[]. In case its an
			// Object[] we convert the array to a List

			Map<String, ?> testContent = (Map<String, ?>) vFiles.get(0);
			Map<String, List<Object>> mapFiles = new HashMap<String, List<Object>>();
			for (Entry<String, ?> entry : testContent.entrySet()) {
				String sFileName = entry.getKey();
				Object obj = entry.getValue();
				if (obj instanceof List) {
					mapFiles.put(sFileName, (List<Object>) obj);
				} else {
					// convert array to List
					mapFiles.put(sFileName, Arrays.asList(obj));
				}
			}
			return mapFiles;
		}

		return null;
	}

	/**
	 * Returns a list of file names attached to the current BlobWorkitem. File
	 * Attachments can be added using the method addFile().
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getFileNames() {
		// File attachments...
		List<String> files = new Vector<String>();

		HashMap<String, List<Object>> mapFiles = null;
		List<?> vFiles = getItemValue("$file");
		if (vFiles != null && vFiles.size() > 0) {
			mapFiles = (HashMap<String, List<Object>>) vFiles.get(0);
			// files = new String[mapFiles.entrySet().size()];
			Iterator<?> iter = mapFiles.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, List<Object>> mapEntry = (Map.Entry<String, List<Object>>) iter.next();
				String aFileName = mapEntry.getKey().toString();
				// files[iFileCount] = aFileName;
				files.add(aFileName);
			}
		}

		return files;
	}

	// @SuppressWarnings("unchecked")
	public Map<String, ?> getItem() {
		return new ItemAdapter(this);
	}

	public Map<String, ?> getItemList() {
		return new ItemListAdapter(this);
	}

	public Map<String, ?> getItemListArray() {
		return new ItemListArrayAdapter(this);
	}

	/*
	 * convenience methods
	 */

	/**
	 * @return current type
	 */
	public String getType() {
		return getItemValueString(WorkflowKernel.TYPE);
	}

	/**
	 * @return current $processID
	 */
	public int getProcessID() {
		return getItemValueInteger(WorkflowKernel.PROCESSID);
	}

	/**
	 * @return current $ActivityID
	 */
	public int getActivityID() {
		return getItemValueInteger(WorkflowKernel.ACTIVITYID);
	}

	/**
	 * set $ActivityID
	 * 
	 * @param activityID
	 */
	public void setActivityID(int activityID) {
		replaceItemValue(WorkflowKernel.ACTIVITYID, activityID);
	}

	/**
	 * @return current $ModelVersion
	 */
	public String getModelVersion() {
		return getItemValueString(WorkflowKernel.MODELVERSION);
	}

	/**
	 * @return $UniqueID
	 */
	public String getUniqueID() {
		return getItemValueString("$UniqueID");
	}

	/**
	 * This class helps to adapt the behavior of a single value item to be used
	 * in a jsf page using a expression language like this:
	 * 
	 * #{mybean.item['txtMyItem']}
	 * 
	 * 
	 * @author rsoika
	 * 
	 */
	class ItemAdapter implements Map<String, Object> {
		ItemCollection itemCollection;

		public ItemAdapter() {
			itemCollection = new ItemCollection();
		}

		public ItemAdapter(ItemCollection acol) {
			itemCollection = acol;
		}

		public void setItemCollection(ItemCollection acol) {
			itemCollection = acol;
		}

		/**
		 * returns a single value out of the ItemCollection if the key does not
		 * exist the method will create a value automatically
		 */
		public Object get(Object key) {
			// check if a value for this key is available...
			// if not create a new empty value
			if (!itemCollection.hasItem(key.toString()))
				itemCollection.replaceItemValue(key.toString(), "");

			// return first value from vector if size >0
			List<?> v = itemCollection.getItemValue(key.toString());
			if (v.size() > 0)
				return v.get(0);
			else
				// otherwise return null
				return null;
		}

		/**
		 * puts a single value into the ItemCollection
		 */
		public Object put(String key, Object value) {
			if (key == null)
				return null;
			itemCollection.replaceItemValue(key.toString(), value);
			return value;
		}

		/* ############### Default methods ################# */

		public void clear() {
			itemCollection.getAllItems().clear();
		}

		public boolean containsKey(Object key) {
			return itemCollection.getAllItems().containsKey(key);
		}

		public boolean containsValue(Object value) {
			return itemCollection.getAllItems().containsValue(value);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Set entrySet() {
			return itemCollection.getAllItems().entrySet();
		}

		public boolean isEmpty() {
			return itemCollection.getAllItems().isEmpty();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Set keySet() {
			return itemCollection.getAllItems().keySet();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void putAll(Map m) {
			itemCollection.getAllItems().putAll(m);

		}

		public Object remove(Object key) {
			return itemCollection.getAllItems().remove(key);
		}

		public int size() {
			return itemCollection.getAllItems().size();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection values() {
			return itemCollection.getAllItems().values();
		}

	}

	/**
	 * This class helps to addapt the behavior of a multivalue item to be used
	 * in a jsf page using a expression language like this:
	 * 
	 * #{mybean.item['txtMyList']}
	 * 
	 * 
	 * @author rsoika
	 * 
	 */
	class ItemListAdapter extends ItemAdapter {

		public ItemListAdapter(ItemCollection acol) {
			itemCollection = acol;
		}

		/**
		 * returns a multi value out of the ItemCollection if the key dos not
		 * exist the method will create a value automatical
		 */
		public Object get(Object key) {
			// check if a value for this key is available...
			// if not create a new empty value
			if (!itemCollection.hasItem(key.toString()))
				itemCollection.replaceItemValue(key.toString(), "");

			return itemCollection.getItemValue(key.toString());
		}

	}

	class ItemListArrayAdapter extends ItemAdapter {

		public ItemListArrayAdapter(ItemCollection acol) {
			itemCollection = acol;
		}

		/**
		 * returns a multi value out of the ItemCollection if the key dos not
		 * exist the method will create a value automatical
		 */
		@SuppressWarnings("rawtypes")
		public Object get(Object key) {
			// check if a value for this key is available...
			// if not create a new empty value
			if (!itemCollection.hasItem(key.toString()))
				itemCollection.replaceItemValue(key.toString(), "");
			// return new ArrayList Object containing values from vector
			ArrayList<Object> aList = new ArrayList<Object>();
			Collection col = itemCollection.getItemValue(key.toString());
			for (Object aEntryValue : col) {
				aList.add(aEntryValue);
			}
			return aList;

		}

		/**
		 * puts a arraylist value into the ItemCollection
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Object put(String key, Object value) {
			if (key == null)
				return null;

			// skipp null values
			if (value == null) {
				itemCollection.replaceItemValue(key.toString(), new Vector());
				return null;
			}
			// convert List into Vector object
			if (value instanceof List || value instanceof Object[]) {
				Vector v = new Vector();
				// check type of list (array and list are supported but need
				// to be read in different ways
				if (value instanceof List)
					for (Object aEntryValue : (List) value) {
						v.add(aEntryValue);
					}
				else if (value instanceof Object[])
					for (Object aEntryValue : (Object[]) value) {
						v.add(aEntryValue);
					}
				itemCollection.replaceItemValue(key.toString(), v);
			} else
				// non convertable object!
				itemCollection.replaceItemValue(key.toString(), value);

			return value;
		}
	}
}
