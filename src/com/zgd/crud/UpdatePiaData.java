package com.zgd.crud;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import com.zgd.common.ComResult;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class UpdatePiaData extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 必須
		String id = req.getParameter("id");
		String playDate = req.getParameter("playDate");
		String taiNo = req.getParameter("taiNo");
		String bonusCount = req.getParameter("bonusCount");
		String ballOutput = req.getParameter("ballOutput");
		String rate = req.getParameter("rate");
		
		// 任意
		String ballInput = req.getParameter("ballInput");
		
		
		boolean isErr = false;
		String fieldMsg ="";
		if (CommonUtil.IsNullOrEmpty(id)) {
			fieldMsg = "id";
			isErr = true;
		}
		if (CommonUtil.IsNullOrEmpty(playDate)) {
			fieldMsg = "日付";
			isErr = true;
		}
		if (CommonUtil.IsNullOrEmpty(taiNo)) {
			fieldMsg = "台番";
			isErr = true;
		}
		if (CommonUtil.IsNullOrEmpty(bonusCount)) {
			fieldMsg = "当たり数";
			isErr = true;
		}
		if (CommonUtil.IsNullOrEmpty(ballOutput)) {
			fieldMsg = "出玉数";
			isErr = true;
		}
		if (CommonUtil.IsNullOrEmpty(rate)) {
			fieldMsg = "確率";
			isErr = true;
		}
		ComResult re = new ComResult();
		re.setSuccess(true);
		if (!isErr) {

			int bonusCountNo = CommonUtil.ObejctToInt(bonusCount);
			int ballInputNo = CommonUtil.ObejctToInt(ballInput);
			int ballOutputNo = CommonUtil.ObejctToInt(ballOutput);
			int rateNo = CommonUtil.ObejctToInt(rate);
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Transaction txn = datastore.beginTransaction();

			try {
				Key employeeKey = KeyFactory.createKey("PIA_DATA_INFO", Long.parseLong(id));
				Entity employee = datastore.get(employeeKey);
				employee.setProperty("playDate", playDate);
				employee.setProperty("taiNo", taiNo);
				employee.setProperty("bonusCount", bonusCountNo);
				employee.setProperty("ballInput", ballInputNo);
				employee.setProperty("ballOutput", ballOutputNo);
				employee.setProperty("rate", rateNo);
				datastore.put(txn, employee);
				txn.commit();
				re.setMsg("データを更新しました。");
			} catch (Exception e) {
				re.setMsg("データ更新処理にはエラーが発生しました。!");
			} finally {
				if (txn.isActive()) {
					txn.rollback();
				}
			}
		} else {
			re.setMsg(fieldMsg + "情報を入力してください。");
		}
		Gson gson = new Gson();
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().println(gson.toJson(re));
	}
}
