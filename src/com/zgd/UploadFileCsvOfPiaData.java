package com.zgd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class UploadFileCsvOfPiaData extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 処理区分
		String hyouka = req.getParameter("hyouka");
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("myFile");

		if (blobKeys == null || blobKeys.isEmpty()) {
			res.sendRedirect("/index.jsp");
		} else {
			if ("uploadFileOnly".equals(hyouka)) {
				res.setContentType("text/plain");
				res.getWriter().println("UploadFileOnly OK");
			} else {
				res.sendRedirect("/UpdatePiaDataByFileCsv?blob-key=" + blobKeys.get(0).getKeyString());
			}
		}
	}
}