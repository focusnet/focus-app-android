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

package eu.focusnet.app.model.widgets;

import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.WidgetTemplate;

/**
 * An instance containing all information pertaining to a GPS widget.
 */
public class GPSWidgetInstance extends DataCollectionWidgetInstance
{

	/**
	 * Variable holding the current latitude
	 */
	private double latitude;

	/**
	 * Variable holding the current longitude
	 */
	private double longitude;

	/**
	 * Variable holding the current altitude
	 */
	private double altitude;

	/**
	 * Variable holding the current precision
	 */
	private float accuracy;

	/**
	 * C'tor
	 *
	 * @param wTpl         Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx      Inherited
	 */
	public GPSWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}


	/**
	 * Nothing specific to be done for this widget instance
	 */
	@Override
	protected void processSpecificConfig()
	{

	}

	/**
	 * Save a new sample
	 */
	public void saveSample(double latitude, double longitude, double altitude, float accuracy)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
	}

	/**
	 * Reset instance variables.
	 */
	public void resetSample()
	{
		this.latitude = this.longitude = this.altitude = this.accuracy = 0;
	}
}
