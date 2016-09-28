package com.zgd.download;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class PiaDataDownload extends HttpServlet {

	private PrintWriter out;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			String fileName = "piaData.csv";
			// コンテントタイプ設定
			resp.setContentType("application/octet-stream");
			// ヘッダー設定
			resp.setHeader("Content-Disposition", "filename=\"" + fileName + "\"");
			// レスポンス出力バイトストリームを取得
			out = resp.getWriter();
			// データ出力
			String strline = "";
			List<String> etiqueta = new ArrayList<String>();
			for (int i = 557; i <= 584; i++) {
				strline += (CommonUtil.ObejctToString(i) + ",");
				etiqueta.add(CommonUtil.ObejctToString(i));
			}
			Query q = new Query("PIA_DATA").addSort("playDate", SortDirection.ASCENDING).addSort("taiNo", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
//			String strMsg = "playDate,taiNo,rate,bonusCount,ballOutput";
//			out.println(strMsg);
	
			out.println(strline);
			strline = "";
			for (Entity en : pq.asIterable()) {
				String taiNo = CommonUtil.ObejctToString(en.getProperty("taiNo"));
				String ballOutput = CommonUtil.ObejctToString(en.getProperty("ballOutput"));
				if (CommonUtil.ObejctToInt(taiNo) < 557){
					continue;
				}
				strline += ballOutput + "," ;
				if (taiNo.equals("584")){
					out.println(strline);
					strline = "";
				}
			}
		} catch (Exception e) {
			out.println(e.getMessage());
		} finally {
			// 終了処理
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
