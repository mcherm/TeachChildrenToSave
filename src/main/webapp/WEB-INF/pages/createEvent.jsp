<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Create New Course</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="createEvent">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Register a New Class</h1>

                <%@include file="include/errors.jsp"%>

                <form:form method="POST" action="createEvent.htm" modelAttribute="formData">
                    <c:if test = "${calledBy == 'siteAdmin'}">
                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Teacher
                                    </div>
                                    <form:select path="teacherId">
                                        <form:option value="" label="Select One..."/>
                                        <c:forEach var="aTeacher" items="${teachers}">
                                            <form:option value="${aTeacher.userId}" label="${aTeacher.lastName}, ${aTeacher.firstName} "/>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </label>
                        </div>
                    </c:if>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Date
                                </div>
                                <form:select path="eventDate">
                                    <form:option value="" label="- Select Date -" />
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
                                    <form:option value="" label="- Select Time -" />
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
                                        <form:option value="" label="Select One..." />
                                        <form:options items="${allowedGrades}"/>
                                    </form:select>
                                </div>
                            </label>
                        </div>
                    </c:if>
                    <c:if test="${!showGradeColumn}">
                        <input type="hidden" id="grade" name="grade" value="${allowedGrades[0]}"/>
                    </c:if>

                    <c:if test="${showDeliveryMethodColumn}">
                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Delivery Method
                                    </div>
                                    <form:select path="deliveryMethod">
                                        <form:option value="" label="Select One..." />
                                        <form:options items="${allowedDeliveryMethods}"/>
                                    </form:select>
                                </div>
                            </label>
                        </div>
                    </c:if>
                    <c:if test="${!showDeliveryMethodColumn}">
                        <input type="hidden" id="deliveryMethod" name="deliveryMethod" value="${allowedDeliveryMethods[0]}"/>
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

                   <button type="submit" value="Register">Register</button>

                </form:form>

			</main>

		</div><%-- mainCnt --%>			        
		               
        
        <%@include file="include/footer.jsp"%>
    </body>
</html>