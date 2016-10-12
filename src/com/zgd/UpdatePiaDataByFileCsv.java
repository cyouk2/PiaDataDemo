package com.zgd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.zgd.common.CommonUtil;

@SuppressWarnings("serial")
public class UpdatePiaDataByFileCsv extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String keyString = req.getParameter("blob-key");
		BlobKey blobkey = new BlobKey(keyString);

		BlobInfoFactory fac = new BlobInfoFactory();
		BlobInfo fileInfo = fac.loadBlobInfo(blobkey);
		String fileName = fileInfo.getFilename();
		String tableName = fileName.toUpperCase().split(".CSV")[0];
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BlobstoreInputStream(blobkey)));
		String line = null;

		String[] columns = {};
		int rowno = 0;
		while ((line = reader.readLine()) != null) {

			if (rowno == 0) {
				columns = line.split(",");
			} else {
				savepidata(line.split(","), columns, tableName);
			}

			rowno++;
		}
		reader.close();
		res.setContentType("text/plain");
		res.getWriter().println("OK");
	}

	private void savepidata(String[] line, String[] a, String tableName) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity employee = new Entity(tableName);
		for (int i = 0; i < a.length; i++) {
			employee.setProperty(a[i].trim(), CommonUtil.ObejctToInt(CommonUtil.ObejctToString(line[i])));
		}

		datastore.put(employee);
	}
}
