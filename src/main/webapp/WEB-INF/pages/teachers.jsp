<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Teachers </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="teachers">

    <a href="#main" class="ada-read">Skip to main content</a>

    <div class="decor"></div>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Teachers Information</h1>

                <table id="approvedVolunteersTable">
                    <thead>
                    <tr>
                        <th scope="col">User ID</th>
                        <th scope="col">Email</th>
                        <th scope="col">Password</th>
                        <th scope="col">First Name</th>
                        <th scope="col">Last Name</th>
                        <th scope="col">User Type</th>
                        <th scope="col">Phone Number</th>
                        <th scope="col">Schoold Id</th>
                        <th scope="col">Linked School</th>
                        <th scope="col">
                            <span class="ada-read">Column of Delete buttons</span>
                        </th>
                        <th scope="col">
                            <span class="ada-read">Column of Modify buttons</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty teachers}">
                            <td colspan="11" class="emptyTableMessage">There are no teachers.</td>
                        </c:if>
                    <c:forEach var="teacher" items="${teachers}">
                    <tr>
                        <td><c:out value="${teacher.userId}"/></td>
                        <td><c:out value="${teacher.email}"/></td>
                        <td>
                            <%-- password throws page error --%>
                            <%--<c:out value="${teacher.password}"/> --%>
                        </td>
                        <td><c:out value="${teacher.firstName}"/></td>
                        <td><c:out value="${teacher.lastName}"/></td>
                        <td>Teacher</td>
                        <td><c:out value="${teacher.phoneNumber}"/></td>
                        <td class="center"><c:out value="${teacher.schoolId}"/></td>
                        <td><c:out value="${teacher.linkedSchool}"/></td>
                        
                        <td>
	                        <form method="POST" action="deleteUser.htm" modelAttribute="formData">
	                                    <input type="hidden" name="userId" value='<c:out value="${teacher.userId}"/>' />
	                                    <button type="submit" class="editOrRegister delete">Delete</button>
	                        </form>
	                    </td>
	                    <td>
	                        <button onclick="js.loadURL('editTeacherData.htm?userId=<c:out value="${teacher.userId}"/>');" class="editOrRegister">
	                            Modify
	                        </button>
	                    </td>
                    </tr>
                    </c:forEach>
                    </tbody>
                </table>

            </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>

    </body>
</html>