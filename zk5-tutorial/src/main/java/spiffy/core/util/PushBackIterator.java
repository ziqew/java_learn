package spiffy.core.util;

import java.util.Iterator;

/**
 * The PushBackIterator enables you to iterate and push back elements should you find that "you have come to far". You
 * can push elements back in the iterator which then will be re-visited upon the subsequent call to <tt>next()</tt>
 * 
 * @author Kasper B. Graversen
 */
public class PushBackIterator<T> implements Iterator<T> {
	/** the iterator we wrap */
	private final Iterator<T> iterator;
	
	/** when pushing back, fill this cache */
	private T pushBackCache = null;
	
	/** record last fetched element such that we know what to push back */
	private T lastFetchedElement = null;
	
	/**
	 * @param iterator
	 *            the iterator to wrap in order to get the push back functionality.
	 */
	public PushBackIterator(final Iterator<T> iterator) {
		this.iterator = iterator;
	}
	
	/**
	 * Returns true if the iteration has more elements. (In other words, returns true if next would return an element rather
	 * than throwing an exception.)
	 * 
	 * @return true if the iterator has more elements.
	 */
	public boolean hasNext() {
		return iterator.hasNext() || pushBackCache != null;
	}
	
	/**
	 * Returns the next element in the iteration. Calling this method repeatedly until the hasNext() method returns false
	 * will return each element in the underlying collection exactly once.
	 * 
	 * @return the next element in the iteration.
	 */
	public T next() {
		// if we have something in the cache.. use that
		if( pushBackCache != null ) {
			lastFetchedElement = pushBackCache;
			pushBackCache = null;
		} else {
			lastFetchedElement = iterator.next();
		}
		return lastFetchedElement;
	}
	
	/**
	 * Push back the last fetched element
	 */
	public void pushBack() {
		if( lastFetchedElement == null )
			throw new IllegalStateException(
					"next() must be called before pushBack(). Cannot push back non-existing element...");
		if( pushBackCache != null )
			throw new IllegalStateException("Cannot push back more than one object!");
		pushBackCache = lastFetchedElement;
	}
	
	/**
	 * Operation currently not supported
	 */
	public void remove() {
		throw new RuntimeException("Operation not supported yet...");
	}
}
