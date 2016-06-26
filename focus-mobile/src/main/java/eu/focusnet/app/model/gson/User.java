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

package eu.focusnet.app.model.gson;

import eu.focusnet.app.controller.UserManager;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusInternalErrorException;

/**
 * A User object as stored in the FOCUS backend. This object is a template, and its actual
 * instantiation can be found in {@link eu.focusnet.app.model.UserInstance}, even though these
 * objects are very similar.
 * <p/>
 * Refer to JSON Schema for further documentation.
 * See https://github.com/focusnet/focus-data-mode
 */
public class User extends FocusObject
{

	private String firstName,
			lastName,
			email,
			company;


	public User(String targetUrl, String firstName, String lastName, String email, String company, UserManager userManager)
	{
		// we expicitely give the owner and editor, such that the FocusObject contructor does not need
		// to call DataManager getUser(). We may be in the case where we try to create the User
		super(Constant.DataModelTypes.FOCUS_DATA_MODEL_TYPE_USER, targetUrl, targetUrl, targetUrl, 1, null, null, true);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.company = company;
		if (userManager != null) {
			this.owner = userManager.getUserIdentification();
			this.editor = userManager.getUserIdentification();
		}
		else {
			throw new FocusInternalErrorException("User must have been identified first.");
		}
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public String getCompany()
	{
		return company;
	}

	public String toString()
	{
		return this.getUrl();
	}
}
