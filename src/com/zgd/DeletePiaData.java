package com.zgd;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.zgd.common.ComResult;

@SuppressWarnings("serial")
public class DeletePiaData extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String id = req.getParameter("id");
		ComResult re = new ComResult();
		
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key employeeKeys = KeyFactory.createKey("PIA_DATA", Long.parseLong(id));
			datastore.delete(employeeKeys);
			
			re.setSuccess(true);
			re.setMsg("データを削除しました。");
		} catch (Exception e) {
			
			re.setSuccess(false);
			re.setMsg("削除処理は失敗しました。");
		} finally {
			Gson gson = new Gson();
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println(gson.toJson(re));
		}
	}
}
