package com.techrocking.filters;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class RouteFilter extends ZuulFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(RouteFilter.class);

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		/*
		 * String access_token = (String) ctx.get("ACCESS_TOKEN"); if
		 * ("".equals(access_token)) { return false; }
		 */
		return FilterUtil.SHOULD_FILTER;
	}

	@Override
	public String filterType() {
		return FilterUtil.FILTER_TYPE_ROUTE;
	}

	@Override
	public int filterOrder() {
		return FilterUtil.FILTER_ORDER;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
        String serviceName = (String) context.get("serviceId");
        
        String access_token = (String) context.get("ACCESS_TOKEN");
        //context.getResponse().setHeader("access_token", access_token);
        //context.getRequest().setAttribute("access_token", access_token);
        
        logger.info("service name " + serviceName);
        
        if("item-service".equalsIgnoreCase(serviceName)) {
        	logger.info("do some processing here");
        }
        
		return null;
	}
	
	private ProxyRequestHelper helper = new ProxyRequestHelper();


	private void forwardToSpecialRoute(String route) {
		
		CloseableHttpClient httpClient = null;
		HttpResponse response = null;

		try {
			httpClient = HttpClients.createDefault();
			response = forward(httpClient,route);
			setResponse(response);
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			try {
				httpClient.close();
			} catch (IOException ex) {
			}
		}
	}

	private HttpResponse forward(HttpClient httpclient, String uri)
			throws Exception {
		
		String zuulServer = "http://localhost:8892";
		URL host = new URL(zuulServer + uri);
		HttpHost httpHost = getHttpHost(host);

		HttpRequest httpRequest;
		
		HttpGet httpGet = new HttpGet(uri);
		httpRequest = httpGet;

		try {
			HttpResponse zuulResponse = httpclient.execute(httpHost, httpRequest);

			return zuulResponse;
		} finally {
		}
	}

	private HttpHost getHttpHost(URL host) {
		HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
		return httpHost;
	}

	
	 private void setResponse(HttpResponse response) throws IOException {
	        this.helper.setResponse(response.getStatusLine().getStatusCode(),
	                response.getEntity() == null ? null : response.getEntity().getContent(),
	                revertHeaders(response.getAllHeaders()));
	    }
	 
	 private MultiValueMap<String, String> revertHeaders(Header[] headers) {
	        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	        for (Header header : headers) {
	            String name = header.getName();
	            if (!map.containsKey(name)) {
	                map.put(name, new ArrayList<String>());
	            }
	            map.get(name).add(header.getValue());
	        }
	        return map;
	    }

}
