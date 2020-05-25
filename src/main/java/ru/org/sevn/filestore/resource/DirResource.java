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


public class DirResource extends BaseFileResource<DirResource>
        implements 
        FolderResource,
        CollectionResource, 
        MakeCollectionableResource, 
        MoveableResource,
        PutableResource
{

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DirResource.class);

    public DirResource(final DirResource parent, final String name) {
        super(parent, name);
    }
    
    //private final Map<String, Resource> children = new LinkedHashMap<>();
    
    @Override
    public List<? extends Resource> getChildren() {
        System.out.println(">>>>>>>children>"+getPath());
        final Map<String, Resource> children = new LinkedHashMap<>();
        getDirResourceManager().retrieveResources(this, children);
        return new ArrayList(children.values());
    }
    
    protected <T extends Resource> T addChildResource(final T res) {
        //children.put(res.getName(), res);
        return res;
    }
    
    @Override
    public void removeChildResource(final String name) {
        //children.remove(name);
    }

    @Override
    public CollectionResource createCollection(final String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        getDirResourceManager().getNewDirLocation(this, newName);
        return addChildResource(new DirResource(this, newName));
    }

    private Resource getChild(final String name) {
        //return children.get(childName);
        return null;
        
    }
    @Override
    public Resource child(final String childName) {
        System.out.println(">>>>>>>child>" + childName);
        final Resource res = getChild(childName);
        if (res == null) {
            return getDirResourceManager().retrieve(this, childName);
        } else {
            return res;
        }
    }

    @Override
    public Resource createNew(final String name, final InputStream in, final Long length, final String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
        final File newFile = getDirResourceManager().getNewLocation(this, name);
        try {
            Files.copy(in, newFile.toPath());
            return addChildResource(new FileResource(this, newFile));
        } catch (final Throwable ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }

    @Override
    public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        getDirResourceManager().deleteDir(this);
        getParent().removeChildResource(getName());
    }

    @Override
    public void moveTo(final CollectionResource dest, final String name) throws ConflictException, NotAuthorizedException, BadRequestException {
        final String oldname = getName();
        final DirResource distdir = (DirResource)dest;
        getDirResourceManager().moveTo(this, distdir, name);
        getParent().removeChildResource(oldname);
        distdir.addChildResource(this);
    }
    
}
