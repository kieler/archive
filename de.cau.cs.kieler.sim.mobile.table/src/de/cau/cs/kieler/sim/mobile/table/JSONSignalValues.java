/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

package de.cau.cs.kieler.sim.mobile.table;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * The Class JSONSignalValues. This is an implementation of a <B>CONVENTION</B>
 * for representing signal values in JSON format. This convention is used
 * to explicitly denote the presents/absents of pure and valued signals.
 * <BR><BR>
 * Signals in JSON format should contain not just a value but a
 * JSONSignalValue. That is they contain a JSONObject as their value, where
 * at least one parameter is called <B>present</B> and this parameter is of type
 * boolean, indicating the presents or absents of a signal.
 * <BR><BR>
 * Examples:<BR>
 * Valued signal a, present, with an integer value of 10:<BR>
 * a:{present:true, value:10}<BR>
 * Valued signal a, absent, with a string value of "hello signal":<BR>
 * a:{present:false, value:"hello signal"}<BR>
 * Pure signal b, present<BR>
 * b:{present:true}<BR>
 * Pure signal b, absent<BR>
 * b:{present:absent}<BR>
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 */
public class JSONSignalValues {

	/** The present key of this *CONVENTION*. */
	public static final String presentKey = "present";
	
	/** The value key of this *CONVENTION*. */
	public static final String valueKey = "value";
	
	//-------------------------------------------------------------------------
	
	/**
	 * Sets the presents of a signaled value. If this is not a SignalValue
	 * or at least a JSONObject where the presentKey can be inserted, then
	 * an exception is thrown.
	 * 
	 * @param signalValue the JSONObject representing the signal value
	 * @param isPresent a boolean indicating whether the signal is present
	 * 
	 * @throws JSONException a JSONException
	 */
	public static void setPresent(JSONObject signalValue,
								  boolean isPresent) 
														throws JSONException {
		signalValue.put(presentKey, isPresent);
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Checks if this Object is a SignalValue. It is a SignalValue iff it is
	 * a JSONObject and has a presentKey.
	 * 
	 * @param signalValue the Object to be checked
	 * 
	 * @return true, if it is a SignalValue
	 * 
	 * @throws JSONException a JSONException
	 */
	public static boolean isSignalValue(Object signalValue) 
														throws JSONException {
		if (!(signalValue instanceof JSONObject)) return false;
		return ((JSONObject)signalValue).has(presentKey);
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Gets the value of a SignalValue. This method returns null if it is a
	 * SignalValue of a pure signal or it is no SignalValue at all.
	 * 
	 * @param signalValue the SignalValue (or Object if that is unclear)
	 * 
	 * @return the value of the SignalValue or null
	 * 
	 * @throws JSONException a JSONException
	 */
	public static Object getSignalValue(Object signalValue)
													throws JSONException {
		//no SignalValue at all
		if (!isSignalValue(signalValue)) return null;
		//if its a pure signal that there is no valueKey
		if (((JSONObject)signalValue).has(valueKey)) 
			return ((JSONObject)signalValue).get(valueKey);
		return null;
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Checks whether the signal of this SignalValue is present. If it is no
	 * SignalValue at all, then the default value is absent.
	 * 
	 * @param signalValue the SignalValue
	 * 
	 * @return true, if signal is present
	 * 
	 * @throws JSONException a JSONException
	 */
	public static boolean isPresent(Object signalValue) 
													throws JSONException {
		//no SignalValue at all
		if (!isSignalValue(signalValue)) return false;
		//if there is a presentKey, return the value of that
		if (((JSONObject)signalValue).has(presentKey)) {
			return (((JSONObject)signalValue).getBoolean(presentKey));
		}
		return false;
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Create a new SignalValue for a valued signal, containing a presentKey
	 * and a valueKey.
	 * 
	 * @param value the value of the signal
	 * @param present true if the signal is present
	 * 
	 * @return the SignalValue as a JSONObject
	 * 
	 * @throws JSONException a JSONException
	 */
	public static JSONObject newValue(Object value, 
									  boolean present) 
														throws JSONException {
		
		JSONObject returnObject =  new JSONObject();

		returnObject.accumulate(valueKey, value);
		returnObject.accumulate(presentKey, new Boolean(present));
		
		return returnObject;
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Create a new SignalValue for a pure signal, containing a presentKey
	 * only.
	 * 
	 * @param present true if the signal is present
	 * 
	 * @return the SignalValue as a JSONObject
	 * 
	 * @throws JSONException a JSONException
	 */
	public static JSONObject newValue(boolean present) 
														throws JSONException {
		
		JSONObject returnObject =  new JSONObject();

		returnObject.accumulate(presentKey, new Boolean(present));
		
		return returnObject;
	}
	
}
