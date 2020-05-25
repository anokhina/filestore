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

import io.milton.resource.DeletableResource;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseFileResource<T extends FolderResource> extends AbstractResource 
        implements DeletableResource
{
    
    private final T parent;
    private String name;
    
    public BaseFileResource(final T parent, final String name) {
        this.parent = parent;
        this.name = name;
    }
    
    public T getParent() {
        return parent;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public Path getPath() {
        if (this.getParent() == null) {
            return Paths.get(getName());
        } else {
            return Paths.get(getParent().getPath().toString(), getName());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public DirResourceManager getDirResourceManager() {
        return DirResourceManager.getDirResourceManager();
    }
}
