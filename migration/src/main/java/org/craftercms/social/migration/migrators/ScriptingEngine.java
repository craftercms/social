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
import java.io.FileInputStream;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.craftercms.social.migration.MigrationTool;
import org.craftercms.social.migration.mongo.MongoConnection;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ScriptingEngine {

    private static ScriptingEngine instance;
    private final ScriptEngine engine;
    private Logger log = LoggerFactory.getLogger(ScriptingEngine.class);

    public static ScriptingEngine getInstance() {
        if (instance == null) {
            instance = new ScriptingEngine();
        }
        return instance;
    }

    private ScriptingEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        final MongoConnection connection = MongoConnection.getInstance();
        engine.put("source", connection.getSource());
        engine.put("destination", connection.getDestination());
        engine.put("messenger", MigrationMessenger.getInstance());
        engine.put("props", MigrationTool.systemProperties);
    }

    public void eval(File script) throws MigrationException {
        try {
            engine.put("log",LoggerFactory.getLogger(script.getName()));
            engine.eval(IOUtils.toString(new FileInputStream(script), "UTF-8"));
        } catch (ScriptException | IOException e) {
            log.error("Error while executing file "+script.getPath(),e);
            throw new MigrationException(e, true);
        } catch (Exception ex){
            throw new MigrationException(ex, false); // For all Other
        }
    }
}
