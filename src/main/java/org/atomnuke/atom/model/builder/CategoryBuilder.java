package org.atomnuke.atom.model.builder;

import java.net.URI;
import org.atomnuke.atom.model.impl.CategoryImpl;

/**
 *
 * @author zinic
 */
public class CategoryBuilder extends CategoryImpl {

   public String getTerm() {
      return term;
   }

   public String getScheme() {
      return scheme;
   }

   public String getLabel() {
      return label;
   }

   public URI getBase() {
      return base;
   }

   public String getLang() {
      return lang;
   }

   public CategoryBuilder setTerm(String term) {
      this.term = term;
      return this;
   }

   public CategoryBuilder setScheme(String scheme) {
      this.scheme = scheme;
      return this;
   }

   public CategoryBuilder setLabel(String label) {
      this.label = label;
      return this;
   }

   public CategoryBuilder setBase(URI base) {
      this.base = base;
      return this;
   }

   public CategoryBuilder setLang(String lang) {
      this.lang = lang;
      return this;
   }
}
