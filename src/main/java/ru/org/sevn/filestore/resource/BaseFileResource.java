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
import io.milton.resource.DeletableResource;
import java.io.File;

public abstract class BaseFileResource<T extends BaseFileResource> extends AbstractResource 
        implements DeletableResource
{
    
    private final T parent;
    private final File file;
    
    public BaseFileResource(final T parent, final File file) {
        this.parent = parent;
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }

    public T getParent() {
        return parent;
    }
    
    @Override
    public String getName() {
        return file.getName();
    }
    
    protected abstract void deletedResource();
    
    @Override
    public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        try {
            if (!getFile().delete()) {
                throw new BadRequestException(this);
            } else {
                deletedResource();
            }
        } catch (final SecurityException ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }

}
