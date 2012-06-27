package net.jps.nuke.atom.model;

import net.jps.nuke.atom.model.annotation.ComplexConstraint;
import net.jps.nuke.atom.model.annotation.Required;
import net.jps.nuke.atom.model.annotation.ElementValue;
import net.jps.nuke.atom.model.constraint.AtomUriConstraint;

/**
 *
 * @author zinic
 */
public interface Generator extends AtomCommonAtributes {

   @ComplexConstraint(AtomUriConstraint.class)
   String uri();

   String version();

   @ElementValue
   String value();
}
