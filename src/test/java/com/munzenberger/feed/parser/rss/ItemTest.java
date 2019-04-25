/*
 * Copyright 2019 Brian Munzenberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.munzenberger.feed.parser.rss;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

    public void testGetUniqueIdWithGuid() {

        Item item = new Item();
        item.setGuid("guid");

        String id = item.getUniqueId();

        assertEquals("guid", id);
    }

    public void testGetUniqueIdWithoutGuid() {

        Item item = new Item();
        item.setTitle("title");
        item.setDescription("description");
        item.setPubDate("pubDate");

        String id = item.getUniqueId();

        assertEquals("7Ii4Jn5KDOCyR1m6Fd9mvw==", id);
    }
}
