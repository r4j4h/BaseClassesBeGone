package net.rotten194;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class CallCache {
	ArrayList<Object[]> events = new ArrayList<Object[]>(2);
		
	/**
	 * @param method The method name
	 * @param id An arbitrary id
	 * @param args The arguments to the method
	 */
	public void createEvent(String method, String id, Object...args){
		events.add(create(method, id, args));
	}

	private Object[] create(String method, String id, Object[] objs){
		if (objs == null) objs = new Object[0];
		Object[] ret = new Object[objs.length + 2];
		ret[0] = method;
		ret[1] = id;
		System.arraycopy(objs, 0, ret, 2, objs.length);
		return ret;
	}
	
	/**
	 * @return An unmodifiable list of all event arrays
	 */
	public List<Object[]> getEvents(){
		return Collections.unmodifiableList(events);
	}
	
	
	/**
	 * A method that returns a filtered event list.
	 * @param method Method filter, or null to accept all methods
	 * @param id Id filter, or null to accept all id's
	 * @param removeMethodAndId If true, will remove the method and id from the event arrays in the returned list, leaving only the arguments. Somewhat slow.
	 * @return An unmodifiable list of all matching event arrays.
	 */
	public List<Object[]> getFilteredEvents(String method, String id, boolean removeMethodAndId){
		ArrayList<Object[]> ret = new ArrayList<Object[]>();
		for (Object[] arr : events){
			boolean accept = true;
			if (method != null && !(arr[0].equals(method))){
				accept = false;
			}
			if (id != null && !(arr[1].equals(id))){
				accept = false;
			}
			if (accept){
				if (removeMethodAndId){
					Object[] retarr = new Object[arr.length - 2];
					System.arraycopy(arr, 2, retarr, 0,retarr.length);
					ret.add(retarr);
				}
				else{
					ret.add(arr);
				}
			}
		}
		return Collections.unmodifiableList(ret);
	}
	
	/**
	 * Takes a list of arguments and generates a single string from them.
	 * <pre>assert getId("foo", "bar", "foobar").equals("foo|bar|foobar");</pre>
	 * @param parts A varargs list of Strings
	 * @return A concatenation of the arguments separated by pipe (<pre>|</pre>) characters.
	 */
	public static String getId(String...parts){
		String ret = "";
		for (int i = 0; i < parts.length; i++){ //DO NOT SWITCH TO FOREACH
			ret += parts[i];
			if (i != parts.length){
				ret += "|";
			}
		}
		return ret;
	}
	
	public ArrayList<Throwable> invokeEventsOn(Object invokee) throws NoSuchMethodException, SecurityException{
		ArrayList<Throwable> exceptions = new ArrayList<Throwable>();
		Class<?> clazz = invokee.getClass();
		for (Object[] arr : events){
			String method = (String)arr[0];
			Object[] params = new Object[arr.length - 2];
			System.arraycopy(arr, 2, params, 0, params.length);
			Class<?>[] types = new Class<?>[arr.length - 2];
			for (int i = 0; i < types.length; i++){
				types[i] = arr[i + 2].getClass();
			}
			Method m = clazz.getDeclaredMethod(method, types);
			try {
				m.invoke(invokee, params);
			} catch (InvocationTargetException e) {
				System.err.println("error in invoke (" + m + "): " + e);
				exceptions.add(e.getCause());
			} catch (IllegalAccessException e) {
				System.err.println("error in invoke(" + m + "): " + e);
				exceptions.add(e);
			} catch (IllegalArgumentException e) {
				System.err.println("error in invoke(" + m + "): " + e);
				exceptions.add(e);
			}
		}
		return exceptions;
	}
}
