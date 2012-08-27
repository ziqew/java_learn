<%@ page import="java.util.Date" %>
<%--

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<html>
<body>


<h2>Hello World!</h2>
<% int foo = 0;%>
<br/>
<br/>
<c:set var="headerDateFormat" value="EEE, d MMM yyyy h:mm:ss aa" />
<c:out value="${headerDateFormat}"/>

</body>
</html>
