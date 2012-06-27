package net.jps.nuke.atom.model;

import net.jps.nuke.atom.model.annotation.ComplexConstraint;
import net.jps.nuke.atom.model.annotation.Required;
import net.jps.nuke.atom.model.annotation.ElementValue;
import net.jps.nuke.atom.model.constraint.AtomUriConstraint;

/**
 *
 * @author zinic
 */
public interface ID extends AtomCommonAtributes {

   @ElementValue
   @Required
   @ComplexConstraint(AtomUriConstraint.class)
   String value();
}
