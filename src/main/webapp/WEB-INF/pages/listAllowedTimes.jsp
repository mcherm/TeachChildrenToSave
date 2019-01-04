<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Admin Edit Allowed Times</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="listAllowedTimes">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>
                    Edit Allowed Times
                </h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('addAllowedTime.htm')" class="editOrRegister">Add Time</button>
                        </li>

                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>

                    </ul>
                </div>

			    <a download="allowedTimes.xls" href="excel/allowedTimes.htm" class="downloadExcel">Export to Excel</a>

                <table id="listAllowedTimesTable" class="responsive">
                    <thead>
                        <tr>
                            <th scope="col" class="time">Time</th>
                            <th scope="col">
                                <span class="ada-read">Column of Delete buttons</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty allowedTimes}">
                            <td colspan="3" class="emptyTableMessage">There are no allowed times now.</td>
                        </c:if>
                        <c:forEach var="allowedTime" items="${allowedTimes}">
                            <tr>
                                <td class="timeColumn" data-title="Time"><c:out value="${allowedTime}"/></td>
                                <td class="action">
			                        <form method="POST" action="deleteAllowedTime.htm" modelAttribute="formData">
			                                    <input type="hidden" name="allowedTime" value='<c:out value="${allowedTime}"/>' />
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
