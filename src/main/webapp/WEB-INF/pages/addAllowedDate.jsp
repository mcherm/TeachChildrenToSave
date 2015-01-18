<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Add Allowed Date</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="addAllowedDate">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <%@include file="include/errors.jsp"%>

                <form:form method="POST" action="addAllowedDate.htm" modelAttribute="formData">

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">Date (yyyy-mm-dd)</div>
                                <form:input path="parsableDateStr"/>
                            </div>
                        </label>
                    </div>

                    <button type="submit" value="Create">Create</button>

                </form:form>

                <div class="cancelBlock">
                    <button onclick="js.loadURL('listAllowedDates.htm')" class="editOrRegister delete">Cancel</button>
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
