<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Admin Edit Allowed Times</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="adminEditAllowedTimes">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <table id="adminEditAllowedTimesTable">
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="time" items="${allowedTimes}">
                            <tr>
                                <td><c:out value="${time}"/></td>
                                <td><button onclick="" class="editOrRegister">Modify</button></td>
                                <td><button onclick="" class="editOrRegister delete">Delete</button></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="addButton">
                    <button onclick="" class="editOrRegister add">Add</button>
                </div>

                <div class="qa-notes">
                    This table LOOKS all right, but so far the buttons don't DO anything.
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
