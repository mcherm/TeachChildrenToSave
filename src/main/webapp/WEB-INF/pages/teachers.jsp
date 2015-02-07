<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Teachers </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="teachers">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Teachers Information</h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                    
                    </ul>
                </div>

				<a download="teachers.xls" href="#" class="downloadExcel" onclick="return ExcellentExport.excel(this, 'approvedVolunteersTable', 'teachers');">Export to Excel</a>

                <table id="approvedVolunteersTable" class="responsive">
                    <thead>
                    <tr>
                        <th scope="col">User ID</th>
                        <th scope="col">Email</th>
                        <th scope="col">Password</th>
                        <th scope="col">First Name</th>
                        <th scope="col">Last Name</th>
                        <th scope="col">User Type</th>
                        <th scope="col">Phone Number</th>
                        <th scope="col">SLC</th>
                        <th scope="col">School Name</th>
                        <th scope="col">District Name</th>
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
                        <td data-title="User ID:"><c:out value="${teacher.userId}"/></td>
                        <td data-title="Email"><c:out value="${teacher.email}"/></td>
                        <td data-title="Password">
                            <%-- password throws page error --%>
                            <%--<c:out value="${teacher.password}"/> --%>
                        </td>
                        <td data-title="First Name"><c:out value="${teacher.firstName}"/></td>
                        <td data-title="Last Name"><c:out value="${teacher.lastName}"/></td>
                        <td data-title="User Type">Teacher</td>
                        <td data-title="Phone Number"><c:out value="${teacher.phoneNumber}"/></td>
                        <td class="center" data-title="SLC"><c:out value="${teacher.linkedSchool.SLC}"/></td>
                        <td class="center" data-title="School"><c:out value="${teacher.linkedSchool.name}"/></td>
                        <td class="center" data-title="District"><c:out value="${teacher.linkedSchool.schoolDistrict}"/></td>
                        
                        <td class="action">
	                        <form method="POST" action="teacherDelete.htm" modelAttribute="formData">
	                                    <input type="hidden" name="teacherId" value='<c:out value="${teacher.userId}"/>' />
	                                    <button type="submit" class="editOrRegister delete">Delete</button>
	                        </form>
	                    </td>
	                    <td class="action">
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