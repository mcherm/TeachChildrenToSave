<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Edit Bank</title>
        <%@include file="include/commonHead.jsp"%>
        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
    </head>
    <body class="editBank">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Edit Bank<c:if test="${canEditAdmins}"> (and Bank Admin)</c:if></h1>

                <%@include file="include/errors.jsp"%>

                <div>

                    <form:form method="POST" action="editBank.htm" modelAttribute="formData">

                        <form:hidden path="bankId"/>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">Bank Name</div>
                                    <form:input path="bankName"/>
                                </div>
                            </label>
                        </div>

                        <div class="formElementCnt">
                            <div class="inputCnt">
                                <div class="info">
                                    If you like, you can mark certain schools as eligible for CRA
                                    credits (and other schools as not eligible) for volunteers from
                                    this bank.
                                </div>
                                <div>
                                    <label>
                                        <input type="radio" name="craRadio" onclick="$('#minLMIForCRA').val('')" <c:if test="${formData.minLMIForCRA == ''}">checked</c:if>/>
                                        Do not mark any schools as being CRA eligible.
                                    </label>
                                    <br/>
                                    <label>
                                        <input type="radio" name="craRadio" <c:if test="${formData.minLMIForCRA != ''}">checked</c:if>/>
                                        Mark all schools with an LMI
                                    </label>
                                    of <form:input path="minLMIForCRA"/> or higher as CRA eligible, and schools
                                    with a lower LMI or no recorded LMI (including private schools) as not CRA
                                    eligible.
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="editOrRegister" value="Edit">Save</button>

                    </form:form>

                </div>

                <br>

                <div>
                    <button onclick="js.loadURL('<c:out value="${cancelURL}"/>')" class="editOrRegister cancel">Cancel</button>
                </div>

                <c:if test="${canEditAdmins}">
                    <div class="listOfBankAdmins">
                        <h2>Bank Admin(s)</h2>

                        <table class="responsive">
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col">Email</th>
                                    <th scope="col">Phone</th>
                                    <th scope="col">
                                        <span class="ada-read">Column of Remove As Admin buttons</span>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty bankAdmins}">
                                    <tr>
                                        <td colspan="3" class="emptyTableMessage">No Bank Admin.</td>
                                    </tr>
                                </c:if>
                                <c:forEach items="${bankAdmins}" var="bankAdmin">
                                    <tr>
                                        <td data-title="Bank Admin" class="center">
                                            <c:out value="${bankAdmin.firstName}"/>
                                            <c:out value="${bankAdmin.lastName}"/>
                                        </td>
                                        <td class="center" data-title="Bank Admin Email">
                                            <c:out value="${bankAdmin.email}"/>
                                        </td>
                                        <td class="center" data-title="Bank Admin Phone">
                                            <c:out value="${bankAdmin.phoneNumber}"/>
                                        </td>
                                        <td class="action">
                                            <form method="POST" id="formData" action="markAsVolunteer.htm">
                                                <input type="hidden" id="userId" name="userId" value="<c:out value="${bankAdmin.userId}"/>"/>
                                                <input type="hidden" id="bankId" name="bankId" value="<c:out value="${formData.bankId}"/>"/>
                                                <button class="editOrRegister" type="submit">Mark&nbsp;as Volunteer</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div class="horiz-buttons">
                            <button class="editOrRegister"
                                    onclick="js.loadURL('markAsBankAdmin.htm?bankId=<c:out value="${formData.bankId}"/>')">
                                Mark Volunteer as Bank Admin
                            </button>
                            <button class="editOrRegister"
                                    onclick="js.loadURL('newBankAdmin.htm?bankId=<c:out value="${formData.bankId}"/>');">
                                Make New Bank Admin
                            </button>
                        </div>
                    </div>
                </c:if>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
