<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Bank Administrator Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="bankAdminHome">
        <%@include file="include/header.jsp"%>
        <h1>Your Home Page</h1>
        <div id="actions">
            <h2>Actions</h2>
            <ul>
                <li><a href="registerForEvent.htm">Register volunteers for classes</a></li>
                <li><a href="manageVolunteers.htm">Manage my bank's volunteers</a></li>
                <li><a href="editPersonalData.htm">Edit my Data</a></li>
            </ul>
        </div>
        <div id="volunteers">
            <h2>Unapproved Volunteers</h2>
            <table id="unapprovedVolunteersTable" class="displayTable">
                <thead>
                    <tr>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="volunteer" items="${volunteers}">
                        <c:if test="${!volunteer.approved}">
                            <tr>
                                <td><c:out value="${volunteer.firstName}"/></td>
                                <td><c:out value="${volunteer.lastName}"/></td>
                                <td><c:out value="${volunteer.email}"/></td>
                                <td><a href="approveVolunteer.htm?volunteerId=<c:out value="${volunteer.userId}"/>">Approve</a></td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>

            <h2>Approved Volunteers</h2>
            <table id="approvedVolunteersTable" class="displayTable">
                <thead>
                    <tr>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th>Classes Registered</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="volunteer" items="${volunteers}">
                    <c:if test="${volunteer.approved}">
                        <tr>
                            <td><c:out value="${volunteer.firstName}"/></td>
                            <td><c:out value="${volunteer.lastName}"/></td>
                            <td><c:out value="${volunteer.email}"/></td>
                            <td><div class="qa-notes">not implemented yet</div></td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
