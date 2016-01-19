// BasicBSONObject.java

/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.bson;

// BSON
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;
import org.bson.util.JSON;


// Java

/**
 * A simple implementation of <code>DBObject</code>. A <code>DBObject</code> can
 * be created as follows, using this class: <blockquote>
 * 
 * <pre>
 * DBObject obj = new BasicBSONObject();
 * obj.put(&quot;foo&quot;, &quot;bar&quot;);
 * </pre>
 * 
 * </blockquote>
 */
public class BasicBSONObject extends LinkedHashMap<String, Object> implements
		BSONObject {

	private static final long serialVersionUID = -4415279469780082174L;

	/**
	 * Creates an empty object.
	 */
	public BasicBSONObject() {
	}

	public BasicBSONObject(int size) {
		super(size);
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * Convenience CTOR
	 * 
	 * @param key
	 *            key under which to store
	 * @param value
	 *            value to stor
	 */
	public BasicBSONObject(String key, Object value) {
		put(key, value);
	}

	/**
	 * Creates a DBObject from a map.
	 * 
	 * @param m
	 *            map to convert
	 */
	@SuppressWarnings("unchecked")
	public BasicBSONObject(Map m) {
		super(m);
	}

	/**
	 * Converts a DBObject to a map.
	 * 
	 * @return the DBObject
	 */
	@Override
	public Map toMap() {
		return new LinkedHashMap<String, Object>(this);
	}

	/**
	 * Deletes a field from this object.
	 * 
	 * @param key
	 *            the field name to remove
	 * @return the object removed
	 */
	@Override
	public Object removeField(String key) {
		return remove(key);
	}

	/**
	 * Checks if this object contains a given field
	 * 
	 * @param field
	 *            field name
	 * @return if the field exists
	 */
	@Override
	public boolean containsField(String field) {
		return super.containsKey(field);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public boolean containsKey(String key) {
		return containsField(key);
	}

	/**
	 * Gets a value from this object
	 * 
	 * @param key
	 *            field name
	 * @return the value
	 */
	@Override
	public Object get(String key) {
		return super.get(key);
	}

	/**
	 * Returns the value of a field as an <code>int</code>.
	 * 
	 * @param key
	 *            the field to look for
	 * @return the field value (or default)
	 */
	public int getInt(String key) {
		Object o = get(key);
		if (o == null)
			throw new NullPointerException("no value for: " + key);

		return BSON.toInt(o);
	}

	/**
	 * Returns the value of a field as an <code>int</code>.
	 * 
	 * @param key
	 *            the field to look for
	 * @param def
	 *            the default to return
	 * @return the field value (or default)
	 */
	public int getInt(String key, int def) {
		Object foo = get(key);
		if (foo == null)
			return def;

		return BSON.toInt(foo);
	}

	/**
	 * Returns the value of a field as a <code>long</code>.
	 * 
	 * @param key
	 *            the field to return
	 * @return the field value
	 */
	public long getLong(String key) {
		Object foo = get(key);
		return ((Number) foo).longValue();
	}

	/**
	 * Returns the value of a field as an <code>long</code>.
	 * 
	 * @param key
	 *            the field to look for
	 * @param def
	 *            the default to return
	 * @return the field value (or default)
	 */
	public long getLong(String key, long def) {
		Object foo = get(key);
		if (foo == null)
			return def;

		return ((Number) foo).longValue();
	}

	/**
	 * Returns the value of a field as a <code>double</code>.
	 * 
	 * @param key
	 *            the field to return
	 * @return the field value
	 */
	public double getDouble(String key) {
		Object foo = get(key);
		return ((Number) foo).doubleValue();
	}

	/**
	 * Returns the value of a field as an <code>double</code>.
	 * 
	 * @param key
	 *            the field to look for
	 * @param def
	 *            the default to return
	 * @return the field value (or default)
	 */
	public double getDouble(String key, double def) {
		Object foo = get(key);
		if (foo == null)
			return def;

		return ((Number) foo).doubleValue();
	}

	/**
	 * Returns the value of a field as a string
	 * 
	 * @param key
	 *            the field to look up
	 * @return the value of the field, converted to a string
	 */
	public String getString(String key) {
		Object foo = get(key);
		if (foo == null)
			return null;
		return foo.toString();
	}

	/**
	 * Returns the value of a field as a string
	 * 
	 * @param key
	 *            the field to look up
	 * @param def
	 *            the default to return
	 * @return the value of the field, converted to a string
	 */
	public String getString(String key, final String def) {
		Object foo = get(key);
		if (foo == null)
			return def;

		return foo.toString();
	}

	/**
	 * Returns the value of a field as a boolean.
	 * 
	 * @param key
	 *            the field to look up
	 * @return the value of the field, or false if field does not exist
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	/**
	 * Returns the value of a field as a boolean
	 * 
	 * @param key
	 *            the field to look up
	 * @param def
	 *            the default value in case the field is not found
	 * @return the value of the field, converted to a string
	 */
	public boolean getBoolean(String key, boolean def) {
		Object foo = get(key);
		if (foo == null)
			return def;
		if (foo instanceof Number)
			return ((Number) foo).intValue() > 0;
		if (foo instanceof Boolean)
			return ((Boolean) foo).booleanValue();
		throw new IllegalArgumentException("can't coerce to bool:"
				+ foo.getClass());
	}

	/**
	 * Returns the object id or null if not set.
	 * 
	 * @param field
	 *            The field to return
	 * @return The field object value or null if not found (or if null :-^).
	 */
	public ObjectId getObjectId(final String field) {
		return (ObjectId) get(field);
	}

	/**
	 * Returns the object id or def if not set.
	 * 
	 * @param field
	 *            The field to return
	 * @param def
	 *            the default value in case the field is not found
	 * @return The field object value or def if not set.
	 */
	public ObjectId getObjectId(final String field, final ObjectId def) {
		final Object foo = get(field);
		return (foo != null) ? (ObjectId) foo : def;
	}

	/**
	 * Returns the date or null if not set.
	 * 
	 * @param field
	 *            The field to return
	 * @return The field object value or null if not found.
	 */
	public Date getDate(final String field) {
		return (Date) get(field);
	}

	/**
	 * Returns the date or def if not set.
	 * 
	 * @param field
	 *            The field to return
	 * @param def
	 *            the default value in case the field is not found
	 * @return The field object value or def if not set.
	 */
	public Date getDate(final String field, final Date def) {
		final Object foo = get(field);
		return (foo != null) ? (Date) foo : def;
	}

	/**
	 * Add a key/value pair to this object
	 * 
	 * @param key
	 *            the field name
	 * @param val
	 *            the field value
	 * @return the <code>val</code> parameter
	 */
	@Override
	public Object put(String key, Object val) {
		return super.put(key, val);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(Map m) {
		for (Map.Entry entry : (Set<Map.Entry>) m.entrySet()) {
			put(entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	public void putAll(BSONObject o) {
		for (String k : o.keySet()) {
			put(k, o.get(k));
		}
	}

	/**
	 * Add a key/value pair to this object
	 * 
	 * @param key
	 *            the field name
	 * @param val
	 *            the field value
	 * @return <code>this</code>
	 */
	public BasicBSONObject append(String key, Object val) {
		put(key, val);

		return this;
	}

	/**
	 * Returns a JSON serialization of this object
	 * 
	 * @return JSON serialization
	 */
	@Override
	public String toString() {
		return JSON.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BSONObject))
			return false;

		BSONObject other = (BSONObject) o;
		if (!keySet().equals(other.keySet()))
			return false;

		for (String key : keySet()) {
			Object a = get(key);
			Object b = other.get(key);

			if (a == null) {
				if (b != null)
					return false;
			}
			if (b == null) {
				if (a != null)
					return false;
			} else if (a instanceof Number && b instanceof Number) {
				if (((Number) a).doubleValue() != ((Number) b).doubleValue())
					return false;
			} else if (a instanceof Pattern && b instanceof Pattern) {
				Pattern p1 = (Pattern) a;
				Pattern p2 = (Pattern) b;
				if (!p1.pattern().equals(p2.pattern())
						|| p1.flags() != p2.flags())
					return false;
			} else {
				if (!a.equals(b))
					return false;
			}
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> type) throws Exception {
		boolean hasConsturctor = false;
		Field[] fields = type.getDeclaredFields();
		Method[] methods = type.getDeclaredMethods();
		T result = null;
		for (Constructor<?> con : type.getConstructors()) {
			if (con.getParameterTypes().length == 0) {
				result = (T) con.newInstance();
				hasConsturctor = true;
				break;
			}
		}
		if (hasConsturctor == false)
			throw new Exception(
					"Class does not exist an constructor method with no arg");
		String methodName;
		String paramName;
		String fieldName;
		Object obj;
		for (Method m : methods) {
			m.setAccessible(true);
			methodName = m.getName();
			if (methodName.startsWith("set")) {
				paramName = methodName.substring(3);
				for (Field f : fields) {
					fieldName = f.getName();
					if (fieldName.equalsIgnoreCase(paramName)) {
						if (this.containsField(fieldName)) {
							obj = this.get(fieldName);
							if(obj == null)
								break;
							// bsonList <=> list
							if (obj instanceof BasicBSONList) {
								List list = new ArrayList();
								BSONObject objList = (BasicBSONList) obj;
								BSONObject temp = null;
								for (String key : objList.keySet()) {
									temp = (BSONObject) objList.get(key);
									Type _type = f.getGenericType();
									if (_type instanceof ParameterizedType && temp != null)
										list.add(temp
												.as((Class<?>) (((ParameterizedType) _type)
														.getActualTypeArguments()[0])));
								}
								m.invoke(result, list);
							} else if (obj instanceof BSONObject) { // bson <=> object
								Type _type = f.getGenericType();
								if(_type == null) break;
								if (_type instanceof ParameterizedType)
									m.invoke(
											result,
											((BSONObject) obj)
													.as((Class<?>) (((ParameterizedType) _type)
															.getActualTypeArguments()[0])));
								else
									m.invoke(result,
											((BSONObject) obj).as(f.getType()));
							} else {// parameter <=> common field
								String _type = f.getType().getName();
								if(_type.equals("int"))
									m.invoke(result, Integer.parseInt(obj.toString()));
								else if(_type.equals("java.lang.String"))
									m.invoke(result, obj.toString());
								else if(_type.equals("long"))
									m.invoke(result, Long.parseLong(obj.toString()));
								else if(_type.equals("byte"))
									m.invoke(result, Byte.parseByte(obj.toString()));
								else if(_type.equals("short"))
									m.invoke(result, Short.parseShort(obj.toString()));
								else if(_type.equals("float"))
									m.invoke(result, Float.parseFloat(obj.toString()));
								else if(_type.equals("double"))
									m.invoke(result, Double.parseDouble(obj.toString()));
								else if(_type.equals("char"))
									m.invoke(result, Boolean.parseBoolean(obj.toString()));
								else
									m.invoke(result, obj);
							}
						}
						break;
					}
				}
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T as(T type) throws Exception {
		type = (T) as(type.getClass());
		return type;
	}

	public static <T> BSONObject typeToBson(T object) throws Exception {
		Class<?> cl = object.getClass();
		Field[] fields = cl.getDeclaredFields();
		Method[] methods = cl.getDeclaredMethods();
		BSONObject result = new BasicBSONObject();
		String methodName = "";
		String paramName = "";
		String fieldName = "";
		for (Method m : methods) {
			m.setAccessible(true);
			methodName = m.getName();
			if (methodName.startsWith("get")) {
				paramName = methodName.substring(3);
				for (Field f : fields) {
					fieldName = f.getName();
					if (fieldName.equalsIgnoreCase(paramName)) {
						Class<?> type = f.getType();
						if(type.isPrimitive() || type.getName().startsWith("java.lang")) {
							result.put(fieldName, m.invoke(object));
						} else if(type.isAssignableFrom(Date.class) || type.isAssignableFrom(java.sql.Date.class)) {
							result.put(fieldName, m.invoke(object));
						} else if(type.isAssignableFrom(List.class)) {
							BSONObject listObj = new BasicBSONList();
							int index = 0;
							Type _type = f.getGenericType();
							if(_type == null) break;
							if(_type instanceof ParameterizedType) {
								List list = (List)m.invoke(object);
								if(list == null) break;
								for(Object obj : list) {
									listObj.put(Integer.toString(index), typeToBson(obj));
									++index;
								}
								result.put(fieldName, listObj);
							}
						}
						break;
					}
				}
			}
		}
		return result;
	}
}
