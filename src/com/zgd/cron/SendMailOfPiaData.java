package com.zgd.cron;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
public class SendMailOfPiaData extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			
			Query q = new Query("PIA_DATA_INFO").addSort("playDate", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			String strMsg = "playDate,taiNo,rate,bonusCount,ballOutput*";
			for (Entity en : pq.asIterable()) {
				String playDate = CommonUtil.ObejctToString(en.getProperty("playDate"));
				String taiNo = CommonUtil.ObejctToString(en.getProperty("taiNo"));
				String rate = CommonUtil.ObejctToString(en.getProperty("rate"));
				String bonusCount = CommonUtil.ObejctToString(en.getProperty("bonusCount"));
				String ballOutput = CommonUtil.ObejctToString(en.getProperty("ballOutput"));
				String line = playDate + "," + taiNo + "," + rate + "," + bonusCount + "," + ballOutput;				
				strMsg += (line + "*");

			}
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("q174221679@gmail.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("q174221679@gmail.com"));
			msg.setSubject("piadata");
			msg.setText(strMsg);
			Transport.send(msg);

		} catch (Exception e) {
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}
