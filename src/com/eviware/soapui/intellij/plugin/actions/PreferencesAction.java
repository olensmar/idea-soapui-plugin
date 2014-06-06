/*
 *  SSoapUI IntelliJ Plugin, copyright (C) 2006-2011 smartbear.com
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

import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.intellij.plugin.SoapUIPlugin;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class PreferencesAction extends AnAction
{
   public void actionPerformed( AnActionEvent e )
   {
      SoapUIPreferencesAction.getInstance().actionPerformed( null );
      SoapUIPlugin.initAutoSaveTimer();
   }
}
