<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Add Volunteer</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="volunteer_registration">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Volunteer Information</h1>

                <form:form method="POST" action="addVolunteer.htm" modelAttribute="volunteer">

                    <%-- error example...if executing this is possible, given our timelines --%>
                    <%-- 1. add the css class 'error' to the label --%>
                    <%-- 2. include 'errorText' span --%>
                    <%-- we should (if possible) also update the H1 tag as well, appending something like: ", form has errors" --%>
					<%--
                    <div class="formElementCnt">
                        <label class="error">
                            <div class="inputCnt">
                                <div class="info">
                                <span class="errorText">
                                    Error:
                                </span>
                                    First Name
                                </div>
                                <input type="text" />
                            </div>
                        </label>
                    </div>
					 --%>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    First Name
                                </div>
                                <form:input path="firstName" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Last Name
                                </div>
                                <form:input path="lastName" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Email Address
                                </div>
                                <form:input path="emailAddress" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Confirm Email Address
                                </div>
                                <form:input path="confirmEmailAddress" />
                            </div>
                        </label>
                    </div>

                    <!-- FIXME: May need to allow editing the password, but that should be a separate call
                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Password
                                </div>
                                <form:input type="password" path="password" />
                            </div>
                        </label>
                    </div>
                    -->

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Confirm Password
                                </div>
                                <form:input type="password" path="confirmPassword" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Work Address Line 1
                                </div>
                                <form:input path="addressLine1" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Work Address Line 2
                                </div>
                                <form:input path="addressLine2" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    City
                                </div>
                                <form:input path="city" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    State
                                </div>
                                <form:input path="state" />
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Zip
                                </div>
                                <form:input path="zipcode" />
                            </div>
                        </label>
                    </div>

                    <button type="submit" value="Submit">Submit</button>

                </form:form>

            </main>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>

    </body>
</html>