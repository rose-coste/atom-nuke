package org.atomnuke.atom;

import org.atomnuke.atom.model.Entry;
import org.atomnuke.atom.model.Feed;
import org.atomnuke.atom.model.builder.EntryBuilder;
import org.atomnuke.atom.model.builder.FeedBuilder;

/**
 *
 * @author zinic
 */
public class ParserResultImpl implements ParserResult {

   private FeedBuilder feed;
   private EntryBuilder entry;

   public void setFeedBuilder(FeedBuilder feed) {
      this.feed = feed;
   }

   public void setEntryBuilder(EntryBuilder entry) {
      this.entry = entry;
   }

   @Override
   public Feed getFeed() {
      return feed != null ? feed : null;
   }

   @Override
   public Entry getEntry() {
      return entry != null ? entry : null;
   }
}
