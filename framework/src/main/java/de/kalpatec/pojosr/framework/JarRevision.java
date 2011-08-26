/* 
 * Copyright 2011 Karl Pauls karlpauls@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kalpatec.pojosr.framework;

import java.io.*;
import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import org.apache.felix.framework.util.StringMap;

class JarRevision extends Revision
{
    private final long m_lastModified;
    private final JarFile m_jar;
    private final URL m_url;
	private final String m_urlString;

    public JarRevision(JarFile jar, URL url, long lastModified)
    {
        m_jar = jar;
        m_url = url;
		m_urlString =  m_url.toExternalForm();
        if (lastModified > 0)
        {
            m_lastModified = lastModified;
        }
        else
        {
            m_lastModified = System.currentTimeMillis();
        }
    }

    @Override
    public long getLastModified()
    {
        return m_lastModified;
    }

    public Enumeration getEntries()
    {
        return new EntriesEnumeration(m_jar.entries());
    }

    @Override
    public URL getEntry(String entryName)
    {
        try
        {
		    if("/".equals(entryName) || "".equals(entryName) || " ".equals(entryName)) {
			    return new URL("jar:" + m_url.toExternalForm() + "!/");
			}
            if (entryName != null)
			{ 
				entryName = ((entryName.startsWith("/")) ? entryName.substring(1) : entryName);
				final JarEntry entry = m_jar.getJarEntry(entryName);
				if ( entry != null) {
								 URL result = new URL("jar:" +  m_urlString + "!/" + entryName);
								 return result;
            }
			}
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
            return null;

    }

}
