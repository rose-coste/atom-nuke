package net.jps.nuke.atom.model.builder;

import java.net.URI;
import net.jps.nuke.atom.model.impl.LangAwareStringValue;

/**
 *
 * @author zinic
 */
public class LangAwareStringValueBuilder extends LangAwareStringValue {

   public static LangAwareStringValueBuilder newBuilder() {
      return new LangAwareStringValueBuilder();
   }

   protected LangAwareStringValueBuilder() {
      value = new StringBuilder();
   }

   public LangAwareStringValue build() {
      return this;
   }

   public void setValue(String value) {
      this.value = new StringBuilder(value);
   }

   public void appendValue(String value) {
      this.value.append(value);
   }

   public void setBase(URI base) {
      this.base = base;
   }

   public void setLang(String lang) {
      this.lang = lang;
   }
}
