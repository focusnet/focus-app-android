package eu.focusnet.app.model.internal.fields;

import com.google.gson.internal.LinkedTreeMap;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class TextfieldFieldInstance extends FieldInstance
{

	public enum ValidType
	{
		TEXTFIELD_TYPE_TEXT("text"),
		TEXTFIELD_TYPE_EMAIL("email"),
		TEXTFIELD_TYPE_DECIMAL("decimal"),
		TEXTFIELD_TYPE_NUMBER("number"),
		TEXTFIELD_TYPE_PHONE("phone");

		private String text;

		ValidType(String text)
		{
			this.text = text;
		}

		public static ValidType fromString(String text)
		{
			if (text != null) {
				for (ValidType b : ValidType.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
			// fallback to generic type
			return TEXTFIELD_TYPE_TEXT;
		}

		@Override
		public String toString()
		{
			return this.text;
		}
	}

	private static final String TEXTFIELD_LABEL_INPUT_TYPE = "input-type";
	protected ValidType inputType;
	private static final String TEXTFIELD_LABEL_DECIMALS_NUMBER = "decimals-number";
	private static final int TEXTFIELD_DEFAULT_DECIMALS_NUMBER = 2;
	private int decimalsNumber;

	public TextfieldFieldInstance(String fieldName, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		super(fieldName, config, dataContext);
	}

	@Override
	protected void processSpecificConfig()
	{
		this.inputType = ValidType.TEXTFIELD_TYPE_TEXT;

		Object rawInputType = this.config.get(TEXTFIELD_LABEL_INPUT_TYPE);
		if (rawInputType == null) {
			this.inputType = ValidType.TEXTFIELD_TYPE_TEXT;
		}
		else {
			try {
				this.inputType = ValidType.fromString(TypesHelper.asString(rawInputType));
			}
			catch (FocusBadTypeException ex) {
				this.markAsInvalid();
			}
		}

		this.decimalsNumber = TEXTFIELD_DEFAULT_DECIMALS_NUMBER;
		Object rawDecimalsNumbers = this.config.get(TEXTFIELD_LABEL_DECIMALS_NUMBER);
		if (rawDecimalsNumbers != null) {
			try {
				this.decimalsNumber = (int) Double.parseDouble(TypesHelper.asString(rawDecimalsNumbers));
			}
			catch (FocusBadTypeException ex) {
				this.markAsInvalid();
			}
		}
	}

	public ValidType getInputType()
	{
		return this.inputType;
	}


	public int getDecimalsNumber()
	{
		return this.decimalsNumber;
	}

}
