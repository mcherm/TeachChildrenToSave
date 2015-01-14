<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title>Teach Children To Save - Schools Information </title>
    <%@include file="include/commonHead.jsp"%>

</head>
<body class="schools">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Schools Information</h1>
            
            <div id="actions">

                <h2>Actions</h2>

                <ul class="noUl">
                    <li class="mb1">
                        <button onclick="js.loadURL('addSchool.htm')" class="editOrRegister">Add New School</button>
                    </li>

                    <li class="mb1">
                        <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                    </li>
                </ul>
            </div>
			<a download="schools.xls" href="#" onclick="return ExcellentExport.excel(this, 'approvedVolunteersTable', 'schools');">Export to Excel</a>
            <table id="approvedVolunteersTable">
                <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Address</th>
                    <th scope="col">City</th>
                    <th scope="col">State</th>
                    <th scope="col">Zip</th>
                    <th scope="col">County</th>
                    <th scope="col">District</th>
                    <th scope="col">Phone</th>
                    <th scope="col">LMI Eligible</th>
                    <th scope="col">SLC</th>
                    <th scope="col">
                        <span class="ada-read">Column of Delete buttons</span>
                    </th>
                    <th scope="col">
                        <span class="ada-read">Column of Modify buttons</span>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${empty schools}">
                    <td colspan="12" class="emptyTableMessage">There are no schools.</td>
                </c:if>
                <c:forEach var="school" items="${schools}">
                <tr>
                    <td><c:out value="${school.name}"/></td>
                    <td><c:out value="${school.addressLine1}"/></td>
                    <td><c:out value="${school.city}"/></td>
                    <td class="center"><c:out value="${school.state}"/></td>
                    <td><c:out value="${school.zip}"/></td>
                    <td><c:out value="${school.county}"/></td>
                    <td><c:out value="${school.schoolDistrict}"/></td>
                    <td><c:out value="${school.phone}"/></td>
                    <td class="center"><c:out value="${school.lmiEligible}"/></td>
                    <td><c:out value="${school.SLC}"/></td>
                    <td>
                        <form method="POST" action="deleteSchool.htm" modelAttribute="formData">
                                    <input type="hidden" name="schoolId" value='<c:out value="${school.schoolId}"/>' />
                                    <button type="submit" class="editOrRegister delete">Delete</button>
                        </form>
                    </td>
                    <td>
                        <button onclick="js.loadURL('editSchool.htm?schoolId=<c:out value="${school.schoolId}"/>');" class="editOrRegister">
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