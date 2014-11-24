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

package org.craftercms.social.migration.migrators;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.craftercms.social.migration.MigrationTool;
import org.craftercms.social.migration.mongo.MongoConnection;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.craftercms.social.migration.util.scripting.PrimitiveWrapFactory;
import org.craftercms.social.migration.util.scripting.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ScriptingEngine {

    private static ScriptingEngine instance;
    private final Context engine;
    private final ScriptableObject scope;

    private Logger log = LoggerFactory.getLogger(ScriptingEngine.class);
    public ScriptingEngine(final MigrationMessenger messenger) {
        // Create and enter a Context. A Context stores information about the execution environment of a script.
        engine = Context.enter();
        engine.setWrapFactory(new PrimitiveWrapFactory());
        scope = new ImporterTopLevel(engine);
        final MongoConnection connection = MongoConnection.getInstance();
        ScriptableObject.putProperty(scope, "source", Context.javaToJS(connection.getSource(), scope));
        ScriptableObject.putProperty(scope, "destination", Context.javaToJS(connection.getDestination(), scope));
        ScriptableObject.putProperty(scope, "props", Context.javaToJS(MigrationTool.systemProperties, scope));
        ScriptableObject.putProperty(scope, "utils", Context.javaToJS(new ScriptUtils(), scope));
        ScriptableObject.putProperty(scope, "log", Context.javaToJS(log, scope));
        ScriptableObject.putProperty(scope, "messenger", Context.javaToJS(messenger, scope));

    }

    public void eval(File script) throws MigrationException {
        try {

            final FileReader scriptInput = new FileReader(script);
            engine.evaluateReader(scope, scriptInput, script.getName(), 0, null);
            scriptInput.close();//
        } catch (EvaluatorException | IOException e) {
            log.error("Error while executing file " + script.getPath(), e);
            throw new MigrationException(e, true);
        } catch (Exception ex) {
            log.error("Error while executing file " + script.getPath(), ex);
            throw new MigrationException(ex, false); // For all Other
        }
    }
}
