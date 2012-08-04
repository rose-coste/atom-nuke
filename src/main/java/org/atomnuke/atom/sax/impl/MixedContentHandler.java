package org.atomnuke.atom.sax.impl;

import org.atomnuke.atom.model.builder.ValueBuilder;
import org.atomnuke.atom.sax.DelegatingHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * TODO: Ugh... make this pretty...
 *
 * @author zinic
 */
public class MixedContentHandler<T extends ValueBuilder> extends DelegatingHandler {

   private final T contentBuilder;
   private int depth;

   public MixedContentHandler(T contentBuilder, DelegatingHandler parent) {
      super(parent);

      this.contentBuilder = contentBuilder;
      depth = 0;
   }

   private static String asFullName(String qName, String localName) {
      final StringBuilder name = new StringBuilder();

      if (qName != null && !"".equals(qName)) {
         name.append(qName);
      }

      if (localName != null && !"".equals(localName)) {
         if (name.length() > 0) {
            name.append(":");
         }

         name.append(localName);
      }

      return name.toString();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      contentBuilder.appendValue("<");
      contentBuilder.appendValue(asFullName(qName, localName));

      for (int i = 0; i < attributes.getLength(); i++) {
         contentBuilder.appendValue(" ");
         contentBuilder.appendValue(asFullName(attributes.getQName(i), attributes.getLocalName(i)));
         contentBuilder.appendValue("\"");
         contentBuilder.appendValue(StringEscapeUtils.escapeXml(attributes.getValue(i)));
         contentBuilder.appendValue("\"");
      }

      contentBuilder.appendValue(">");
      depth++;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (depth > 0) {
         contentBuilder.appendValue("</");
         contentBuilder.appendValue(asFullName(qName, localName));
         contentBuilder.appendValue(">");
      }

      if (--depth < 0) {
         releaseToParent().endElement(uri, localName, qName);
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      contentBuilder.appendValue(new String(ch, start, length).trim());
   }
}