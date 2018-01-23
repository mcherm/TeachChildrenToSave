<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Edit Personal Data</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="editPersonalData">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>
                    Edit Your Account
                </h1>

                <%@include file="include/errors.jsp"%>

                <form:form method="POST" action="editVolunteerPersonalData.htm" modelAttribute="formData">

                    <form:hidden path="userId"/>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    First Name
                                </div>
                                <form:input path="firstName"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Last Name
                                </div>
                                <form:input path="lastName"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Email
                                </div>
                                <form:input path="email"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Suite/Floor Number
                                </div>
                                <form:input path="suiteOrFloorNumber"/>
                            </div>
                        </label>
                    </div>


                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Street Address
                                </div>
                                <form:input path="streetAddress"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    City
                                </div>
                                <form:input path="city"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    State
                                </div>
                                <form:input path="State"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Zip Code
                                </div>
                                <form:input path="zip"/>
                            </div>
                        </label>
                    </div>

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

                    <c:if test="${not empty bankSpecificFieldLabel}">
                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        <c:out value="${bankSpecificFieldLabel}"/>
                                    </div>
                                    <form:input path="bankSpecificData"/>
                                </div>
                            </label>
                        </div>
                    </c:if>

                    <button type="submit" value="Save">Save</button>

                    <button type="button" onclick="js.loadURL('${cancelURL}')">Cancel</button>

                </form:form>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
