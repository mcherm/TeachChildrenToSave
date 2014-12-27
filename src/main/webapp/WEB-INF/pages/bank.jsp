<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>

        <title>Teach Children To Save - Bank </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="event_registration">
        <%@include file="include/header.jsp"%>

        <h2>Bank Information</h2>
        <form:form method="POST" action="addEvent.htm" modelAttribute="event">
           <div>
            <div class="row_div">
                <div class="row_div_left">
                    <div class="caption_div"><form:label path="bankId">Bank ID</form:label></div>
                    <div class="field_div row_div_field_left_joint"><c:out value="${bank.bankId}"/></div>
               </div>
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="bankAdminId">Bank Admin ID</form:label></div>
                    <div class="field_div"><c:out value="${bank.bankAdminId}"/></div>
                </div>
                
                <div class="row_div_right">
                    <div class="caption_div"><form:label path="bankAdminId">Bank Name</form:label></div>
                    <div class="field_div"><c:out value="${bank.bankName}"/></div>
                </div>
                
            </div>

            
        </div>
        <!--</form:form>-->
        <%@include file="include/footer.jsp"%>
    </body>
</html>