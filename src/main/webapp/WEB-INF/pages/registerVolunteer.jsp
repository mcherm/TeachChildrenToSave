<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Register New Volunteer</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body>
        <%@include file="include/header.jsp"%>
        <h2>Register a New Volunteer</h2>
        <div class="errorMessage"><c:out value="${errorMessage}" default=""/></div>
        <div class="volunteerRegistrationForm">
            <form:form method="POST" action="registerVolunteer.htm" modelAttribute="volunteer">
                <div>
                    <div><form:label path="login">Username</form:label></div>
                    <div><form:input path="login"/></div>
                </div>
                <div>
                    <div><form:label path="email">Email</form:label></div>
                    <div><form:input path="email"/></div>
                </div>
                <div>
                    <div><form:label path="password">Password</form:label></div>
                    <div><form:input path="password"/></div>
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
                    <div class="qa-notes">This needs to be a tool for selecting from the list.</div>
                    <div><form:label path="bankId">Select your bank</form:label></div>
                    <div>
                        <form:select path="bankId">
                            <form:option value="0" label="- Select Bank -" />
                            <form:options items="${banks}" itemValue="bankId" itemLabel="bankName" />
                        </form:select>
                    </div>
                </div>
                <div>
                    <input type="submit" value="Register"/>
                </div>
            </form:form>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
