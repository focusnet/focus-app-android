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

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

//http://binkley.blogspot.fr/2009/04/jumping-work-queue-in-executor.html
public final class PriorityTask<T> extends FutureTask<T> implements Comparable<PriorityTask<T>>
{
	private final int priority;

	public PriorityTask(final int priority, final Callable<T> tCallable)
	{
		super(tCallable);

		this.priority = priority;
	}

	public PriorityTask(final int priority, final Runnable runnable,
						final T result)
	{
		super(runnable, result);

		this.priority = priority;
	}

	@Override
	public int compareTo(final PriorityTask<T> o)
	{
		final long diff = o.priority - priority;
		return 0 == diff ? 0 : 0 > diff ? -1 : 1;
	}
}
