<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teach Children to Save - Edit Personal Data</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
    <%@include file="include/header.jsp"%>
    <h2>Edit Data</h2>
    <div class="errorMessage"><c:out value="${errorMessage}" default=""/></div>
    <div>
        <form:form method="POST" action="editPersonalData.htm" modelAttribute="formData">
            <div>
                <div><form:label path="email">Email</form:label></div>
                <div><form:input path="email"/></div>
            </div>
            <div>
                <div><form:label path="firstName">First Name</form:label></div>
                <div><form:input path="firstName"/></div>
            </div>
            <div>
                <div><form:label path="lastName">Last Name</form:label></div>
                <div><form:input path="lastName"/></div>
            </div>
            <div>
                <div><form:label path="phoneNumber">Phone Number</form:label></div>
                <div><form:input path="phoneNumber"/></div>
            </div>
            <div>
                <input type="submit" value="Edit"/>
            </div>
        </form:form>
    </div>
</body>
</html>