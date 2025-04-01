<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://teachchildrentosaveday.org/tctstaglib" prefix="fn" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Sign Up Volunteer for an Event</title>
        <%@include file="include/commonHead.jsp"%>

        <style>td {display:block;}</style>

        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
        <script type="text/javascript">
            availableEvents = [
                <c:forEach items="${events}" var="event" varStatus="eventStatus">
                    {
                        'eventId': '<c:out value="${event.eventId}"/>',
                        'eventDate': '<c:out value="${event.eventDate.pretty}"/>',
                        'eventTime': '<c:out value="${event.eventTime}"/>',
                        'grade': '<c:out value="${event.grade}"/>',
                        'deliveryMethod': '<c:out value="${event.deliveryMethod}"/>',
                        'numberStudents': '<c:out value="${event.numberStudents}"/>',
                        'notes': '<c:out value="${fn:forJavascriptString(event.notes)}"/>',
                        'email': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.email)}"/>',
                        'firstName': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.firstName)}"/>',
                        'lastName': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.lastName)}"/>',
                        'phoneNumber': '<c:out value="${event.linkedTeacher.phoneNumber}"/>',
                        'schoolName': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.name)}"/>',
                        'addressLine1': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.addressLine1)}"/>',
                        'city': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.city)}"/>',
                        'state': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.state)}"/>',
                        'zip': '<c:out value="${event.linkedTeacher.linkedSchool.zip}"/>',
                        'county': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.county)}"/>',
                        'schoolDistrict': '<c:out value="${fn:forJavascriptString(event.linkedTeacher.linkedSchool.schoolDistrict)}"/>',
                        'lmiEligible': '<c:out value="${event.linkedTeacher.linkedSchool.lmiEligible}"/>'
                        <c:if test="${bank.minLMIForCRA != null}">
                            ,'craEligible': '<c:choose><c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when><c:otherwise>Not eligible</c:otherwise></c:choose>'
                        </c:if>
                    }<c:if test="${not eventStatus.last}">,</c:if>
                </c:forEach>
            ];

            // this includes the common functions for sorting and filtering the event table
            <%@include file="include/sortableTable.jsp"%>

            /*
             * This creates the table of events (dynamically constructing the HTML).
             */
            function buildTable() {
                var html =
                    "<thead><tr>" +
                    "    <th data-title='Sort list by:' scope='col' class='sortable date' id='col_for_eventDate'><button onclick='sortBy(\"eventDate\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Date</button></th>" +
                    "    <th scope='col' class='sortable time' id='col_for_eventTime'><button onclick='sortBy(\"eventTime\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Time</button></th>" +
                    "    <th scope='col' class='sortable grade' id='col_for_grade'><button onclick='sortBy(\"grade\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Grade</button></th>" +
                    "    <th scope='col' class='sortable deliveryMethod' id='col_for_deliveryMethod'><button onclick='sortBy(\"deliveryMethod\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Delivery Method</button></th>" +
                    "    <th scope='col' class='sortable students' id='col_for_numberStudents'><button onclick='sortBy(\"numberStudents\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Students</button></th>" +
                    "    <th scope='col' class='sortable teacher' id='col_for_firstName'><button onclick='sortBy(\"firstName\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>Teacher</button></th>" +
                    "    <th scope='col' class='sortable school' id='col_for_schoolName''><button onclick='sortBy(\"schoolName\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>School</button></th>" +
                    <c:if test="${bank.minLMIForCRA != null}">
                    "    <th scope='col' class='sortable' id='col_for_craEligible''><button onclick='sortBy(\"craEligible\", availableEvents)'><span class='ada-read'>Sort by&nbsp;</span>CRA</button></th>" +
                    </c:if>
                    "    <th scope='col' class='action'><span class='ada-read'>Column of Details buttons</span></th>" +
                    <c:if test="${volunteerSignupsOpen}">
                    "    <th scope='col' class='action'><span class='ada-read'>Column of Sign Up buttons</span></th>" +
                    </c:if>
                    "</tr></thead>" +
                    "<tbody>";
                <c:if test="${empty events}">
                    html += "<td colspan=\"8\" class=\"emptyTableMessage\">There are currently no open classes.</td>";
                </c:if>


                $.each(availableEvents, function(i,event) {
                    var showThisEvent = true;
                    $.each(filterSettings, function(field,settings) {
                        var allAreUnchecked = true;
                        $.each(settings, function(value,isChecked) {
                            if (isChecked) {
                                allAreUnchecked = false;
                            }
                        });
                        if (!allAreUnchecked) {
                            var actualValue = event[field];
                            if (! settings[actualValue]) {
                                showThisEvent = false;
                            }
                        }
                    });
                    if (showThisEvent) {
                        html +=
                            "<tr id='rowForEvent" + event.eventId + "'>" +
                            "    <td class='date' data-title='Date'>" + event.eventDate + "</td>" +
                            "    <td class='time' data-title='Time'>" + event.eventTime + "</td>" +
                            "    <td class='grade' class='center' data-title='Grade'>" + event.grade + "</td>" +
                            "    <td class='deliveryMethod' class='center' data-title='Delivery Method'>" + event.deliveryMethod + "</td>" +
                            "    <td class='students' class='center' data-title='Number of students'>" + event.numberStudents + "</td>" +
                            "    <td class='teacher' data-title='Teacher'>" + event.firstName + " " + event.lastName + "</td>" +
                            "    <td class='school' data-title='School'>" + event.schoolName + "</td>" +
                            <c:if test="${bank.minLMIForCRA != null}">
                            "    <td data-title='CRA'>" + event.craEligible + "</td>" +
                            </c:if>
                            "    <td class='action'><form action='eventDetails.htm' method='POST'>" +
                            "        <input type='hidden' name='eventId' value='" + event.eventId +"'/>" +

                            "        <input type='hidden' name='doneURL' value='${calledByURL}'/>" +
                            "        <button class='editOrRegister details' type='submit'>Details</button>" +
                            "    </form></td>" +
                            <c:if test="${volunteerSignupsOpen}">
                            "    <td class='action'>" +
                            "            <form method='POST' action='eventRegistration.htm'>" +
                            "                <input type='hidden' name='eventId' value='" + event.eventId + "'>" +
                            "                <input type='hidden' name='volunteerId' value = '${volunteerId}'>" +
                            "                <button type='submit' class='editOrRegister details'>Sign Up</button>" +
                            "            </form>" +
                            "    </td>" +
                            </c:if>
                            "</tr>" +
                            "<tr>" +
                            (
                                event.notes === null || event.notes === ""
                                ? ""
                                : "<td colspan='11' class = 'italic'>Class Note: "+ event.notes + "</td>"
                            ) +
                            "</tr>";
                    }
                });
                html +=
                    "</tbody>";

                $('#dynamicEventTable').html(html);
            }

            $(document).ready(function() {
                createSelectionCheckboxes({
                    field: 'eventDate',
                    legend: 'Date',
                    itemLabel: function(s) {return s;}
                });
                createSelectionCheckboxes({
                    field: 'eventTime',
                    legend: 'Time',
                    itemLabel: function(s) {return s;}
                });
                createSelectionCheckboxes({
                    field: 'county',
                    legend: 'County',
                    itemLabel: function(s) {return s;}
                });
                createSelectionCheckboxes({
                    field: 'grade',
                    legend: 'Grade',
                    itemLabel: function(s) {return s;}
                });
                createSelectionCheckboxes({
                    field: 'deliveryMethod',
                    legend: 'Delivery Method',
                    itemLabel: function(s) {return s;}
                });
                <%-- FIXME: Here I should have another checkbox for selecting by CRA Eligible --%>
                <%--
                createSelectionCheckboxes({
                    field: 'lmiEligible',
                    legend: 'CRA Eligible',
                    itemLabel: function(s) {return {'true': 'Yes', 'false': 'No'}[s];}
                });
                --%>
                buildTable();

            });
        </script>
    </head>
    <body class="evenRegistration">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp"%>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <c:if test = "${calledBy == 'siteAdmin' }">
                <h1> Sign Up  ${volunteerFirstName} ${volunteerLastName} for a Class</h1>
            </c:if>

            <c:if test = "${calledBy == 'bankAdmin' }">
                <h1>My Volunteering</h1>
                <c:if test="${not volunteerSignupsOpen}">
                    <div class="importantMessage">
                        Signups for classes will be available soon.
                    </div>
                </c:if>

            </c:if>

            <c:if test = "${calledBy == 'siteAdmin' || calledBy == 'bankAdmin' }">
                <%-- If this page is called by the siteAdmin include a list of classes the volunteer is already
                     signed up for--%>


                <div id="currentlyRegisteredEvents">
                    <c:if test = "${calledBy == 'bankAdmin' }">
                        <h2>Classes I am currently signed up for: </h2>
                    </c:if>
                    <c:if test = "${calledBy == 'siteAdmin' }">
                        <h2>Classes that ${volunteerFirstName} ${volunteerLastName} is currently signed up for: </h2>
                    </c:if>



                    <table id="eventTable" class="displayTable responsive">

                        <thead>
                        <tr>
                            <th scope="col">Date</th>
                            <th scope="col">Time</th>
                            <th scope="col">School</th>
                            <th scope="col">Teacher</th>
                            <th scope="col" class="center">Grade</th>
                            <th scope="col">Delivery Method</th>
                            <th scope="col" class="center">Students</th>
                            <c:if test="${bank.minLMIForCRA != null}">
                                <th scope="col">CRA</th>
                            </c:if>

                            <th scope="col"><span class="ada-read">Column of Details buttons</span></th>
                            <th scope="col"><span class="ada-read">Column of Delete buttons</span></th>

                        <%--
                            <th scope="col"><span class="ada-read">Column of Withdraw buttons</span></th>
                            --%>

                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty volunteerEvents}">
                            <td colspan="8" class="emptyTableMessage">${volunteerFirstName} ${volunteerLastName} has not signed up for any classes yet.</td>
                        </c:if>
                        <c:forEach var="event" items="${volunteerEvents}">
                            <tr>

                                <td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>
                                <td data-title="Time"><c:out value="${event.eventTime}"/></td>
                                <td data-title="School"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                                <td data-title="Teacher"><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>
                                <td data-title="Grade" class="center"><c:out value="${event.grade}"/></td>
                                <td data-title="Delivery Method"><c:out value="${event.deliveryMethod}"/></td>
                                <td data-title="Students" class="center"><c:out value="${event.numberStudents}"/></td>
                                <c:if test="${bank.minLMIForCRA != null}">
                                    <td data-title="CRA">
                                        <c:choose>
                                            <c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when>
                                            <c:otherwise>Not eligible</c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:if>
                                <td class="action">
                                    <form action="eventDetails.htm" method="POST">
                                        <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                        <input type="hidden" name="doneURL" value="${calledByURL}"/>
                                        <button class="editOrRegister details" type="submit">Details</button>
                                    </form>
                                </td>

                                <td class="action">
                                    <button onclick="js.loadURL('volunteerWithdraw.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister delete">
                                        Withdraw
                                    </button>
                                </td>

                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <br/>  <br/>
                </div><%-- #events --%>


                <h2>Available Classes</h2>
                <div> When you sign-up a volunteer an automatic email will be generated to the volunteer and the teacher of the class</div>
            </c:if>
            <c:if test = "${calledBy == 'volunteer'}">
                <h1>Classes</h1>
            </c:if>


            <fieldset class="refine">
                    <legend>
                        Refine by:
                    </legend>

                    <div class="checkboxLists">
                        <div id="eventDate_checkboxes"><%-- populated by javascript --%></div>
                        <div id="eventTime_checkboxes"><%-- populated by javascript --%></div>
                        <div id="county_checkboxes"><%-- populated by javascript --%></div>
                        <div id="grade_checkboxes"><%-- populated by javascript --%></div>
                        <div id="deliveryMethod_checkboxes"><%-- populated by javascript --%></div>
                        <div id="lmiEligible_checkboxes"><%-- populated by javascript --%></div>
                    </div>

                    <div class="selectLists">
                        <div id="eventDate_select"><%-- populated by javascript --%></div>
                        <div id="eventTime_select"><%-- populated by javascript --%></div>
                        <div id="county_select"><%-- populated by javascript --%></div>
                        <div id="grade_select"><%-- populated by javascript --%></div>
                        <div id="deliveryMethod_select"><%-- populated by javascript --%></div>
                        <div id="lmiEligible_select"><%-- populated by javascript --%></div>
                    </div>

                </fieldset>

                <table id="dynamicEventTable" class="responsive"><%-- populated by javascript --%></table>

                <br>

                <div class="doneBlock">
                    <button onclick="js.loadURL('${calledBy}Home.htm');" class="editOrRegister delete">Done</button>
                </div>
        </main>

    </div><%-- .mainCnt --%>

    <%@include file="include/footer.jsp" %>

    </body>
</html>
