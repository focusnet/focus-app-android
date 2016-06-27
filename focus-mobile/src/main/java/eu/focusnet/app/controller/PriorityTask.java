/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.controller;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


/**
 * A priority task for our custom priority-based pool
 *
 * See http://binkley.blogspot.fr/2009/04/jumping-work-queue-in-executor.html

 * @param <T> The response to {@code call()}.
 */
public final class PriorityTask<T> extends FutureTask<T> implements Comparable<PriorityTask<T>>
{
	/**
	 * The priority. The higher it is, the sooner the task will be run.
	 */
	private final int priority;

	/**
	 * Constructor for use with {@code Callable}
	 *
	 * @param priority The priority
	 * @param tCallable The {@code Callable}
	 */
	public PriorityTask(final int priority, final Callable<T> tCallable)
	{
		super(tCallable);

		this.priority = priority;
	}
/*

FIXME remove
	public PriorityTask(final int priority, final Runnable runnable,
						final T result)
	{
		super(runnable, result);

		this.priority = priority;
	}
*/
	/**
	 * Comparision method
	 * @param o The object to compare to
	 * @return {@code 0} if the current object and the campared object have the same priority,
	 * {@code -1} if the other object has higher priority and {@code +1} if the current object
	 * has higher priority.
	 *
	 * FIXME check that priority comparison is in correct order with a simple test.
 	 */
	@Override
	public int compareTo(@NonNull final PriorityTask<T> o)
	{
		final long diff = this.priority - o.priority;
		return 0 == diff ? 0 : (diff > 0 ? +1 : -1) ;
	}
}
