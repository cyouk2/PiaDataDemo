package com.zgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.zgd.common.ComRootResult;

@SuppressWarnings("serial")
public class GetTaiNoList extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("TAI_NO").addSort("taiNo", SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Entity en : pq.asIterable()) {
			String strTaiNo = String.valueOf(en.getProperty("taiNo"));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taiNo", strTaiNo);
			list.add(map);
		}
		Gson gson = new Gson();
		ComRootResult re = new ComRootResult();
		re.setSuccess(true);
		re.setMsg("");
		re.setRoot(list);
		resp.setContentType("text/plain");
		resp.getWriter().println(gson.toJson(re));
	}
}
