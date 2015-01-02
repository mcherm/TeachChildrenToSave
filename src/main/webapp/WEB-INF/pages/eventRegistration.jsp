<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Create New Event</title>
        <%@include file="include/commonHead.jsp"%>
        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
        <script type="text/javascript">
            availableEvents = [
                <c:forEach items="${events}" var="event" varStatus="eventStatus">
                    {
                        'eventId': <c:out value="${event.eventId}"/>,
                        'eventDate': '<c:out value="${event.eventDate}"/>',
                        'eventTime': '<c:out value="${event.eventTime}"/>',
                        'grade': '<c:out value="${event.grade}"/>',
                        'numberStudents': '<c:out value="${event.numberStudents}"/>',
                        'notes': '<c:out value="${event.notes}"/>',
                        'email': '<c:out value="${event.linkedTeacher.email}"/>',
                        'firstName': '<c:out value="${event.linkedTeacher.firstName}"/>',
                        'lastName': '<c:out value="${event.linkedTeacher.lastName}"/>',
                        'phoneNumber': '<c:out value="${event.linkedTeacher.phoneNumber}"/>',
                        'schoolName': '<c:out value="${event.linkedTeacher.linkedSchool.name}"/>',
                        'addressLine1': '<c:out value="${event.linkedTeacher.linkedSchool.addressLine1}"/>',
                        'addressLine2': '<c:out value="${event.linkedTeacher.linkedSchool.addressLine2}"/>',
                        'city': '<c:out value="${event.linkedTeacher.linkedSchool.city}"/>',
                        'state': '<c:out value="${event.linkedTeacher.linkedSchool.state}"/>',
                        'zip': '<c:out value="${event.linkedTeacher.linkedSchool.zip}"/>',
                        'county': '<c:out value="${event.linkedTeacher.linkedSchool.county}"/>',
                        'schoolDistrict': '<c:out value="${event.linkedTeacher.linkedSchool.schoolDistrict}"/>',
                        'lmiEligible': '<c:out value="${event.linkedTeacher.linkedSchool.lmiEligible}"/>'
                    }<c:if test="${not eventStatus.last}">,</c:if>
                </c:forEach>
            ];

            /*
             * This returns a map giving the count of the times each value of this field has
             * occurred, and an array with all the values that it takes on. For your convenience,
             * the array is sorted.
             */
            function countsAndDistinctValuesForField(events, field) {
                var counts = {};
                var values = [];
                $.each(events, function(i, event) {
                    var value = event[field];
                    if (typeof(counts[value]) == "undefined") {
                        counts[value] = 1;
                        values.push(value);
                    } else {
                        counts[value] += 1;
                    }
                });
                values.sort();
                return {'counts': counts, 'values': values};
            }

            $(document).ready(function() {
                var createSelectionCheckboxes = function(args) {
                    var countsAndValues = countsAndDistinctValuesForField(availableEvents, args.field);
                    var counts = countsAndValues.counts;
                    var values = countsAndValues.values;
                    $('#' + args.field +'_checkboxes').html(
                        "<fieldset>" +
                        "    <legend>" + args.legend + "</legend>" +
                        "    <ul>" +
                                $.map( values, function(value) {
                                    return "" +
                                        "<li>" +
                                        "    <label>" +
                                        "        <input type='checkbox' />" +
                                        "        <span class='txt'>" + args.itemLabel(value) + " (" + counts[value] + ")" +
                                        "    </label>" +
                                        "</li>";
                                }).join('') +
                        "    </ul>" +
                        "</fieldset>"
                    );
                }
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
                    itemLabel: function(s) {return "Grade " + s;}
                });
                createSelectionCheckboxes({
                    field: 'lmiEligible',
                    legend: 'CRA Eligible',
                    itemLabel: function(s) {return {'true': 'Yes', 'false': 'No'}[s];}
                });
            });
        </script>
    </head>
    <body class="evenRegistration">
    <%@include file="include/header.jsp"%>

    <a href="#main" class="ada-read">Skip to main content</a>


    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

                <h1>Classes</h1>

                <fieldset class="refine">
                    <legend>
                        Refine by:
                    </legend>

                    <div id="eventDate_checkboxes"><!--populated by javascript --></div>
                    <div id="eventTime_checkboxes"><!--populated by javascript --></div>
                    <div id="county_checkboxes"><!--populated by javascript --></div>
                    <div id="grade_checkboxes"><!--populated by javascript --></div>
                    <div id="lmiEligible_checkboxes"><!--populated by javascript --></div>

                </fieldset>

                <table id="eventTable">
                    <thead>
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Time</th>
                        <th scope="col" class="center">Grade</th>
                        <th scope="col" class="center">Students</th>
                        <th scope="col">Teacher</th>
                        <th scope="col">School</th>
                        <th>
                            <span class="ada-read">Column of Sign Up buttons</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${events}" var="event">
                        <tr id="rowForEvent<c:out value="${event.eventId}"/>">
                            <td><c:out value="${event.eventDate}"/></td>
                            <td><c:out value="${event.eventTime}"/></td>
                            <td class="center"><c:out value="${event.grade}"/></td>
                            <td class="center"><c:out value="${event.numberStudents}"/></td>
                            <td>
                                <c:out value="${event.linkedTeacher.firstName}"/>
                                <c:out value="${event.linkedTeacher.lastName}"/>
                            </td>
                            <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                            <td>
                                <div class="createEventForm">
                                    <form:form method="POST" action="eventRegistration.htm" modelAttribute="formData">
                                        <input type="hidden" name="eventId" value="${event.eventId}">
                                        <%--<input type="submit" value="Sign Up"/>--%>
                                        <button type="submit" value="Sign Up" class="editOrRegister">
                                            Sign Up
                                        </button>
                                    </form:form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <button onclick="js.loadURL('volunteerHome.htm');" class="editOrRegister doneAdding">Done adding classes</button>

        </main>

    </div><%-- .mainCnt --%>

    <%@include file="include/footer.jsp" %>

    </body>
</html>
