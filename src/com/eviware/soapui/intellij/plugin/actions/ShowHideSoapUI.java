/*
 *  SoapUI IntelliJ Plugin, copyright (C) 2006-2009 eviware.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.eviware.soapui.intellij.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;
import com.eviware.soapui.intellij.plugin.SoapUIPlugin;

/**
 * @author Konstantin Bulenkov
 */
public class ShowHideSoapUI extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = DataKeys.PROJECT.getData(e.getDataContext());

        assert project != null;

        SoapUIPlugin plugin = (SoapUIPlugin) project.getComponent(SoapUIPlugin.NAME);
        if (isSoupUIPresent(project)) {
            plugin.unregisterToolWindow();
        } else {
            if (! plugin.isInitialized()) {
                plugin.init();                
            } else {
                plugin.initToolWindow();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Project project = DataKeys.PROJECT.getData(e.getDataContext());
        e.getPresentation().setVisible(project != null);
        if (project == null) return;
        String text = (isSoupUIPresent(project) ? "Close" : "Start") + " SoapUI";
        e.getPresentation().setText(text);
    }

    private boolean isSoupUIPresent(Project project) {
        return ToolWindowManager.getInstance(project)
                .getToolWindow(SoapUIPlugin.WORKSPACE_WINDOW_ID) != null;
    }
}
