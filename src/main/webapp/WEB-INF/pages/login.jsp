<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teach Children To Save - Login</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
    <%@include file="include/header.jsp"%>
    <h2>Teach Children To Save Login</h2>
    <div class="errorMessage"><c:out value="${errorMessage}" default=""/></div>
    <div class="qa-notes">Some valid logins for testing: larry(V) jane(BA) lucy(T) harry(SA) moe(V) curley(V)
        shemp(V) joshua(T) allen(T). V=Volunteer; T=Teacher; BA=BankAdmin; SA=SiteAdmin. All have "pass"
        as their password.</div>
    <form:form method="POST" action="login.htm" modelAttribute="login">
        <div id="container">
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
