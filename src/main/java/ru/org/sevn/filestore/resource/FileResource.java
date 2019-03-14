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

import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.CollectionResource;
import io.milton.resource.CopyableResource;
import io.milton.resource.DeletableResource;
import io.milton.resource.GetableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.ReplaceableResource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class FileResource extends BaseFileDirResource implements GetableResource, ReplaceableResource, MoveableResource, CopyableResource, DeletableResource{

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileResource.class);

    public FileResource(final DirResource parent, final File file) {
        super(parent, file);
    }

    @Override
    public void copyTo(final CollectionResource toCollection, final String name) throws NotAuthorizedException, BadRequestException, ConflictException {
        if (toCollection instanceof DirResource) {
            final DirResource dirResource = (DirResource)toCollection;
            final File newFile = new File(dirResource.getFile(), name);
            try {
                Files.copy(getFile().toPath(), newFile.toPath());
            } catch (IOException ex) {
                throw new NotAuthorizedException(this, ex);
            }
        } else {
            throw new BadRequestException(this);
        }
    }

    
    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException {
        //TODO range
        Files.copy(getFile().toPath(), out);
    }

    @Override
    public void replaceContent(final InputStream in, final Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        try {
            Files.copy(in, getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new NotAuthorizedException(this, ex);
        }
    }    
    
    @Override
    public Long getContentLength() {
        return getFile().length();
    }

    @Override
    public String getContentType(final String accept) {
        try {
            return Files.probeContentType(getFile().toPath());
        } catch (IOException ex) {
            //TODO
            ex.printStackTrace();
        }
        return null;
        //return "text/plain";
    }

    @Override
    public Long getMaxAgeSeconds(final Auth auth) {
        return null;
    }

}
