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
		String[] strList = taiNo.split(",");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String reslut = "";
		if (strList.length == 3){
			int taiNos = CommonUtil.ObejctToInt(strList[0]);
			String groupNames = CommonUtil.ObejctToString(strList[1]);
			int index = CommonUtil.ObejctToInt(strList[2]);
			try{
				List<Entity> employees = new ArrayList<Entity>();
				
				for(int i = 0 ;i < index;i++){

					Entity employee = new Entity("TAI_NO");
					employee.setProperty("taiNo", CommonUtil.ObejctToString(taiNos + i));
					employee.setProperty("groupName", groupNames);
					employees.add(employee);
					reslut += (taiNos + ",");
				}
				datastore.put(employees);

			} catch (Exception e) {
				
			} 
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println("OK:" + reslut);
		}else{
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println("taiNo,groupName,index");
		}	
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}