package com.zgd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class Serve extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String keyString = req.getParameter("blob-key");
		BlobKey blobkey = new BlobKey(keyString);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BlobstoreInputStream(blobkey)));
		String line = null;
		
		
		//String columns = "playDate,taiNo,bonusCount,rate,ballOutput,ballInput,totalOut,totalOutBefore,rateN,bonusCountN,ballOutputN,playDateN,totalOutN";
		String columns ="playDate,taiNo,rate,bonusCount,ballOutput";
		String[] a = columns.split(",");
		while ((line = reader.readLine()) != null) {
			// Do whatever you like with the line
			savepidata(line.split(","), a);
		}
		reader.close();
		res.setContentType("text/plain");
		res.getWriter().println("OK");
	}
	
	private void savepidata(String[] line,String[] a){
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity employee = new Entity("PIA_DATA");
		for(int i = 0; i < a.length; i++){
			employee.setProperty(a[i], line[i]);
		}
		
		datastore.put(employee);
	}
}
