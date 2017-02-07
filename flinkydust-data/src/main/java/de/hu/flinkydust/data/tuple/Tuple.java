/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hu.flinkydust.data.tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Basisklasse für ein Tupel, das beliebige Daten speichern kann und beliebig groß sein kann.
 */
public class Tuple {
	
	private Object[] values;
	private int arity;

	private Map<String, Integer> fieldIndexMap;

	public Tuple(int arity) {
		this.arity = arity;
		this.values = new Object[arity];
	}

	public Tuple(Object[] values) {
		this.values = values;
		this.arity = values.length;
	}

	public Tuple(Object[] values, Map<String, Integer> fieldIndexMap) {
		this.values = values;
		this.arity = values.length;
		this.fieldIndexMap = fieldIndexMap;
	}

	public Tuple(int arity, Map<String, Integer> fieldIndexMap) {
		this.values = new Object[arity];
		this.arity = arity;
		this.fieldIndexMap = fieldIndexMap;
	}

	/**
	 * Erzeugt eine Map mit dem gewünschten Fieldmapping.
	 * @param fieldNames
	 * 			Die Namen der Felder
	 * @return
	 * 			Die Map der Feldnamen.
	 */
	public static Map<String, Integer> createFieldMap(String[] fieldNames) {
		Map<String, Integer> fieldIndexMap = new HashMap<>();
		for (int headerIndex = 0; headerIndex < fieldNames.length; headerIndex++) {
			fieldIndexMap.put(fieldNames[headerIndex], headerIndex);
		}
		return fieldIndexMap;
	}

	public int getFieldIndex(String fieldName) {
		if (fieldIndexMap == null) {
			throw new NoFieldMappingException("Es wurde kein Feldmapping angegeben!");
		}
		Integer value = fieldIndexMap.get(fieldName);
		if (value == null) {
			throw new FieldNotFoundException("Das Feld mit dem Namen " + fieldName + " konnte nicht gefunden werden.");
		}
		return value;
	}

	public Map<String, Integer> getFieldIndexMap() {
		return fieldIndexMap;
	}

	public void setFieldIndexMap(Map<String, Integer> fieldIndexMap) {
		this.fieldIndexMap = fieldIndexMap;
	}

	public void setFieldIndex(String fieldName, int index) {
		fieldIndexMap.put(fieldName, index);
	}
	
	/**
	 * Gets the field at the specified position.
	 *
	 * @param pos The position of the field, zero indexed.
	 * @return The field at the specified position.
	 * @throws IndexOutOfBoundsException Thrown, if the position is negative, or equal to, or larger than the number of fields.
	 */
	public Object getField(int pos) {
		if (pos >= arity) {
			throw new IndexOutOfBoundsException("Index kann nicht größer als die Anzahl der Felder im Tupel sein.");
		}

		return values[pos];
	}

	public Object getField(String fieldName) {
		return getField(getFieldIndex(fieldName));
	}

	/**
	 * Sets the field at the specified position.
	 *
	 * @param value The value to be assigned to the field at the specified position.
	 * @param pos The position of the field, zero indexed.
	 * @throws IndexOutOfBoundsException Thrown, if the position is negative, or equal to, or larger than the number of fields.
	 */
	public void setField(Object value, int pos) {
		if (pos >= arity) {
			throw new IndexOutOfBoundsException("Index kann nicht größer als die Anzahl der Felder im Tupel sein.");
		}

		values[pos] = value;
	}

	/**
	 * Gets the number of field in the tuple (the tuple arity).
	 *
	 * @return The number of fields in the tuple.
	 */
	public int getArity() {
		return arity;
	}


}
