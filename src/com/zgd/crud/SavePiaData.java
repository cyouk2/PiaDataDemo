package com.zgd.crud;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.zgd.GetPiaData;
import com.zgd.common.ComResult;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class SavePiaData extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String playDate = req.getParameter("playDate");
		String taiNo = req.getParameter("taiNo");
		String bonusCount = req.getParameter("bonusCount");
		String ballOutput = req.getParameter("ballOutput");
		String rate = req.getParameter("rate");
		
		String ballInput = req.getParameter("ballInput");

		
		boolean isErr = false;
		boolean isExist = false;
		String fieldMsg ="";
		
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

		List<Map<String, Object>> list = GetPiaData.getTallestPeople(playDate, taiNo);

		if (list != null && list.size() > 0) {
			isErr = true;
			isExist = true;
		}
		ComResult re = new ComResult();
		re.setSuccess(true);

		if (!isErr) {

			int bonusCountNo = CommonUtil.ObejctToInt(bonusCount);
			int ballInputNo = CommonUtil.ObejctToInt(ballInput);
			int ballOutputNo = CommonUtil.ObejctToInt(ballOutput);
			int rateNo = CommonUtil.ObejctToInt(rate);
			
			try {

				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity employee = new Entity("PIA_DATA_INFO");
				employee.setProperty("playDate", playDate);
				employee.setProperty("taiNo", taiNo);
				employee.setProperty("bonusCount", bonusCountNo);
				employee.setProperty("ballInput", ballInputNo);
				employee.setProperty("ballOutput", ballOutputNo);
				employee.setProperty("rate", rateNo);
				datastore.put(employee);

				re.setMsg("台番：" + taiNo + ";日付：" + playDate + "の情報を保存しました。");
			} catch (Exception w) {
				re.setMsg("保存処理にはエラーが発声しました。");
			}
		} else {
			re.setMsg(fieldMsg + "を入力してください。");
		}
		if (isExist){
			re.setMsg("該当データが存在しました。");
		}
		Gson gson = new Gson();
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().println(gson.toJson(re));
	}
}
