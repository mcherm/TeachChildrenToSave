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
                            <button onclick="js.loadURL('viewSiteSettings.htm')" class="editOrRegister cancel">Cancel</button>
                        </li>
                    </ul>
                </div>

                <h2>

                    <h2>Editing Setting '${formData.settingName}'</h2>
                    <form:form method="POST" action="editSiteSetting.htm" modelAttribute="formData">

                        <form:hidden path="settingName"/>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">New Value</div>
                                    <form:input path="settingValue"/>
                                </div>
                            </label>
                        </div>

                        <button type="submit" class="editOrRegister" value="Edit">Save</button>

                    </form:form>

                </div>

            </main>

        </div>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
