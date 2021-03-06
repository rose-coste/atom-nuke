package org.atomnuke.atom.model.builder;

import java.net.URI;
import org.atomnuke.atom.model.impl.PersonConstructImpl;

/**
 *
 * @author zinic
 */
public class PersonConstructBuilder extends PersonConstructImpl {

   public PersonConstructBuilder() {
   }

   public String getName() {
      return name;
   }

   public String getEmail() {
      return email;
   }

   public String getUri() {
      return uri;
   }

   public URI getBase() {
      return base;
   }

   public String getLang() {
      return lang;
   }

   public PersonConstructBuilder setName(String name) {
      this.name = name;
      return this;
   }

   public PersonConstructBuilder setEmail(String email) {
      this.email = email;
      return this;
   }

   public PersonConstructBuilder setUri(String uri) {
      this.uri = uri;
      return this;
   }

   public PersonConstructBuilder setBase(URI base) {
      this.base = base;
      return this;
   }

   public PersonConstructBuilder setLang(String lang) {
      this.lang = lang;
      return this;
   }
}
