<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">

    <head>
        <title>Teach Children To Save - View Documents</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="documents">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Available Documents</h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                    </ul>
                </div>

                <div>

                    <table class="responsive full-page-table">
                        <thead>
                            <tr>
                                <th scope="col">File Name</th>
                                <th>
                                    <table class="same-size-cols">
                                        <tr>
                                            <th scope="col">Teachers</th>
                                            <th scope="col">Volunteers</th>
                                            <th scope="col">BankAdmin Only</th>
                                            <th scope="col">
                                                <span class="ada-read">Column of Edit buttons</span>
                                            </th>
                                        </tr>
                                    </table>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty documents}">
                                <td colspan="2" class="emptyTableMessage">There are no documents.</td>
                            </c:if>
                            <c:forEach var="aDocument" items="${documents}">
                                <tr>
                                    <form action="editDocument.htm" method="POST">
                                        <td><a href="${s3Util.makeS3URL(aDocument.name)}">${aDocument.name}</a></td>
                                        <td>
                                            <table class="same-size-cols">
                                                <tr>
                                                    <td>
                                                        <input type="hidden" name="name" value="${aDocument.name}">
                                                        <input type="checkbox" name="showToTeacher" <c:if test="${aDocument.showToTeacher}">checked</c:if>>
                                                    </td>
                                                    <td><input type="checkbox" name="showToVolunteer" <c:if test="${aDocument.showToVolunteer}">checked</c:if>></td>
                                                    <td><input type="checkbox" name="showToBankAdmin" <c:if test="${aDocument.showToBankAdmin}">checked</c:if>></td>
                                                    <td class="action">
                                                        <button type="submit" class="editOrRegister smallButton">Update</button>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </form>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                </div>

            </main>

        </div>

        <%@include file="include/footer.jsp"%>

    </body>
</html>
