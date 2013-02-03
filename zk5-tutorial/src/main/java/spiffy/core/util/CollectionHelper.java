package spiffy.core.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper methods for collections
 * 
 * @author Kasper B. Graversen, (c) 2007
 */
public class CollectionHelper {
	
	/**
	 * An easy way to construct an array list. just do
	 * 
	 * <pre>
	 * ArrayList&lt;String&gt; al = CollectionHelper.ArrayList(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;);
	 * </pre>
	 * 
	 * Rather than
	 * 
	 * <pre>
	 * ArrayList&lt;String&gt; al = new ArrayList&lt;String&gt;()
	 * al.add(&quot;a&quot;);
	 * al.add(&quot;b&quot;);
	 * al.add(&quot;c&quot;);
	 * </pre>
	 * 
	 * @param elements
	 *            the elements to create an ArrayList of
	 * @return a freshly created <tt>ArrayList</tt> containing elements given as arguments. If elements is null, null
	 *         is returned.
	 * @since 0.1
	 */
	public static <T> ArrayList<T> arrayList(final T... elements) {
		if( elements == null )
			return null;
		final ArrayList<T> result = new ArrayList<T>(elements.length);
		for(final T elem : elements) {
			result.add(elem);
		}
		return result;
	}
	
	/**
	 * An easy way to generate an ArrayList holding mixed types of objects. The current type system in Java does not
	 * allow you to say
	 * 
	 * <pre>
	 * ArrayList&lt;? extends Object&gt; genericList = arrayList(1, &quot;two&quot;, 3.0);
	 * </pre>
	 * 
	 * thus you should use this helper method instead
	 * 
	 * @param elements
	 *            elements to store in the list
	 * @return an array list
	 */
	public static ArrayList<? super Object> arrayListObjects(final Object... elements) {
		if( elements == null )
			return null;
		final ArrayList<? super Object> result = new ArrayList<Object>(elements.length);
		for(final Object elem : elements) {
			result.add(elem);
		}
		return result;
	}
	
	/**
	 * Checks to see if the collection is of size 1 and if so returns that element.
	 * <p>
	 * This method is particularly nice for DAO implementations, as all get methods should return a collection of
	 * objects rather than just one object. This enables the DAO to return several objects in case the query is wrong,
	 * or worse, if there are data problems in the database. Hence avoid code such as
	 * 
	 * <pre>
	 * class PersonDao {
	 * 	Person getPerson(String arg1, String arg2);
	 * }
	 * </pre>
	 * 
	 * instead use
	 * 
	 * <pre>
	 * class PersonDao {
	 * 	Collection&lt;Person&gt; getPerson(String arg1, String arg2);
	 * }
	 * </pre>
	 * 
	 * and query the first element with this method
	 * 
	 * @param collection
	 *            any non-collection
	 * @return first element of a collection, if the collection has a size of 1.
	 * @throws IllegalStateException
	 *             when collection is not of size 1
	 * @since 0.3
	 */
	public static <T> T firstOnly(final Collection<T> collection) {
		if( collection == null )
			throw new IllegalArgumentException("argument collection is null");
		if( collection.size() != 1 )
			throw new IllegalStateException("Collection has size " + collection.size() + " must have size 1!");
		return collection.iterator().next();
	}
}
