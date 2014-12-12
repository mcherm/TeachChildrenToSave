<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Teacher </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="event_registration">
        <%@include file="include/header.jsp"%>

        <h2>Teacher Information</h2>
        <!--<form:form method="POST" action="addEvent.htm" modelAttribute="event">-->
           <div>
            <div class="row_div">
                <div class="row_div_left">
                    <div class="caption_div"><form:label path="userId">User ID</form:label></div>
                    <div class="field_div row_div_field_left_joint"><c:out value="${teacher.userId}"/></div>
               </div>
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="name">Login</form:label></div>
                    <div class="field_div"><c:out value="${teacher.login}"/></div>
                </div>
                
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="addressLine1">Email</form:label></div>
                    <div class="field_div"><c:out value="${teacher.email}"/></div>
                </div>
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="firstName">FirstName</form:label></div>
                    <div class="field_div"><c:out value="${teacher.firstName}"/></div>
                </div>
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="phoneNumber">Phone</form:label></div>
                    <div class="field_div"><c:out value="${teacher.phoneNumber}"/></div>
                </div>
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="state">School Id</form:label></div>
                    <div class="field_div"><c:out value="${teacher.schoolId}"/></div>
                </div>
                
                
            </div>

            
        </div>
        <!--</form:form>-->
        <%@include file="include/footer.jsp"%>
    </body>
</html>