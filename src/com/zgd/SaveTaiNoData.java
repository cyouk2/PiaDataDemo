package com.zgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class SaveTaiNoData extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String taiNo = req.getParameter("taiNo");
		String groupName = req.getParameter("groupName");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		int taiNos = CommonUtil.ObejctToInt(taiNo);
		try{
			List<Entity> employees = new ArrayList<Entity>();
			for(int i = 0 ;i < 7;i++){

				Entity employee = new Entity("TAI_NO");
				employee.setProperty("taiNo", CommonUtil.ObejctToString(taiNos + i));
				employee.setProperty("groupName", CommonUtil.ObejctToString(groupName));
				employees.add(employee);
				
			}
			datastore.put(employees);

		} catch (Exception e) {
			
		} finally {
		
		}
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().println("OK");
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}