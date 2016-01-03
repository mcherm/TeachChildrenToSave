<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - View Site Settings</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="viewSiteSettings">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Site Settings</h1>

                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                    </ul>
                </div>

                <div>

                    <table class="responsive">
                        <thead>
                            <tr>
                                <th scope="col">Setting Name</th>
                                <th scope="col">Setting Value</th>
                                <th scope="col">
                                    <span class="ada-read">Column of Edit buttons</span>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty siteSettings}">
                                <td colspan="2" class="emptyTableMessage">There are no siite settings.</td>
                            </c:if>
                            <c:forEach var="siteSettingEntry" items="${siteSettings.entrySet()}">
                                <tr>
                                    <td>${siteSettingEntry.key}</td>
                                    <td>${siteSettingEntry.value}</td>
                                    <td class="action">
                                        <button onclick="js.loadURL('showEditSiteSetting.htm?settingToEdit=${siteSettingEntry.key}')" class="editOrRegister">
                                            Edit
                                        </button>
                                    </td>
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
