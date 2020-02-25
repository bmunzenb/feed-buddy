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

import junit.framework.TestCase;

import com.munzenberger.feed.config.Handler;
import com.munzenberger.feed.config.Property;

public class HandlerFactoryTest extends TestCase {
	
	public void testNewHandlerProperties() throws Exception {
		
		Handler h = new Handler();
		h.setClazz(MockItemHandler.class.getName());
		h.addProperty(new Property("foo", "value1"));
		h.addProperty(new Property("bar", "value2"));
		
		MockItemHandler handler = (MockItemHandler) ItemHandlerFactory.newHandler(h);
		handler.assertValues("value1", "value2");
	}
}
