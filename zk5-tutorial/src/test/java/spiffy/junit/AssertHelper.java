package spiffy.junit;

import junit.framework.Assert;

import java.util.Collection;

/**
 * This class provides essential assertions missing from JUnit in order to create clear JUnit test cases.
 * 
 * @author Kasper B. Graversen
 */
public class AssertHelper extends Assert {

/**
 * Check if a collection is empty
 * 
 * @param collection
 */
public static <T> void assertEmpty(final String message, final Collection<T> collection) {
	if( collection == null ) {
		fail(message + "[collection is null]");
	}
	if( collection.size() != 0 ) {
		fail("Collection " + collection + " is not empty!");
	}
}

/**
 * Check if two arrays element for element are equal. If not an AssertionFailedError is thrown. When both argument
 * arrays are <tt>null</tt>, the method silently returns.
 */
public static void assertEquals(final String message, final byte[] expected, final byte[] actual) {
	if( expected == null && actual == null ) { return; }
	if( expected == null || actual == null ) {
		fail(message + " [Expected: " + expected + ", actual " + actual + ']');
	}
	assertEquals(message + " [Length]", expected.length, actual.length);
	for( int i = 0; i < expected.length; i++ ) {
		assertEquals(message + " [" + i + ']', expected[i], actual[i]);
	}
}

/**
 * Check if two arrays element for element are equal. If not an AssertionFailedError is thrown. When both argument
 * arrays are <tt>null</tt>, the method silently returns.
 */
public static void assertEquals(final String message, final int[] expected, final int[] actual) {
	if( expected == null && actual == null ) { return; }
	if( expected == null || actual == null ) {
		fail(message + " [Expected: " + expected + ", actual " + actual + ']');
	}
	assertEquals(message + " [Length]", expected.length, actual.length);
	for( int i = 0; i < expected.length; i++ ) {
		assertEquals(message + " [" + i + ']', expected[i], actual[i]);
	}
}

/**
 * Check if two arrays element for element are equal. If not an AssertionFailedError is thrown. When both argument
 * arrays are <tt>null</tt>, the method silently returns.
 */
public static void assertEquals(final String message, final Object[] expected, final Object[] actual) {
	if( expected == null && actual == null ) { return; }
	if( expected == null || actual == null ) {
		fail(message + " [Expected: " + expected + ", actual " + actual + ']');
	}
	assertEquals(message + " [Length]", expected.length, actual.length);
	for( int i = 0; i < expected.length; i++ ) {
		assertEquals(message + " [" + i + ']', expected[i], actual[i]);
	}
}

/**
 * check that the actual value is greater than the expected
 */
public static void assertGreaterThan(final String message, final double expectedGreaterThan, final double actual) {
	if( expectedGreaterThan >= actual ) {
		fail(message + ". Actual not greater than expected, " + expectedGreaterThan + " >= " + actual);
	}
}

/**
 * check that the actual value is greater than the expected
 */
public static void assertGreaterThan(final String message, final int expectedGreaterThan, final int actual) {
	if( expectedGreaterThan >= actual ) {
		fail(message + ". Actual not greater than expected, " + expectedGreaterThan + " >= " + actual);
	}
}

public static void assertNotEquals(final int expected, final int actual) {
	if( expected == actual ) {
		fail("The two values are equal! Expected: " + expected + " actual: " + actual);
	}
}

/**
 * check that the actual value is not the expected
 */
public static void assertNotEquals(final String message, final double expected, final double actual) {
	if( expected == actual ) {
		fail(message + ". The two values are equal! Expected: " + expected + " actual: " + actual);
	}
}

/**
 * check that the actual value is not the expected
 */
public static void assertNotEquals(final String message, final int expected, final int actual) {
	if( expected == actual ) {
		fail(message + ". The two values are equal! Expected: " + expected + " actual: " + actual);
	}
}

}
