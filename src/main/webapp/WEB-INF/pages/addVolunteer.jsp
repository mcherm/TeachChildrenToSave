<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>STeach Children To Save - Add Volunteer</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body>
        <%@include file="include/header.jsp"%>

        <h2>Volunteer Information</h2>
        <form:form method="POST" action="/addVolunteer.htm">
            <div>
                <div>
                    <div><form:label path="firstName">First Name</form:label></div>
                    <div><form:input path="firstName" /></div>

                    <div><form:label path="lastName">Last Name</form:label></div>
                    <div><form:input path="lastName" /></div>
                </div>
                <div>
                    <div><form:label path="emailAddress">Email Address</form:label></div>
                    <div><form:input path="emailAddress" /></div>
                </div>
                <div>
                    <div><form:label path="confirmEmailAddress">Confirm Email Address</form:label></div>
                    <div><form:input path="confirmEmailAddress" /></div>
                </div>

                <div>
                    <div><form:label path="password">password</form:label></div>
                    <div><form:input path="password" /></div>
                </div>

                <div>
                    <div><form:label path="confirmPassword">Re-Enter Password</form:label></div>
                    <div><form:input path="confirmPassword" /></div>
                </div>

                <div>
                    <div><form:label path="addressLine1">Work Address Line1</form:label></div>
                    <div><form:input path="addressLine1" /></div>
                </div>

                <div>
                    <div><form:label path="addressLine2">Work Address Line2</form:label></div>
                    <div><form:input path="addressLine2" /></div>
                </div>

                <div>
                    <div><form:label path="city">City</form:label></div>
                    <div><form:input path="city" /></div>
                </div>

                <div>
                    <div><form:label path="state">State</form:label></div>
                    <div><form:input path="state" /></div>

                    <div><form:label path="zipcode">zip</form:label></div>
                    <div><form:input path="zipcode" /></div>
                </div>
                <div>
                    <div>
                        <input type="submit" value="Submit"/>
                    </div>
                </div>
            </div>
        </form:form>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
