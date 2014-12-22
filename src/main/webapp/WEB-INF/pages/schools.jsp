<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title>Teach Children To Save - Schools Information </title>
    <%@include file="include/commonHead_innerPage.jsp"%>

</head>
<body class="schools">

    <a href="#main" class="ada-read">Skip to main content</a>

    <div class="decor"></div>

    <%@include file="include/header_innerPage.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Schools Information</h1>

            <table id="approvedVolunteersTable">
                <thead>
                <tr>
                    <th scope="col" class="center">School ID</th>
                    <th scope="col">Address1</th>
                    <th scope="col">Address2</th>
                    <th scope="col">City</th>
                    <th scope="col">State</th>
                    <th scope="col">Zip</th>
                    <th scope="col">County</th>
                    <th scope="col">District</th>
                    <th scope="col">Phone</th>
                    <th scope="col">
                        <span class="ada-read">Column of Delete buttons</span>
                    </th>
                    <th scope="col">
                        <span class="ada-read">Column of Modify buttons</span>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="school" items="${schools}">
                <tr>
                    <td class="center"><c:out value="${school.schoolId}"/></td>
                    <td><c:out value="${school.addressLine1}"/></td>
                    <td><c:out value="${school.addressLine2}"/></td>
                    <td><c:out value="${school.city}"/></td>
                    <td><c:out value="${school.state}"/></td>
                    <td><c:out value="${school.zip}"/></td>
                    <td><c:out value="${school.county}"/></td>
                    <td><c:out value="${school.schoolDistrict}"/></td>
                    <td><c:out value="${school.phone}"/></td>
                    <td>
                        <button onclick="js.loadURL('delete.htm');" class="editOrRegister delete">
                            Delete
                        </button>
                    </td>
                    <td>
                        <button onclick="js.loadURL('show.htm');" class="editOrRegister">
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