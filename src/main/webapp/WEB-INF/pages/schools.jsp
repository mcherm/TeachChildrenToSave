<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
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

			<a download="schools.xls" href="#" class="downloadExcel" onclick="return ExcellentExport.excel(this, 'approvedVolunteersTable', 'schools');">Export to Excel</a>

            <table id="approvedVolunteersTable" class="responsive">
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
                    <th scope="col">Delete
                        <span class="ada-read">Column of Delete buttons</span>
                    </th>
                    <th scope="col">Modify
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
                    <td data-title="Name"><c:out value="${school.name}"/></td>
                    <td data-title="Address"><c:out value="${school.addressLine1}"/></td>
                    <td data-title="City"><c:out value="${school.city}"/></td>
                    <td class="center" data-title="State"><c:out value="${school.state}"/></td>
                    <td data-title="Zip"><c:out value="${school.zip}"/></td>
                    <td data-title="County"><c:out value="${school.county}"/></td>
                    <td data-title="District"><c:out value="${school.schoolDistrict}"/></td>
                    <td data-title="Phone"><c:out value="${school.phone}"/></td>
                    <td class="center" data-title="LMI Eligible"><c:out value="${school.lmiEligible}"/></td>
                    <td data-title="SLC"><c:out value="${school.SLC}"/></td>
                    <td class="action">
                        <form method="GET" action="deleteSchool.htm" modelAttribute="formData">
                                    <input type="hidden" name="schoolId" value='<c:out value="${school.schoolId}"/>' />
                                    <button type="submit" class="editOrRegister delete">Delete</button>
                        </form>
                    </td>

                    <td class="action">
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