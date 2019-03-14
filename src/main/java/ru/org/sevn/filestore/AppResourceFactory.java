/*
 * Copyright 2019 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.filestore;

import io.milton.common.Path;
import io.milton.http.HttpManager;
import io.milton.http.ResourceFactory;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.Resource;
import java.io.File;
import ru.org.sevn.filestore.resource.RootFileResource;

public class AppResourceFactory implements ResourceFactory {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ResourceFactory.class);
    public static final String FILE_PATH = "fileStore";
    public static final String ATTR_ROOT = "rootResource";

    @Override
    public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {
        log.debug("getResource: url: " + url);
        final Path path = Path.path(url);
        final Resource r = find(path);
        log.debug("found: " + r + " url: " + url + " path: " + path);
        return r;
    }

    private Resource find(Path path) throws NotAuthorizedException, BadRequestException {
        if (path.isRoot()) {
            RootFileResource r = (RootFileResource) HttpManager.request().getAttributes().get(ATTR_ROOT);
            if( r == null ) {
                final File storeDir = new File(FILE_PATH); //TODO
                log.debug("store dir: " + storeDir.getAbsolutePath());
                storeDir.mkdirs(); //TODO
                r = new RootFileResource(storeDir);
                HttpManager.request().getAttributes().put(ATTR_ROOT, r);
            }
            
            return r;
        }
        final Resource parent = find(path.getParent());
        if (parent instanceof CollectionResource) {
            final CollectionResource folder = (CollectionResource) parent;
            return folder.child(path.getName());
        }
        return null;
    }    
}
