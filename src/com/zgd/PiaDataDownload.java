package com.zgd;

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
			
			Query q = new Query("PIA_DATA").addSort("playDate", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			out.println("playDate,taiNo,rate,bonusCount,ballOutput");
			for (Entity en : pq.asIterable()) {
				String s1 = CommonUtil.ObejctToString(en.getProperty("playDate"));
				String s2 = CommonUtil.ObejctToString(en.getProperty("taiNo"));
				String s3 = CommonUtil.ObejctToString(en.getProperty("rate"));
				String s4 = CommonUtil.ObejctToString(en.getProperty("bonusCount"));
				String s5 = CommonUtil.ObejctToString(en.getProperty("ballOutput"));
				out.println(s1 + "," +s2 + "," +s3 + "," +s4+ "," +s5 );
			}

		} finally {
			// 終了処理
			if (out != null) {
				out.close();
			}
		}
	}	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req,resp);
	}
}
