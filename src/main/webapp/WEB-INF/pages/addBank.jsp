<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Add Bank</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Add New Bank (and Bank Admin)</h1>

                <c:if test="${not empty errorMessage}">
                    <div class="errorMessage">
                        <c:out value="${errorMessage}" default=""  />
                    </div>
                </c:if>

                <div>

                    <form:form method="POST" action="addBank.htm" modelAttribute="formData">

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

                        <button type="submit" value="Create">Create</button>

                    </form:form>

                </div>
                <div>
                    <button onclick="js.loadURL('viewEditBanks.htm')" class="editOrRegister cancel">Cancel</button>
                </div>

                <div>
                    NOTE: Your newly created bank admin will need to use password reset to log on for the first time.
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>