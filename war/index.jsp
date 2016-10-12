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
	<span>playDate,taiNo,rate,bonusCount,ballOutput</span>
	<br/>
	<form action="<%=blobstoreService.createUploadUrl("/upload")%>" method="post" enctype="multipart/form-data">
		<input type="file" name="myFile">
		<input type="submit" value="Submit">
	</form>
</body>
</html>