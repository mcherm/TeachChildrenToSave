<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Edit Bank</title>
        <%@include file="include/commonHead.jsp"%>
        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
    </head>
    <body class="">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Edit Bank (and Bank Admin)</h1>

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
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Bank Admin First Name
                                    </div>
                                    <form:input path="firstName" />
                                </div>
                            </label>
                        </div>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Bank Admin Last Name
                                    </div>
                                    <form:input path="lastName" />
                                </div>
                            </label>
                        </div>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Bank Admin Email Address
                                    </div>
                                    <form:input path="email" />
                                </div>
                            </label>
                        </div>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Bank Admin Phone Number
                                    </div>
                                    <form:input path="phoneNumber" />
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

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
