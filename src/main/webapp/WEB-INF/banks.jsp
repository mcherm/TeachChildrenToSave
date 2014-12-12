<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Bank </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="event_registration">
        <%@include file="include/header.jsp"%>

        <h2>Bank Information</h2>
       
        <%@include file="include/footer.jsp"%>
        <div id="bankList">
        <table id="approvedVolunteersTable" class="displayTable">
                <thead>
                    <tr>
                        <th>Bank ID</th>
                        <th>Bank Name Name</th>
                        <th>Bank admin Id</th>
                        <th>1</th>
                        <th>2</th>
                        
                       
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="bank" items="${banks}">
                    
                        <tr>
                            <td><c:out value="${bank.bankId}"/></td>
                            <td><c:out value="${bank.bankName}"/></td>
                            <td><c:out value="${bank.bankAdminId}"/></td>
                            <td><a href="delete.htm">Delete </a></td>
                            <td><a href="show.htm">Modify </a></td>
                        </tr>
                    
                </c:forEach>
                
                </tbody>
            </table>
            </div>
            
    </body>
</html>