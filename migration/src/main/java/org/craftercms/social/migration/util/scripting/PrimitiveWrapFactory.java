/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.migration.util.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * Created by Carlos Ortiz on 11/20/14.
 */
public class PrimitiveWrapFactory extends WrapFactory {
    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj;
        } else if (obj instanceof Character) {
            char[] a = {((Character)obj).charValue()};
            return new String(a);
        } else if (obj instanceof String) {
            return new String((String)obj);
        }
        return super.wrap(cx, scope, obj, staticType);
    }
}
