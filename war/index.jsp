<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
<html>
<head>
<title>Upload Test</title>
</head>
<body>
	<form action="<%=blobstoreService.createUploadUrl("/UploadFileCsvOfPiaData")%>" method="post" enctype="multipart/form-data">
		<input type="radio" name="hyouka" value="uploadFileOnly" checked="checked">uploadFileOnly
		<input type="radio" name="hyouka" value="uploadAndUpdateData">uploadAndUpdateData
		<br/><br/><br/>
		<input type="file" name="myFile">
		<br/><br/><br/>
		<input type="submit" value="Submit">
	</form>
</body>
</html>