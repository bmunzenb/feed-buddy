package com.munzenberger.feed.engine;

import junit.framework.TestCase;

import com.munzenberger.feed.config.Feed;
import com.munzenberger.feed.ui.StandardOutMessageDispatcher;

public class FeedProcessorTest extends TestCase {

	public void testProcess() throws Exception {
		
		System.setProperty("http.agent", "feed-buddy");
		
		Feed feed = new Feed();
		feed.setUrl("http://groups.google.com/group/craftsman-guild/feed/rss_v2_0_msgs.xml");
		
		@SuppressWarnings("unused")
		FeedProcessor processor = new FeedProcessor(feed, new StandardOutMessageDispatcher(), new StubProcessedItemsRegistry());
		//processor.run();
	}
}
