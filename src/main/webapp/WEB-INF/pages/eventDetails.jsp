<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Event Details</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="eventDetails">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Event Details</h1>

                <c:if test="${showSignupButton}">
                    <div>
                        <form method="POST" action="eventRegistration.htm" modelAttribute="formData">
                            <input type="hidden" name="eventId" value='<c:out value="${event.eventId}"/>' />
                            <button type="submit" class="editOrRegister">Sign Up</button>
                        </form>
                    </div>
                </c:if>

                <div class="section group">
                    <div class="col span_1_of_2">
                        <fieldset>
                            <legend>Class</legend>
                            <div id="eventDateField">
                                <label>Date:</label>
                                <span class="value"><c:out value="${event.eventDate.pretty}"/></span>
                            </div>
                            <div id="eventTimeField">
                                <label>Time:</label>
                                <span class="value"><c:out value="${event.eventTime}"/></span>
                            </div>
                            <div id="gradeField">
                                <label>Grade:</label>
                                <span class="value"><c:out value="${event.grade}"/></span>
                            </div>
                            <div id="numberStudentsField">
                                <label>Students:</label>
                                <span class="value"><c:out value="${event.numberStudents}"/></span>
                            </div>
                            <div id="notesField">
                                <label>Notes:</label>
                                <span class="value"><c:out value="${event.notes}"/></span>
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>Volunteer</legend>


                            <c:choose>
                                <c:when test="${event.linkedVolunteer == null}">
                                    <div id="noVolunteerYet">
                                        <div>No volunteer yet.</div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div id="volunteerNameField">
                                        <label>Volunteer Name:</label>
                                        <span class="value">
                                            <c:out value="${event.linkedVolunteer.firstName}"/>
                                            <c:out value="${event.linkedVolunteer.lastName}"/>
                                        </span>
                                    </div>
                                    <div id="volunteerEmailField">
                                        <label>Volunteer Email:</label>
                                        <span class="value"><c:out value="${event.linkedVolunteer.email}"/></span>
                                    </div>
                                    <div id="volunteerPhoneField">
                                        <label>Volunteer Phone:</label>
                                        <span class="value"><c:out value="${event.linkedVolunteer.phoneNumber}"/></span>
                                    </div>
                                    <div id="bankNameField">
                                        <label>Bank Name:</label>
                                        <span class="value"><c:out value="${event.linkedVolunteer.linkedBank.bankName}"/></span>
                                    </div>
                                </c:otherwise>
                            </c:choose>



                        </fieldset>
                    </div>
                    <div class="col span_1_of_2">
                        <fieldset>
                            <legend>Teacher</legend>
                            <div id="teacherNameField">
                                <label>Teacher Name:</label>
                                <span class="value">
                                    <c:out value="${event.linkedTeacher.firstName}"/>
                                    <c:out value="${event.linkedTeacher.lastName}"/>
                                </span>
                            </div>
                            <div id="teacherEmailField">
                                <label>Teacher Email:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.email}"/></span>
                            </div>
                            <div id="teacherPhoneField">
                                <label>Teacher Phone:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.phoneNumber}"/></span>
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>School</legend>
                            <div id="schoolNameField">
                                <label>School Name:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></span>
                            </div>
                            <div id="schoolAddressField">
                                <label>School Address:</label>
                                <div style="margin-left: 2em">
                                    <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.addressLine1}"/></div>
                                    <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.city}"/></div>
                                    <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.state}"/></div>
                                    <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.zip}"/></div>
                                </div>
                            </div>
                            <div id="schoolCountyField">
                                <label>School County:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.linkedSchool.county}"/></span>
                            </div>
                            <div id="schoolDistrictField">
                                <label>School District:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.linkedSchool.schoolDistrict}"/></span>
                            </div>
                            <div id="schoolPhoneField">
                                <label>School Phone:</label>
                                <span class="value"><c:out value="${event.linkedTeacher.linkedSchool.phone}"/></span>
                            </div>
                        </fieldset>
                    </div>
                </div>

                <div id="schoolMap">
                    <iframe
                            width="600"
                            height="450"
                            frameborder="0"
                            style="border:0"
                            src="https://www.google.com/maps/embed/v1/place?key=<c:out value="${googleMapsKey}"/>&q=<c:out value="${event.linkedTeacher.linkedSchool.addressInGoogleMapsForm}"/>">
                    </iframe>
                </div>

                <div>
                    <button class="editOrRegister" onclick="js.loadURL('<c:out value="${doneURL}"/>');">Done</button>
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
