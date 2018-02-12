package com.davidbracewell.io.resource;

import com.davidbracewell.string.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author David B. Bracewell
 */
public class ZipResource extends BaseResource implements ReadOnlyResource {

   private ZipEntry entry;
   private ZipFile zipFile;

   ZipResource(String zipFile, String entry) {
      try {
         this.zipFile = new ZipFile(zipFile);
      } catch (IOException e) {
         e.printStackTrace();
      }
      if (StringUtils.isNotNullOrBlank(entry)) {
         this.entry = this.zipFile.getEntry(entry);
      }
   }

   @Override
   protected InputStream createInputStream() throws IOException {
      return zipFile.getInputStream(entry);
   }

   @Override
   public boolean exists() {
      return zipFile != null;
   }

   @Override
   public Resource getChild(String relativePath) {
      if (entry == null) {
         return new ZipResource(zipFile.getName(), relativePath);
      }
      return new ZipResource(zipFile.getName(), entry.getName() + "/" + relativePath);
   }

   @Override
   public Resource getParent() {
      return new ZipResource(zipFile.getName(), entry.getName());
   }

   @Override
   public String descriptor() {
      return zipFile.getName() + (entry == null ? "" : ":" + entry.getName());
   }

   @Override
   public List<Resource> getChildren() {
      Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
      List<Resource> resources = new ArrayList<>();
      final String prefix = entry == null ? "" : entry.getName();
      while (enumeration.hasMoreElements()) {
         ZipEntry ze = enumeration.nextElement();
         if (ze.getName().startsWith(prefix)) {
            resources.add(new ZipResource(zipFile.getName(), ze.getName()));
         }
      }
      return resources;
   }

}//END OF ZipResource
