<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - Login</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
<%@include file="include/header.jsp"%>
<h2>Volunteer Information</h2>
<form:form method="POST" action="login.htm" modelAttribute="login">
   <div id="container">
    <div class="qa-notes">For now, the letter of uses chooses type: V(olunteer), T(eacher), B(ankAdmin), S(iteAdmin)</div>
    <div class="row_div">
    	<div class="row_div_left">
	        <div><form:label path="userID">User Name</form:label></div>
	        <div><form:input path="userID" /></div>
        </div>
    </div>
    <div class="row_div">
    	<div class="row_div_left">
	        <div><form:label path="password">Password</form:label></div>
	        <div><form:input path="password" type="password"/></div>
        </div>
    </div>
    <div class="row_div">
        <div>
            <input type="submit" value="Login"/>
        </div>
    </div>
</div>  
</form:form>
</body>
</html>