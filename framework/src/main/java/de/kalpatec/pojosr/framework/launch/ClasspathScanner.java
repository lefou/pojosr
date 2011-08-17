/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.kalpatec.pojosr.framework.launch;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.felix.framework.util.MapToDictionary;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;

public class ClasspathScanner
{
    public List<BundleDescriptor> scanForBundles() throws Exception
    {
        return scanForBundles(null);
    }

    public List<BundleDescriptor> scanForBundles(String filterString)
            throws Exception
    {
        Filter filter = (filterString != null) ? FrameworkUtil
                .createFilter(filterString) : null;
        List<BundleDescriptor> bundles = new ArrayList<BundleDescriptor>();
        for (Enumeration<URL> e = getClass().getClassLoader().getResources(
                "META-INF/MANIFEST.MF"); e.hasMoreElements();)
        {
            URL manifestURL = e.nextElement();
            InputStream input = null;
            try
            {
                input = manifestURL.openStream();
                Attributes attributes = new Manifest(input).getMainAttributes();
                Map<String, String> headers = new HashMap<String, String>();
                for (Object key : attributes.keySet())
                {
                    headers.put(key.toString(),
                            attributes.getValue(key.toString()));
                }
                if ((filter == null)
                        || filter.match(new MapToDictionary(headers)))
                {
                    bundles.add(new BundleDescriptor(getClass()
                            .getClassLoader(), getParentURL(manifestURL),
                            headers));
                }
            }
            finally
            {
                if (input != null)
                {
                    input.close();
                }
            }
        }
        return bundles;
    }

    private URL getParentURL(URL url) throws Exception
    {
        String externalForm = url.toExternalForm();
        return new URL(externalForm.substring(0, externalForm.length()
                - "META-INF/MANIFEST.MF".length()));
    }
}
