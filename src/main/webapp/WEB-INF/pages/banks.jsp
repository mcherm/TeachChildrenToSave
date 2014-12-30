<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Bank </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="banks">

    <a href="#main" class="ada-read">Skip to main content</a>

    <div class="decor"></div>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Bank Information</h1>

            <table id="approvedVolunteersTable">
                <thead>
                    <tr>
                        <th scope="col">Bank Name</th>
                        <th scope="col" class="center">Bank admin name</th>
                        <th scope="col" class="center">Bank admin email</th>
                        <th scope="col" class="center">Bank admin phone</th>
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
                            <td><c:out value="${bank.bankName}"/></td>
                            <td class="center">
                                <c:out value="${bank.linkedBankAdmin.firstName}"/>
                                <c:out value="${bank.linkedBankAdmin.lastName}"/>
                            </td>
                            <td class="center"><c:out value="${bank.linkedBankAdmin.email}"/></td>
                            <td class="center"><c:out value="${bank.linkedBankAdmin.phoneNumber}"/></td>
                            <td>
                                <form method="POST" action="deleteBank.htm" modelAttribute="formData">
                                    <input type="hidden" name="bankId" value='<c:out value="${bank.bankId}"/>' />
                                    <button type="submit" class="editOrRegister delete">Delete</button>
                                </form>
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

            <div>
                <button onclick="js.loadURL('addBank.htm')" class="editOrRegister">Add New Bank</button>
            </div>

        </main>
    </div><%-- mainCnt --%>

     <%@include file="include/footer.jsp"%>

    </body>
</html>
