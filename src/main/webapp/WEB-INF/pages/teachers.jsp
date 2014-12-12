<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Schools </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="event_registration">
        <%@include file="include/header.jsp"%>

        <h2>Schools Information</h2>
       
        <%@include file="include/footer.jsp"%>
        <div id="bankList">
        <table id="approvedVolunteersTable" class="displayTable">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Login</th>
                        <th>Email</th>
                        <th>Password</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>User Type</th>
                        <th>Phone Number</th>
                        <th>Schoold Id</th>
                        <th>Linked School</th>
                        <th>1</th>
                        <th>2</th>
                         
                        
                       
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="teacher" items="${teachers}">
                    
                        <tr>
                            <td><c:out value="${teacher.userId}"/></td>
                            <td><c:out value="${teacher.login}"/></td>
                            <td><c:out value="${teacher.email}"/></td>
                            <td><c:out value="${teacher.password}"/></td>
                            <td><c:out value="${teacher.firstName}"/></td>
                            <td><c:out value="${teacher.lastName}"/></td>
                            <td>Teacher</td>
                            <td><c:out value="${teacher.phoneNumber}"/></td>
                            <td><c:out value="${teacher.schoolId}"/></td>
                            <td><c:out value="${teacher.linkedSchool}"/></td>
                            <td><a href="delete.htm">Delete </a></td>
                            <td><a href="show.htm">Modify </a></td>
                        </tr>
                    
                </c:forEach>
                
                </tbody>
            </table>
            </div>
            
    </body>
</html>