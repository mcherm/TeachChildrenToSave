<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Manage Passwords </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="managePasswords">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Manage Passwords</h1>

                <table id="usersTable" class="responsive">
                    <thead>
                        <tr>
                            <th scope="col">User ID</th>
                            <th scope="col">Email</th>
                            <th scope="col">First Name</th>
                            <th scope="col">Last Name</th>
                            <th scope="col">User Type</th>
                            <th scope="col">Pwd Reset Link</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td data-title="User ID"><c:out value="${user.userId}"/></td>
                                <td data-title="Email"><c:out value="${user.email}"/></td>
                                <td data-title="First Name"><c:out value="${user.firstName}"/></td>
                                <td data-title="Last Name"><c:out value="${user.lastName}"/></td>
                                <td data-title="User Type"><c:out value="${user.userType.displayName}"/></td>
                                <td data-title="Pwd Reset Link"><a href="${passwordResetUrls.get(user.userId)}">Link</a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>

    </body>
</html>