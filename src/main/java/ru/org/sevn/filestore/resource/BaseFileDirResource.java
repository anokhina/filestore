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
import io.milton.resource.MoveableResource;
import java.io.File;

public abstract class BaseFileDirResource extends BaseFileResource<DirResource> implements MoveableResource {

    public BaseFileDirResource(final DirResource parent, final File file) {
        super(parent, file);
    }
    
    @Override
    protected void deletedResource() {
        getParent().retriveChildren().remove(getName());
    }

    private boolean isParent(final CollectionResource collectionResource) {
        if (collectionResource instanceof BaseFileResource) {
            final BaseFileResource baseFileResource = (BaseFileResource)collectionResource;
            return getParent().getFile().equals(baseFileResource.getFile());
        }
        return false;
    }

    protected void movedResource(final DirResource dest, final String name) {
        getParent().retriveChildren().remove(getName());
        dest.retriveChildren().put(name, new FileResource(dest, new File(dest.getFile(), name)));
    }

    @Override
    public void moveTo(final CollectionResource dest, final String name) throws ConflictException, NotAuthorizedException, BadRequestException {
        if (isParent(dest)) {
            moveTo(getParent().getFile(), getParent(), name);
        } else {
            if (dest instanceof DirResource) {
                final DirResource newParent = (DirResource)dest;
                if (newParent.getFile().isDirectory()) {
                    moveTo(newParent.getFile(), newParent, name);
                } else {
                    throw new BadRequestException(this);
                }
            }
        }
    }
    
    private void moveTo(final File dir, final DirResource dest, final String name) throws NotAuthorizedException {
        try {
            getFile().renameTo(new File(dir, name));
            movedResource(dest, name);
        } catch (final SecurityException ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }    
}
