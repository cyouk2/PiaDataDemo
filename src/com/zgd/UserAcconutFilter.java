package com.zgd;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserAcconutFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		UserService userService = UserServiceFactory.getUserService();

        // このフィルタが実行されているウェブページのURIを取得します。
        String thisURL = ((HttpServletRequest) request).getRequestURI();
        
        // このフィルタにリクエストを出したプリンシパルを取得します（認証していない場合、null）
        Principal principal = ((HttpServletRequest) request).getUserPrincipal();

        HttpSession session = ((HttpServletRequest) request).getSession();

        if (principal == null) {
            // ユーザ認証済
            session.setAttribute("isLogin", "LOGOUT");
            // ログイン用のGoogle AccountsのURLを取得します
            session.setAttribute("urlpath", userService.createLoginURL(thisURL));
        } else {
            // ユーザ未認証
            session.setAttribute("isLogin", "LOGIN");
            // ログアウト用のGoogle AccountsのURLを取得します
            session.setAttribute("urlpath", userService.createLogoutURL(thisURL));
            // ユーザアカウント関連情報
            session.setAttribute("userInfo", userService.getCurrentUser());
        }

        filterChain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		

	}

}
