/**
 *
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
 *
 */

package eu.focusnet.app.ui.util;

/**
 *
 */
public class NavigationUtil
{

	public static PathType checkPathType(String path)
	{
		String[] parts = path.split("\\|");
		PathType pathType = null;
		switch (parts.length) {
			case 1:
				pathType = PathType.PROJECT_ID;
				break;
			case 3:
				pathType = PathType.PROJECT_ID_PAGE_ID;
				break;
		}

		return pathType;

//        if (parts.length >= 1) {
//            return this.projects.get(parts[0]);
//        }
//        return null;// exception instead ?
//
//        if (path.contains("[") && path.contains("]") && path.contains("/")) {
//            return PathType.PROJECT_ID_BRACKETS_PAGE_ID;
//        } else if (path.contains("[") && path.contains("]")) {
//            return PathType.PROJECT_ID_BRACKETS;
//        } else if (path.contains("/")) {
//            return PathType.PROJECT_ID_PAGE_ID;
//        }
//        return PathType.PROJECT_ID;
	}

	public enum PathType
	{
		PROJECT_ID, PROJECT_ID_PAGE_ID
	}
}
