<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Bank </title>
        <%@include file="include/commonHead_innerPage.jsp"%>

    </head>
    <body class="banks">

    <a href="#main" class="ada-read">Skip to main content</a>

    <div class="decor"></div>

    <%@include file="include/header_innerPage.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Bank Information</h1>

            <table id="approvedVolunteersTable">
                <thead>
                <tr>
                    <th scope="col" class="center">Bank ID</th>
                    <th scope="col">Bank Name Name</th>
                    <th scope="col" class="center">Bank admin Id</th>
                    <th scope="col" class="center">
                        <span class="ada-read">Column of Delete buttons</span>
                    </th>
                    <th scope="col" class="center">
                        <span class="ada-read">Column of Modify buttons</span>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="bank" items="${banks}">
                <tr>
                    <td class="center"><c:out value="${bank.bankId}"/></td>
                    <td><c:out value="${bank.bankName}"/></td>
                    <td class="center"><c:out value="${bank.bankAdminId}"/></td>
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