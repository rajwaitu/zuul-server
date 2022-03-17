package com.techrocking.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.techrocking.util.RestUtil;
import com.techrocking.util.VerifyTokenResponse;


@Component
public class PreFilter extends ZuulFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(PreFilter.class);
	
	@Autowired
	private RestUtil restUtil;

	@Override
	public boolean shouldFilter() {
		return FilterUtil.SHOULD_FILTER;
	}

	@Override
	public String filterType() {
		return FilterUtil.FILTER_TYPE_PRE;
	}

	@Override
	public int filterOrder() {
		return FilterUtil.FILTER_ORDER;
	}
	
	@Override
	public Object run() throws ZuulException {
		FilterUtil.setTransactionId();
		logger.info("transaction id created : " + FilterUtil.getTransactionId());
		RequestContext ctx = RequestContext.getCurrentContext();

		String bearerToken = ctx.getRequest().getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String access_token = bearerToken.substring(7, bearerToken.length());

			VerifyTokenResponse response = restUtil.verifyToken(access_token);
			if (response != null && response.getActive()) {
				ctx.put("ACCESS_TOKEN", access_token);
				 //ctx.getRequest().setAttribute("access_token", access_token);
				 ctx.addZuulRequestHeader("access_token", access_token);
			} else {
				//ctx.put("ACCESS_TOKEN", "");
				throw new ZuulException("Invalid Access Token", 401, "Invalid Access Token");
			}
		} else {
			//ctx.put("ACCESS_TOKEN", "");
			throw new ZuulException("No Access Token Received", 401, "No Access Token Received");
		}
		return null;
	}
	
}
