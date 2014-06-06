/*
 *  SoapUI IntelliJ Plugin, copyright (C) 2006-2011 smartbear.com
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

package com.eviware.soapui.intellij.plugin;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.intellij.testFramework.LightVirtualFile;

/**
 * VirtualFile for a SoapUI ModelItem
 */

public class SoapUIVirtualFile extends LightVirtualFile
{
   private DesktopPanel desktopPanel;

   public SoapUIVirtualFile( DesktopPanel desktopPanel )
   {
      super( ( desktopPanel.getModelItem() == null ? desktopPanel.getTitle() : desktopPanel.getModelItem().getName() ),
              SoapUIEditorProvider.SOAPUI_FILE_TYPE, "" );
      this.desktopPanel = desktopPanel;
   }

   public ModelItem getModelItem()
   {
      return desktopPanel.getModelItem();
   }

   public DesktopPanel getDesktopPanel()
   {
      return desktopPanel;
   }
}
