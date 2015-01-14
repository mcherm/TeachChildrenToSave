<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Admin Edit Allowed Times</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="adminEditAllowedTimes">

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
                            <button onclick="alert('not implemented yet')" class="editOrRegister">Add Time</button>
                        </li>

                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>

                    </ul>
                </div>
			<a download="allowedTimes.xls" href="#" onclick="return ExcellentExport.excel(this, 'adminEditAllowedTimesTable', 'allowedTimes');">Export to Excel</a>
                <table id="adminEditAllowedTimesTable">
                    <thead>
                        <tr>
                            <th scope="col" class="time">Time</th>
                            <th scope="col">
                                <span class="ada-read">Column of Modify buttons</span>
                            </th>
                            <th scope="col">
                                <span class="ada-read">Column of Delete buttons</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty allowedTimes}">
                            <td colspan="3" class="emptyTableMessage">There are no allowed times now.</td>
                        </c:if>
                        <c:forEach var="time" items="${allowedTimes}">
                            <tr>
                                <td class="timeColumn"><c:out value="${time}"/></td>
                                
                                <td>
			                        <form method="POST" action="allowedTimeDelete.htm" modelAttribute="formData">
			                                    <input type="hidden" name="time" value='<c:out value="${time}"/>' />
			                                    <button type="submit" class="editOrRegister delete">Delete</button>
			                        </form>
		                    	</td>
			                    <td>
			                        <button onclick="js.loadURL('editAllowedTime.htm?time=<c:out value="${time}"/>');" class="editOrRegister">
			                            Modify
			                        </button>
			                    </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>


                <div class="qa-notes">
                    This table LOOKS all right, but so far the buttons don't DO anything.
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
