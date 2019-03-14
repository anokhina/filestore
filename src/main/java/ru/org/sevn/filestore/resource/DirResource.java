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
package ru.org.sevn.filestore.resource;

import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.PutableResource;
import io.milton.resource.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class DirResource extends BaseFileDirResource 
        implements 
        CollectionResource, 
        MakeCollectionableResource, 
        MoveableResource,
        PutableResource
{

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DirResource.class);

    public DirResource(final DirResource parent, final File file) {
        super(parent, file);
    }
    
    private final Map<String, Resource> children = new LinkedHashMap<>();
    
    @Override
    public List<? extends Resource> getChildren() {
        retriveChildren();
        return new ArrayList(children.values());
    }
    
    protected Map<String, Resource> retriveChildren() {
        if (children.size() == 0) {
            for (final File f : getFile().listFiles()) {
                final Resource res;
                if (f.isDirectory()) {
                    res = new DirResource(this, f);
                } else {
                    res = new FileResource(this, f);
                }
                children.put(res.getName(), res);
            }
        }
        return children;
    }
    
    protected <T extends Resource> T addResource(final T res) {
        children.put(res.getName(), res);
        return res;
    }

    @Override
    public CollectionResource createCollection(final String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        final File newDir = new File(getFile(), newName);
        try {
            if (newDir.mkdirs()) {
                return addResource(new DirResource(this, newDir));
            } else {
                throw new BadRequestException(this);
            }
        } catch (final SecurityException ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }

    @Override
    public Resource child(final String childName) {
        return retriveChildren().get(childName);
    }

    @Override
    public Resource createNew(final String name, final InputStream in, final Long length, final String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
        final File newFile = new File(getFile(), name);
        try {
            Files.copy(in, newFile.toPath());
            return addResource(new FileResource(this, newFile));
        } catch (final Throwable ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }

}
