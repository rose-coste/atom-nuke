package net.jps.nuke.atom.model.impl;

import net.jps.nuke.atom.model.Category;

/**
 *
 * @author zinic
 */
public abstract class CategoryImpl extends AtomCommonAttributesImpl implements Category {

   protected String term;
   protected String scheme;
   protected String label;

   public String term() {
      return term;
   }

   public String scheme() {
      return scheme;
   }

   public String label() {
      return label;
   }
}
