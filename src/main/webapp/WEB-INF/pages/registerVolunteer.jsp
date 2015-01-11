<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Register New Volunteer</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="registerVolunteer">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">


            <h1>Register a New Volunteer</h1>

            <c:if test="${not empty errorMessage}">
                <div class="errorMessage">
                    <c:out value="${errorMessage}" default=""  />
                </div>
            </c:if>

            <form:form method="POST" action="registerVolunteer.htm" modelAttribute="formData">

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                First Name (*)
                            </div>
                            <form:input path="firstName"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Last Name (*)
                            </div>
                            <form:input path="lastName"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Email (*)
                            </div>
                            <form:input path="email"/>
                        </div>
                    </label>
                </div>

                <%-- TODO: re-enter email field --%>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Password (*)
                            </div>
                            <form:input path="password" type="password"/>
                        </div>
                    </label>
                </div>

                <%-- TODO: re-enter password field --%>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Phone Number
                            </div>
                            <form:input path="phoneNumber"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Select your bank (*)
                            </div>

                            <form:select path="bankId">
                                <form:option value="0" label="- Select Bank -" />
                                <form:options items="${banks}" itemValue="bankId" itemLabel="bankName" />
                            </form:select>
                        </div>
                    </label>
                </div>

                <button type="submit">Register</button>

            </form:form>

            <div>* - Required field</div>

        </main>
    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
    </body>
</html>
