/*
 * SoapUI IntelliJ Plugin, copyright (C) 2006-2011 smartbear.com
 *
 * soapUI is free software; you can redistribute it and/or modify it under the
 * terms of version 2.1 of the GNU Lesser General Public License as published by
 * the Free Software Foundation.
 *
 * soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details at gnu.org.
 */

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

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.support.UISupport;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class AboutSoapUIAction extends AnAction
{
   public void actionPerformed( AnActionEvent e )
   {
      URI splashURI = null;
      try
      {
         String str = UISupport.findSplash( SoapUI.SOAPUI_SPLASH ).toString();
         str = str.replaceAll( " ", "%20" );
         splashURI = new URI( str );
      }
      catch( URISyntaxException e1 )
      {
         SoapUI.logError( e1 );
      }

      Properties props = new Properties();
      try
      {
         props.load( SoapUI.class.getResourceAsStream( SoapUI.BUILDINFO_PROPERTIES ) );
      }
      catch( Exception e1 )
      {
         SoapUI.logError( e1 );
      }

      UISupport.showExtendedInfo( "About soapUI Plugin", null,
              "<html><body><p align=center><img src=\"" + splashURI + "\"><br>soapUI " +
                      SoapUI.SOAPUI_VERSION + " IntelliJ Plug-in, copyright (C) 2004-2009 eviware.com<br>" +
                      "<a href=\"http://www.soapui.org\">http://www.soapui.org</a> | " +
                      "<a href=\"http://www.eviware.com\">http://www.eviware.com</a><br>" +
                      "Core Build " + props.getProperty( "build.number" ) + ", Build Date " +
                      props.getProperty( "build.date" ) + "</p></body></html>",

              new Dimension( 470, 360 ) );
   }
}
