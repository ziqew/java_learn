package spiffy.core.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Helper methods for string manipulations and/or string representations of various entities
 * 
 * @author Kasper B. Graversen, (c) 2007
 */

public class StringHelper {
	
	/**
	 * Given a string check to see if it is in a collection of strings (using <tt>equals()</tt>)
	 * 
	 * @param searchString
	 *            string to search for. If <tt>null</tt> false is returned.
	 * @param searchStringList
	 *            list of strings to search within
	 * @return true if string is in the search list, or false if searchString is <tt>null</tt> or not present in the
	 *         list
	 * @throws IllegalArgumentException
	 *             when argument SearchStringList has length > 0
	 * @since 0.03
	 */
	public static boolean in(final String searchString, final String... searchStringList) {
		if( searchString == null ) { return false; }
		if( searchStringList.length == 0 ) { throw new IllegalArgumentException(
				"argument SearchStringList must have length > 0"); }
		
		for(final String s : searchStringList) {
			if( s.equals(searchString) ) { return true; }
		}
		return false;
	}
	
	/**
	 * Given a string, trim it, and if different from the string "", check to see if it is in a collection of strings
	 * (using <tt>equals()</tt>).
	 * <P>
	 * This is quite often used in web programming, where input on the server side may either be <tt>null</tt>,
	 * <tt>""</tt> or some string.
	 * <p>
	 * 
	 * @param searchString
	 *            string to search for
	 * @param searchStringList
	 *            list of strings to search within
	 * @return true if string is in the search list, or false if searchString is <tt>null</tt> or not present in the
	 *         list
	 * @throws IllegalArgumentException
	 *             when argument SearchStringList has length > 0
	 * @see #in(String, String[])
	 * @since 0.03
	 */
	public static boolean inAndNonEmpty(final String searchString, final String... searchStringList) {
		if( searchString == null ) { return false; }
		if( searchStringList.length == 0 ) { throw new IllegalArgumentException(
				"argument SearchStringList must have length > 0"); }
		final String trimmedString = searchString.trim();
		if( trimmedString.equals("") ) { return false; }
		
		for(final String s : searchStringList) {
			if( s.equals(trimmedString) ) { return true; }
		}
		return false;
	}
	
	/**
	 * Join 0..n strings separated by a delimiter. The delimiter is not concatenated after the last element. e.g. given
	 * an iterator with the elements <tt>&quot;, &quot;, &quot;a&quot;, &quot;b&quot;, &quot;c&quot;</tt>
	 * <pre>
	 * join(iterator)
	 * </pre>
	 * 
	 * becomes
	 * 
	 * <pre>
	 * &quot;a, b, c&quot;
	 * </pre>
	 * 
	 * @param delimiter
	 *            the delimiter to insert between each string.
	 * @param iterator
	 *            the iterator of elements to concatenate.
	 * @return the delimiter-concatenated string. If <tt>null</tt> is given as input strings, <tt>null</tt> is
	 *         returned.
	 */
	public static String join(final String delimiter, final Iterator<?> iterator) {
		if( iterator == null ) { return null; }
		
		final StringBuilder sb = new StringBuilder();
		while(iterator.hasNext()) {
			sb.append(iterator.next().toString());
			sb.append(delimiter);
		}
		// remove last delimiter
		final int bufLen = sb.length();
		if( bufLen > 0 ) { // only removed if we ever added anything
			sb.delete(bufLen - delimiter.length(), bufLen);
		}
		return sb.toString();
	}
	
	/**
	 * Join 0..n strings separated by a delimiter. The delimiter is not concatenated after the last element. e.g.
	 * 
	 * <pre>
	 * join(&quot;, &quot;, &quot;a&quot;, &quot;b&quot;, &quot;c&quot;)
	 * </pre>
	 * 
	 * becomes
	 * 
	 * <pre>
	 * &quot;a, b, c&quot;
	 * </pre>
	 * 
	 * @param delimiter
	 *            the delimiter to insert between each string.
	 * @param strings
	 *            the array of strings to concatenate.
	 * @return the delimiter-concatenated string. If <tt>null</tt> is given as input strings, <tt>null</tt> is
	 *         returned.
	 */
	public static String join(final String delimiter, final String... strings) {
		if( strings == null ) { return null; }
		if( strings.length == 0 ) { return ""; }
		final StringBuilder sb = new StringBuilder();
		
		// add first 1.. n-1
		final int len = strings.length - 1;
		int i = 0;
		for(; i < len; i++) {
			sb.append(strings[i]);
			sb.append(delimiter);
		}
		// add last
		sb.append(strings[i]);
		return sb.toString();
	}
	
