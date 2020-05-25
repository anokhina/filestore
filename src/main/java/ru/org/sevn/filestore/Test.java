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
package ru.org.sevn.filestore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author Veronica Anokhina
 */
public class Test {
    
    private static File getFileTo(final File dir, final Path srcPath, final Path destPath, final String newName) {
        final String dirPath = dir.getAbsolutePath();
        final String baseDirPath = dirPath.substring(0, dirPath.length() - srcPath.toString().length());
        System.err.println(">>----" + baseDirPath+":"+srcPath+":"+destPath);
        final File baseDir = new File(baseDirPath);
        return new File (new File(baseDir, destPath.toString()), newName);
    }

    public static void main(String[] args) throws Exception {
        File f = new File("/mnt/dav/test/Media/backup/out/df.png");
        System.out.println(">>>>>>>>>" + f.exists());
        /*
        File d1 = new File("zzz1/zzz11");
        File d2 = new File("zzz2/zzz22");
        d1.mkdirs();
        d2.mkdirs();
        final File f = getFileTo(d2, Paths.get("zzz1", "zzz11"), Paths.get("zzz2", "zzz22"), "zzz3");
        System.out.println(">>>>>>>>>" + f);
        
    	File file = d1;
    	long totalSpace = file.getTotalSpace(); //total disk space in bytes.
    	long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
    	long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.

    	System.out.println(" === Partition Detail ===");

    	System.out.println(" === bytes ===");
    	System.out.println("Total size : " + totalSpace + " bytes");
    	System.out.println("Space free : " + usableSpace + " bytes");
    	System.out.println("Space free : " + freeSpace + " bytes");

    	System.out.println(" === mega bytes ===");
    	System.out.println("Total size : " + totalSpace /1024 /1024 + " mb");
    	System.out.println("Space free : " + usableSpace /1024 /1024 + " mb");
    	System.out.println("Space free : " + freeSpace /1024 /1024 + " mb");        
        */
    }
}
