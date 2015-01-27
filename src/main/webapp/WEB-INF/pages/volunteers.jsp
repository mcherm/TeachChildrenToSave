<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Volunteers </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="volunteerInformation">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Volunteers Information</h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                    	
                    </ul>
                </div>
                
				<a download="volunteers.xls" href="#" class="downloadExcel" onclick="return ExcellentExport.excel(this, 'approvedVolunteersTable', 'volunteers');">Export to Excel</a>

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
                        <th scope="col">Bank Name</th>
                        <th scope="col">
                            <span class="ada-read">Column of Delete buttons</span>
                        </th>
                        <th scope="col">
                            <span class="ada-read">Column of Modify buttons</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="volunteer" items="${volunteers}">
                    <tr>
                        <td data-title="User ID"><c:out value="${volunteer.userId}"/></td>
                        <td data-title="Email"><c:out value="${volunteer.email}"/></td>
                        <td data-title="Password">
                            <%-- password throws page error --%>
                            <%--<c:out value="${volunteer.password}"/> --%>
                        </td>
                        <td data-title="First Name"><c:out value="${volunteer.firstName}"/></td>
                        <td data-title="Last Name"><c:out value="${volunteer.lastName}"/></td>
                        <td data-title="User Type">Volunteer</td>
                        <td data-title="Phone Number"><c:out value="${volunteer.phoneNumber}"/></td>
                        <td class="center" data-title="Bank Name"><c:out value="${volunteer.linkedBank.bankName}"/></td>
                                                
                        <td class="action">
	                        <form method="POST" action="deleteUser.htm" modelAttribute="formData">
	                                    <input type="hidden" name="userId" value='<c:out value="${volunteer.userId}"/>' />
	                                    <button type="submit" class="editOrRegister delete">Delete</button>
	                        </form>
	                    </td>
	                    <td class="action">
	                        <button onclick="js.loadURL('editVolunteerData.htm?userId=<c:out value="${volunteer.userId}"/>');" class="editOrRegister">
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