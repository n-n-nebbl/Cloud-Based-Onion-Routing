<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Simple Onion Proxy Client</title>
</head>
<body>
<form action="#" method="POST">
	<input type="text" name="directoryServer" value="localhost" />
	<input type="text" name="dirPort" value="8001" />
	<input type="text" name="requestServer" value="localhost" />
	<input type="text" name="requestPort" value="8080" />
	<input type="text" name="request" value="GET /onion.testservice/ HTTP/1.1\nHost: localhost:8080\n\n" />
	<input name="callService" type="submit" value="call webservice" />
</form>
${output}
</body>
</html>