/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Generic method cache interceptor based on the arguments, method and class names
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean {
	
	private static final Log logger = LogFactory.getLog(MethodCacheInterceptor.class);

	private Cache cache;

	/**
	 * sets cache name to be used
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	/**
	 * Checks if required attributes are provided.
	 */
	public void afterPropertiesSet() {
		Assert.notNull(cache,
				"A cache is required. Use setCache(Cache) to provide one.");
	}

	/**
	 * main method caches method result if method is configured for caching
	 * method results must be serializable
	 */
	public Object invoke(MethodInvocation invocation) throws Exception {
		String targetName = invocation.getThis().getClass().getName();
		String methodName = invocation.getMethod().getName();
		Object[] arguments = invocation.getArguments();
		Object result;

		logger.debug("looking for method result in cache");
		String cacheKey = getCacheKey(targetName, methodName, arguments);
		Element element = cache.get(cacheKey);
		if (element == null) {
			// call target/sub-interceptor
			logger.debug("calling intercepted method");
			try {
				result = invocation.proceed();
			} catch(Throwable t) {
				throw new Exception(t.getMessage(), t);
			}

			// cache method result
			logger.debug("caching result");
			element = new Element(cacheKey, (Serializable) result);
			cache.put(element);
		}
		return element.getValue();
	}

	/**
	 * creates cache key: targetName.methodName.argument0.argument1...
	 */
	private String getCacheKey(String targetName, String methodName,
			Object[] arguments) {
		StringBuffer sb = new StringBuffer();
		sb.append(targetName).append(".").append(methodName);
		if ((arguments != null) && (arguments.length != 0)) {
			for (int i = 0; i < arguments.length; i++) {
				sb.append(".").append(arguments[i]);
			}
		}

		return sb.toString();
	}
}