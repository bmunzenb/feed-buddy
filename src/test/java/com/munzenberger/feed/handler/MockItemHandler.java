/*
 * Copyright 2017 Brian Munzenberger
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
package com.munzenberger.feed.handler;

import junit.framework.Assert;

import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public class MockItemHandler implements ItemHandler {

	private String name1;
	
	private String name2;
	
	public void setPoop(String name1) {
		this.name1 = name1;
	}

	public void setDick(String name2) {
		this.name2 = name2;
	}

	public void assertValues(String value1, String value2) {
		Assert.assertEquals(value1, this.name1);
		Assert.assertEquals(value2, this.name2);
	}
	
	public void process(Item item, Logger logger) throws ItemHandlerException {
		throw new UnsupportedOperationException("Method not implemented");
	}
}
