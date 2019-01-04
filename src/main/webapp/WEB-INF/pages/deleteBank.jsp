<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Delete Bank </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="volunteerInformation">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1> Bank Delete Confirm </h1>

                    <h2>If you delete a bank the associated bank admin and volunteers shown below will also be deleted.
                    The volunteers will be withdrawn from any classes they are signed up for and a withdrawal notice will be emailed to the teacher
                    informing them that their volunteer had to cancel and their class will be relisted for a new volunteer.</h2>

                <div id="actions">

                    <h2>Actions</h2>
                    <td class="action">
                        <form method="POST" action="deleteBank.htm" modelAttribute="formData">
                            <input type="hidden" name="bankId" value='<c:out value="${bank.bankId}"/>' />
                            <button type="submit" class="editOrRegister delete">Delete ${bank.bankName} and Volunteers</button>
                        </form>
                    </td>
                  </ul>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('viewEditBanks.htm')" class="editOrRegister cancel">Cancel</button>
                        </li>

                    </ul>
                </div>

                <h2> ${bank.bankName} Volunteers to be Deleted</h2>
                <a download="volunteers.xls" href="excel/deletedBankVolunteers/<c:out value="${bank.bankId}"/>.htm" class="downloadExcel">Export to Excel</a>

                <table id="volunteersToBeDeletedTable" class="responsive">
                    <thead>
                        <tr>
                            <th scope="col">User ID</th>
                            <th scope="col">Email</th>
                            <th scope="col">First Name</th>
                            <th scope="col">Last Name</th>
                            <th scope="col">User Type</th>
                            <th scope="col">Phone Number</th>
                     </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty volunteers}">
                            <td colspan="6" class="emptyTableMessage">There are currently no bank admin or volunteers associated with this bank.</td>
                        </c:if>

                        <c:forEach var="volunteer" items="${volunteers}">
                            <tr>
                                <td data-title="User ID"><c:out value="${volunteer.userId}"/></td>
                                <td data-title="Email"><c:out value="${volunteer.email}"/></td>
                                <td data-title="First Name"><c:out value="${volunteer.firstName}"/></td>
                                <td data-title="Last Name"><c:out value="${volunteer.lastName}"/></td>
                                <td data-title="User Type"><c:out value="${volunteer.userType.getDisplayName()}"/></td>
                                <td data-title="Phone Number"><c:out value="${volunteer.phoneNumber}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>

    </body>
</html>