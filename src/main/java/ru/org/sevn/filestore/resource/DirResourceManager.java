/*
 * Copyright 2020 Veronica Anokhina.
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
import io.milton.resource.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Veronica Anokhina
 */
public class DirResourceManager {
    
    public static final String FILE_PATH = "fileStore";
    
    //TODO
    private final static DirResourceManager DRM = new DirResourceManager(
            "fileStore2", "fileStore1");
    
    private final File[] rootDirs;
    private final AtomicInteger activeIdx = new AtomicInteger(0);
    private final AtomicLong atomicLong = new AtomicLong(0);
    
    private DirResourceManager(final String... rootDirs) {
        this.rootDirs = new File[rootDirs.length];
        for (int i = 0; i < this.rootDirs.length; i++) {
            this.rootDirs[i] = new File(rootDirs[i]);
        }
        initDirs();
        selectActive();
    }
    private DirResourceManager(final Path... rootDirs) {
        this.rootDirs = new File[rootDirs.length];
        for (int i = 0; i < this.rootDirs.length; i++) {
            this.rootDirs[i] = rootDirs[i].toFile();
        }
        initDirs();
        selectActive();
    }
    
    private DirResourceManager(final File[] rootDirs) {
        this.rootDirs = rootDirs;
        initDirs();
        selectActive();
    }
    
    private void initDirs() {
        for (int i = 0; i < this.rootDirs.length; i++) {
            this.rootDirs[i].mkdirs();
        }        
    }
    private void selectActive() {
        long max = 0;
        int idx = 0;
        for (int i = 0; i < rootDirs.length; i++) {
            final long fs = rootDirs[i].getFreeSpace();
            if (fs > max) {
                max = fs;
                idx = i;
            }
        }
        activeIdx.set(idx);
        atomicLong.set(0);
    }
    
    private final RootFileResource rootFileResource = new RootFileResource();
    
    public RootFileResource getRootFileResource() {
        System.out.println(">>>>>>>>>>>getRootFileResource");
        return rootFileResource;
    }
    
    public Resource find(io.milton.common.Path path) throws NotAuthorizedException, BadRequestException {
        System.out.println(">>>>>>>>>>>find>" + path);
        if (path.isRoot()) {
            return getRootFileResource();
        } else {
            final Resource parent = find(path.getParent());
            if (parent instanceof CollectionResource) {
                final CollectionResource folder = (CollectionResource) parent;
                return folder.child(path.getName());
            }
            return null;
        }
    }
    
    public static DirResourceManager getDirResourceManager() {
        return DRM;
    }
    
    public File getNewLocation(final DirResource dr, final String name) {
        atomicLong.incrementAndGet();
        final File f = new File(rootDirs[this.activeIdx.get()], dr.getPath().toString());
        f.mkdirs();
        return new File(f, name);
    }
    
    public File getNewDirLocation(final DirResource dr, final String name) {
        atomicLong.incrementAndGet();
        final File f = new File(rootDirs[this.activeIdx.get()], dr.getPath().toString());
        final File res = new File(f, name);
        res.mkdirs();
        return res;
    }
    
    private File[] getFiles(final DirResource dir) {
        final Path p = dir.getPath();
        final String path = p.toString();
        final File[] res = new File[rootDirs.length];
        for(int i = 0; i < rootDirs.length; i++) {
            res[i] = new File(rootDirs[i], path);
        }
        return res;
    }
    
    public void deleteDir(final DirResource dir) throws NotAuthorizedException, ConflictException, BadRequestException {
        for (final File f : getFiles(dir)) {
            if (f.exists()) {
                if (!f.delete()) {
                    throw new BadRequestException(dir);
                }
            }
        }
    }
    
    public File getFile(final FileResource file) {
        for (File d : getFiles(file.getParent())) {
            final File f = new File(d, file.getName());
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }
    
    public void deleteFile(final FileResource dir) throws NotAuthorizedException, ConflictException, BadRequestException {
        final File f = getFile(dir);
        if (f != null) {
            if (!f.delete()) {
                throw new BadRequestException(dir);
            }
        }
    }
    
    private Resource makeRes(final DirResource dir, final File f) {
        if (f.isDirectory()) {
            return new DirResource(dir, f.getName());
        } else {
            return new FileResource(dir, f);
        }
    }
    
    public Resource retrieve(final DirResource dir, final String childName) {
        for(final File d : getFiles(dir)) {
            final File f = new File(d, childName);
            if (f.exists()) {
                return makeRes(dir, f);
            }
        }
        return null;
    }
    
    public void retrieveResources(final DirResource dir, Map<String, Resource> children) {
        for(final File d : getFiles(dir)) {
            if (d.exists() && d.isDirectory()) {
                for (final File f : d.listFiles()) {
                    children.put(f.getName(), makeRes(dir, f));
                }
            }
        }
    }
    
    private File getFileTo(final File dir, final DirResource src, final DirResource dest, final String newName) {
        return getFileTo(dir, src.getPath(), dest.getPath(), newName);
    }
    private File getFileTo(final File dir, final Path srcPath, final Path destPath, final String newName) {
        final String dirPath = dir.getAbsolutePath();
        final File baseDir = new File(dirPath.substring(0, dirPath.length() - srcPath.toString().length()));
        return new File (new File(baseDir, destPath.toString()), newName);
    }
    
    public void moveTo(final DirResource src, final DirResource dest, final String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        if (dest.getPath().toString().equals(src.getPath().toString())) {
            throw new BadRequestException(src);
        } else {
            if (dest.getPath().toString().contains(src.getPath().toString())) { //can't move to child dir
                throw new BadRequestException(src);
            } else {
                //newName
                final Resource r = retrieve(dest, newName);
                if (r != null) {
                    throw new BadRequestException(src);
                } else {
                    
                }
                for (final File dir : getFiles(src)) {
                    if (dir.exists()) {
                        final File f = getFileTo(dir, src, dest, newName);
                        try {
                            Files.move(dir.toPath(), f.toPath());
                        } catch (IOException ex) {
                            throw new BadRequestException(src);
                        }
                    }
                }
            }
        }
        
    }

    public void moveTo(final FileResource src, final DirResource dest, final String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        final Resource child = dest.child(newName);
        try {
            if (child == null) {
                final File f = getNewLocation(dest, newName);
                Files.move(src.getFile().toPath(), f.toPath());
            } else {
                if (child instanceof DirResource) {
                    throw new BadRequestException(src);
                } else {
                    //file exists
                    throw new BadRequestException(src);
                }
            }
        } catch (Exception ex) {
            throw new BadRequestException(src);
        }

        src.setName(newName);
    }
    
}
