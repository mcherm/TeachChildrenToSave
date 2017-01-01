<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Admin Edit Allowed Times</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="listAllowedDates">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>
                    Edit Allowed Dates
                </h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('addAllowedDate.htm')" class="editOrRegister">Add Date</button>
                        </li>

                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                     	
                    </ul>
                </div>

				<a download="allowedDates.xls" href="#" class="downloadExcel" onclick="return ExcellentExport.excel(this, 'listAllowedDatesTable', 'allowedDates');">Export to Excel</a>

                <table id="listAllowedDatesTable" class="responsive">
                    <thead>
                        <tr>
                            <th scope="col" class="time">Date</th>
                            <th scope="col">
                                <span class="ada-read">Column of Delete buttons</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty allowedDates}">
                            <td colspan="3" class="emptyTableMessage">There are no allowed dates now.</td>
                        </c:if>
                        <c:forEach var="allowedDate" items="${allowedDates}">
                            <tr>
                                <td class="dateColumn" data-title="Date"><c:out value="${allowedDate.pretty}"/></td>
                                <td class="action">
			                        <form method="POST" action="deleteAllowedDate.htm" modelAttribute="formData">
			                                    <input type="hidden" name="parseableDateStr" value='<c:out value="${allowedDate.parseable}"/>' />
			                                    <button type="submit" class="editOrRegister delete">Delete</button>
			                        </form>
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
