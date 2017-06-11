package com.harvey.w.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harvey.w.core.exception.NoneLoginException;
import com.harvey.w.core.model.UserBaseModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Context extends AbstractContext {

	private static final ThreadLocal<RequestInfo> requestThreadLocal = new ThreadLocal<RequestInfo>();

	public static HttpServletRequest getRequest() {
		RequestInfo ri = requestThreadLocal.get();
		return ri != null ? ri.getRequest() : null;
	}

	public static HttpServletResponse getResponse() {
		RequestInfo ri = requestThreadLocal.get();
		return ri != null ? ri.getResponse() : null;
	}

	public static void setRequest(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			request.setAttribute("userInfo", authentication.getPrincipal());
		}
		requestThreadLocal.set(new RequestInfo(request, response));
	}

	public static void releaseRequest() {
		requestThreadLocal.remove();
	}

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static String getContextPath() {
		return getServletContext().getContextPath();
	}

    public static <T extends UserBaseModel> T checkCurrentUser() {
        T user = getCurrentUser();
        if (user == null) {
            throw new NoneLoginException();
        }
        return user;
    }
	
	static class RequestInfo {

		private HttpServletRequest request;
		private HttpServletResponse response;

		public RequestInfo(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public void setRequest(HttpServletRequest request) {
			this.request = request;
		}

		public HttpServletResponse getResponse() {
			return response;
		}

		public void setResponse(HttpServletResponse response) {
			this.response = response;
		}

	}
}
