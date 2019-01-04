<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Bank </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="banks">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Bank Information</h1>

            <div id="actions">

                <h2>Actions</h2>

                <ul class="noUl">
                    <li class="mb1">
                        <button onclick="js.loadURL('addBank.htm')" class="editOrRegister">Add New Bank</button>

                    </li>

                    <li class="mb1">
                        <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                    </li>
                </ul>
            </div>

			<a download="banks.xls" href="excel/banks.htm" class="downloadExcel">Export to Excel</a>

            <table id="approvedVolunteersTable" class="responsive">
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
                    <c:if test="${empty banks}">
                        <td colspan="6" class="emptyTableMessage">There are no banks.</td>
                    </c:if>
                    <c:forEach var="bank" items="${banks}">
                        <tr>
                            <td data-title="Bank Name"><c:out value="${bank.bankName}"/></td>
                            <td data-title="Bank Admin" class="center">
                                <c:out value="${bank.linkedBankAdmin.firstName}"/>
                                <c:out value="${bank.linkedBankAdmin.lastName}"/>
                            </td>
                            <td class="center" data-title="Bank Admin Email"><c:out value="${bank.linkedBankAdmin.email}"/></td>
                            <td class="center" data-title="Bank Admin Phone"><c:out value="${bank.linkedBankAdmin.phoneNumber}"/></td>
                            <td class="action">
                                <button onclick="js.loadURL('deleteBank.htm?bankId=<c:out value="${bank.bankId}"/>');"
                                        class="editOrRegister delete">Delete</button>
                            </td>
                            <td class="action">
                                <button onclick="js.loadURL('editBank.htm?bankId=<c:out value="${bank.bankId}"/>');" class="editOrRegister">Modify</button>
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
