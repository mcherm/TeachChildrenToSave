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
                        <th>School ID</th>
                        <th>Address1</th>
                        <th>Address2</th>
                        <th>City</th>
                        <th>Zip</th>
                        <th>County</th>
                        <th>District</th>
                        <th>Phone</th>
                        <th>1</th>
                        <th>2</th>
                         
                        
                       
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="school" items="${schools}">
                    
                        <tr>
                            <td><c:out value="${school.schoolId}"/></td>
                            <td><c:out value="${school.addressLine1}"/></td>
                            <td><c:out value="${school.addressLine2}"/></td>
                            <td><c:out value="${school.city}"/></td>
                            <td><c:out value="${school.state}"/></td>
                            <td><c:out value="${school.zip}"/></td>
                            <td><c:out value="${school.county}"/></td>
                            <td><c:out value="${school.schoolDistrict}"/></td>
                            <td><c:out value="${school.phone}"/></td>
                            <td><a href="delete.htm">Delete </a></td>
                            <td><a href="show.htm">Modify </a></td>
                        </tr>
                    
                </c:forEach>
                
                </tbody>
            </table>
            </div>
            
    </body>
</html>