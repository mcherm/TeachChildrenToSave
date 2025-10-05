<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Edit Course</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="createEvent">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Edit a Class</h1>

                <c:if test="${not empty errorMessage}">
                    <div class="errorMessage">
                        <c:out value="${errorMessage}" default=""  />
                    </div>
                </c:if>

                <form:form method="POST" action="editEvent.htm" modelAttribute="formData">
					<form:hidden path="eventId"/>
                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Date
                                </div>
                                <form:select path="eventDate">
                                    <form:options items="${allowedDates}" itemValue="parseable" itemLabel="pretty"/>
                                </form:select>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Time (approximate)
                                </div>
                                <form:select path="eventTime">
                                    <form:options items="${allowedTimes}"/>
                                </form:select>
                            </div>
                        </label>
                    </div>
                    <c:if test="${showGradeColumn}">
                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Grade
                                    </div>
                                    <form:select path="grade">
                                        <form:options items="${allowedGrades}"/>
                                    </form:select>
                                </div>
                            </label>
                        </div>
                    </c:if>

                    <c:if test="${showDeliveryMethodColumn}">
                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Delivery Method
                                    </div>
                                    <form:select path="deliveryMethod">
                                        <form:options items="${allowedDeliveryMethods}"/>
                                    </form:select>
                                </div>
                            </label>
                        </div>
                    </c:if>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Number of Students
                                </div>
                                <form:input path="numberStudents"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Notes for the Volunteer
                                </div>
                                <form:textarea path="notes"/>
                            </div>
                        </label>
                    </div>

                   <button type="submit" value="Register">Save</button>

                </form:form>

			</main>

		</div><%-- mainCnt --%>			        
		               
        
        <%@include file="include/footer.jsp"%>
    </body>
</html>