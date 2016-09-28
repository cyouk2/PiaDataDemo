package com.zgd.download;

import java.io.IOException;
import java.io.PrintWriter;

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
public class PiaDataInfoDownload extends HttpServlet {

	private PrintWriter out;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			String fileName = "PiaDataInfo.csv";
			// コンテントタイプ設定
			resp.setContentType("application/octet-stream");
			// ヘッダー設定
			resp.setHeader("Content-Disposition", "filename=\"" + fileName + "\"");
			// レスポンス出力バイトストリームを取得
			out = resp.getWriter();
			// データ出力
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query q = new Query("PIA_DATA").addSort("playDate", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			String strMsg = "playDate,taiNo,rate,bonusCount,ballOutput";
			out.println(strMsg);
			
			for (Entity en : pq.asIterable()) {
				String playDate = CommonUtil.ObejctToString(en.getProperty("playDate"));
				String taiNo = CommonUtil.ObejctToString(en.getProperty("taiNo"));
				String rate = CommonUtil.ObejctToString(en.getProperty("rate"));
				String bonusCount = CommonUtil.ObejctToString(en.getProperty("bonusCount"));
				String ballOutput = CommonUtil.ObejctToString(en.getProperty("ballOutput"));
				String line = playDate + "," + taiNo + "," + rate + "," + bonusCount + "," + ballOutput;
				out.println(line);
			}

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