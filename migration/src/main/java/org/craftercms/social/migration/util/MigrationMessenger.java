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

package org.craftercms.social.migration.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 */
public final class MigrationMessenger {
    public enum Level{
        INFO(""),WARNING("warning"),ERROR("danger"),TASK_START("info"),TASK_END("success");
        private String cssClass;

        Level(final String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }
    }
    private static MigrationMessenger instance;
    private final WebView textArea;

    public static MigrationMessenger getInstance(WebView textArea){
        if(instance==null){
            instance=new MigrationMessenger(textArea);
        }
        return instance;
    }
    public static MigrationMessenger getInstance(){
        if(instance==null){
           throw new IllegalStateException("Instance haven't been initialize correctly");
        }
        return instance;
    }


    private MigrationMessenger(final WebView log) {
        this.textArea = log;
    }

    public void log(final String level,final String message,final String source){
        log(Level.valueOf(level),message,source);
    }
    public void clear(){
        final Document doc = textArea.getEngine().getDocument();
        final Element root = doc.getElementById("logs");
        final NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            root.removeChild(list.item(i));
        }
    }
    public void log(final Level level, final String message,final String msgSource) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Document doc = textArea.getEngine().getDocument();
                String dateFormat = new SimpleDateFormat("yyyy MM dd hh:mm:ss zzz").format(new Date());
                final Element tr = doc.createElement("tr");
                tr.setAttribute("class",level.getCssClass());
                final Element tmigrator = doc.createElement("td");
                final Element tdate = doc.createElement("td");
                final Element tmessage = doc.createElement("td");
                tmessage.setAttribute("class","text-center");
                tmessage.setTextContent(message);
                tdate.setTextContent(dateFormat);
                tmigrator.setTextContent(msgSource);
                tr.appendChild(tmigrator);
                tr.appendChild(tdate);
                tr.appendChild(tmessage);
                doc.getElementById("logs").appendChild(tr);

            }
        });
    }
}