	/**
	 * Remove all occurrences of all specified characters.
	 * <p>
	 * This is an easy way to e.g. remove all formatting chars such as ' ', '\t', '\n' from Strings for easy comparison
	 * of code generated String.
	 * 
	 * @param baseString
	 *            the string to trim
	 * @param removeChars
	 *            the characters to remove
	 * @return a string with the specified characters removed.
	 */
	public static String removeAll(final String baseString, final Character... removeChars) {
		final StringBuffer resultString = new StringBuffer();
		// cache for quick lookup
		final HashMap<Character, Object> charMatchMap = new HashMap<Character, Object>();
		for(final Character c : removeChars) {
			charMatchMap.put(c, c);
		}
		
		for(int i = 0; i < baseString.length(); i++) {
			final char c = baseString.charAt(i);
			if( charMatchMap.containsKey(c) == false ) {
				resultString.append(c);
			}
		}
		return resultString.toString();
	}
	
	private static String repeatJoin(final String baseString, final int endSize, final boolean isLeftJoin,
			final String... joinStrings) {
		
		if( baseString == null ) { throw new IllegalArgumentException("Argument baseString is null"); }
		
		if( joinStrings == null ) { throw new IllegalArgumentException("argument joinStrings is null"); }
		
		if( joinStrings.length == 0 ) { return baseString; }
		
		if( endSize < baseString.length() ) { throw new IllegalArgumentException(
				"Argument endSize is less than the length of baseString"); }
		// appending as many times possible
		final int resultingSize = endSize - baseString.length();
		final StringBuilder joiningStrings = new StringBuilder(resultingSize);
		int i = 0;
		while(true) {
			if( joiningStrings.length() + joinStrings[i].length() <= resultingSize ) {
				joiningStrings.append(joinStrings[i]);
			} else {
				break;
			}
			i = (i + 1) % joinStrings.length;// round-robin the joinStrings
		}
		
		// final join
		if( isLeftJoin ) {
			joiningStrings.append(baseString);
		} else {
			joiningStrings.insert(0, baseString);
		}
		
		return joiningStrings.toString();
	}
	
	/**
	 * Create a string by repeatedly joining/appending one or more strings in front of the string until the resulting
	 * string is equal to or as close possible to some length. E.g. * <code>
	 * repeatLeftJoin(&quot;100&quot;, 6, &quot;#&quot;));
	 * </code>
	 * yields <code>&quot;fully fill&quot;, &quot;###100&quot;</code>
	 *
	 *  <code>
	 * repeatLeftJoin(&quot;100&quot;, 6, &quot;##&quot;));
	 * </pre>
	 *
	 * Yields <code> &quot;##100&quot;,</code>
	 *
	 * @param baseString
	 *            the string to join strings onto
	 * @param endSize
	 *            the resulting size
	 * @param joinStrings
	 *            one or more strings to join onto the basestring. If the empty list is given, the baseString is returned.
	 * @return a joined string
	 */
	public static String repeatLeftJoin(final String baseString, final int endSize, final String... joinStrings) {
		return repeatJoin(baseString, endSize, true, joinStrings);
	}
	
	/**
	 * Create a string by repeatedly joining/appending one or more strings at the end of the string until the resulting
	 * string is equal to or as close possible to some length. E.g.
	 * 
	 * <pre>
	 * repeatLeftJoin(&quot;100&quot;, 6, &quot;#&quot;));
	 * </pre>
	 * 
	 * Yields <code>&quot;fully fill&quot;, &quot;###100&quot;</code>
	 *
	 *  <pre>
	 * repeatLeftJoin(&quot;100&quot;, 6, &quot;##&quot;));
	 * </pre>
	 * 
	 * Yields <code> &quot;##100&quot;,</code>
	 * 
	 * @param baseString
	 *            the string to join strings onto
	 * @param endSize
	 *            the resulting size
	 * @param joinStrings
	 *            one or more strings to join onto the basestring. If the empty list is given, the baseString is
	 *            returned.
	 * @return a joined string
	 */
	public static String repeatRightJoin(final String baseString, final int endSize, final String... joinStrings) {
		return repeatJoin(baseString, endSize, false, joinStrings);
	}
	
	/**
	 * Returns a string representation of any throwable (e.g. exceptions)
	 * 
	 * @param throwable
	 *            the throwable/exception to get a representation from
	 * @return a string representing the throwable
	 * @since 0.1
	 */
	public static String toString(final Throwable throwable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return result.toString();
	}
	
}
