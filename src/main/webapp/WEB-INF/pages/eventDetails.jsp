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

                <div id="eventFields">
                    <div id="eventDateField">
                        <div class="label">Date</div>
                        <div class="value"><c:out value="${event.eventDate.pretty}"/></div>
                    </div>
                    <div id="eventTimeField">
                        <div class="label">Time</div>
                        <div class="value"><c:out value="${event.eventTime}"/></div>
                    </div>
                    <div id="gradeField">
                        <div class="label">Grade</div>
                        <div class="value"><c:out value="${event.grade}"/></div>
                    </div>
                    <div id="numberStudentsField">
                        <div class="label">Students</div>
                        <div class="value"><c:out value="${event.numberStudents}"/></div>
                    </div>
                    <div id="notesField">
                        <div class="label">Notes</div>
                        <div class="value"><c:out value="${event.notes}"/></div>
                    </div>
                </div>

                <div id="teacherFields">
                    <div id="teacherNameField">
                        <div class="label">Teacher Name</div>
                        <div class="value">
                            <c:out value="${event.linkedTeacher.firstName}"/>
                            <c:out value="${event.linkedTeacher.lastName}"/>
                        </div>
                    </div>
                    <div id="teacherEmailField">
                        <div class="label">Teacher Email</div>
                        <div class="value"><c:out value="${event.linkedTeacher.email}"/></div>
                    </div>
                    <div id="teacherPhoneField">
                        <div class="label">Teacher Phone</div>
                        <div class="value"><c:out value="${event.linkedTeacher.phoneNumber}"/></div>
                    </div>
                </div>

                <div id="schoolFields">
                    <div id="schoolNameField">
                        <div class="label">School Name</div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></div>
                    </div>
                    <div id="schoolAddressField">
                        <div class="label">School Address</div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.addressLine1}"/></div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.city}"/></div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.state}"/></div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.zip}"/></div>
                    </div>
                    <div id="schoolCountyField">
                        <div class="label">School County</div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.county}"/></div>
                    </div>
                    <div id="schoolDistrictField">
                        <div class="label">School District</div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.schoolDistrict}"/></div>
                    </div>
                    <div id="schoolPhoneField">
                        <div class="label">School Phone</div>
                        <div class="value"><c:out value="${event.linkedTeacher.linkedSchool.phone}"/></div>
                    </div>
                    <div id="schoolLMIEligibleField">
                        <div class="label">School Eligible for CRA Credits?</div>
                        <div class="value">
                            <div class="qa-notes">Not implemented yet</div>
                        </div>
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

                <c:choose>
                    <c:when test="${event.linkedVolunteer == null}">

                        <div id="noVolunteerYet">
                            <div>No volunteer yet.</div>
                        </div>

                    </c:when>
                    <c:otherwise>

                        <div id="volunteerFields">
                            <div id="volunteerNameField">
                                <div class="label">Volunteer Name</div>
                                <div class="value">
                                    <c:out value="${event.linkedVolunteer.firstName}"/>
                                    <c:out value="${event.linkedVolunteer.lastName}"/>
                                </div>
                            </div>
                            <div id="volunteerEmailField">
                                <div class="label">Volunteer Email</div>
                                <div class="value"><c:out value="${event.linkedVolunteer.email}"/></div>
                            </div>
                            <div id="volunteerPhoneField">
                                <div class="label">Volunteer Phone</div>
                                <div class="value"><c:out value="${event.linkedVolunteer.phoneNumber}"/></div>
                            </div>
                            <div id="bankNameField">
                                <div class="label">Bank Name</div>
                                <div class="value"><c:out value="${event.linkedVolunteer.linkedBank.bankName}"/></div>
                            </div>
                        </div>

                    </c:otherwise>
                </c:choose>

                <div>
                    <%-- FIXME: Need to code this --%>
                    <button class="editOrRegister" onclick="js.loadURL('<c:out value="${doneURL}"/>');">Done</button>
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
