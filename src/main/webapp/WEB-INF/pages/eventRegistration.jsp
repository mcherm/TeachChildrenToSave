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
                        'eventDate': '<c:out value="${event.eventDate.pretty}"/>',
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

            /*
             * This is accessed as filterSettings[field][value] and it returns true if that
             * box is checked and false if it isn't. Only the checked values should be shown
             * UNLESS none of the values for a field are checked, in which case that field
             * should not restrict the display of events.
             */
            var filterSettings = {};

            /*
             * This creates the table of events (dynamically constructing the HTML).
             */
            function buildTable() {
                var html =
                    "<thead><tr>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"eventDate\")' id='col_for_eventDate'>Date</th>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"eventTime\")' id='col_for_eventTime'>Time</th>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"grade\")' id='col_for_grade'>Grade</th>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"numberStudents\")' id='col_for_numberStudents'>Students</th>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"firstName\")' id='col_for_firstName'>Teacher</th>" +
                    "    <th scope='col' class='sortable' onclick='sortBy(\"schoolName\")' id='col_for_schoolName''>School</th>" +
                    "    <th scope='col'><span class='ada-read'>Column of Details buttons</span></th>" +
                    "    <th scope='col'><span class='ada-read'>Column of Sign Up buttons</span></th>" +
                    "</tr></thead>" +
                    "<tbody>";
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
                            "    <td>" + event.eventDate + "</td>" +
                            "    <td>" + event.eventTime + "</td>" +
                            "    <td class='center'>" + event.grade + "</td>" +
                            "    <td class='center'>" + event.numberStudents + "</td>" +
                            "    <td>" + event.firstName + " " + event.lastName + "</td>" +
                            "    <td>" + event.schoolName + "</td>" +
                            "    <td><form action='eventDetails.htm' method='POST'>" +
                            "        <input type='hidden' name='eventId' value='" + event.eventId +"'/>" +
                            "        <input type='hidden' name='doneURL' value='eventRegistration.htm'/>" +
                            "        <button class='editOrRegister details' type='submit'>Details</button>" +
                            "    </form></td>" +
                            "    <td>" +
                            "        <div class='createEventForm'>" +
                            "            <form method='POST' action='eventRegistration.htm'>" +
                            "                <input type='hidden' name='eventId' value='" + event.eventId + "'>" +
                            "                <button type='submit' value='Sign Up' class='editOrRegister'>Sign Up</button>" +
                            "            </form>" +
                            "        </div>" +
                            "    </td>" +
                            "</tr>";
                    }
                });
                html +=
                    "</tbody>";

                $('#dynamicEventTable').html(html);
            }

            /*
             * This is the function that gets called when a checkbox is clicked.
             */
            function toggleFilter(field, value) {
                filterSettings[field][value] = ! filterSettings[field][value];
                buildTable();
            }

            /*
             * lookupTable is an object used as a map. if key is a key in lookupTable, this
             * returns the corresponding value from lookupTable. If it is NOT a key in lookupTable,
             * this returns -1. The "safely" in the name refers to the fact that we are
             * avoiding returning undefined which would cause problems in the sort function.
             */
            function getValueSafely(lookupTable, key) {
                var result = lookupTable[key];
                if (result === undefined) {
                    return -1;
                } else {
                    return result;
                }
            }

            /*
             * Returns true if value1 is less than value2, when both are values
             * of the given field. Passes field in order to allow us to have
             * different sorting logic for different types of fields. The default
             * is basic string ordering.
             */
            function isLessThan(field, value1, value2) {
                if ($.inArray(field, ['grade', 'numberStudents']) > -1) {
                    // -- numeric sort --
                    return parseInt(value1) < parseInt(value2);
                } else if (field == 'eventDate') {
                    // -- date sort --
                    var dateOrder = {
                        <c:forEach items="${allowedDates}" var="date" varStatus="status">
                        "<c:out value="${date.pretty}"/>": <c:out value="${status.index}"/>
                        <c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    };
                    return getValueSafely(dateOrder, value1) < getValueSafely(dateOrder, value2);
                } else if (field == 'eventTime') {
                    // -- time sort --
                    var timeOrder = {
                        <c:forEach items="${allowedTimes}" var="time" varStatus="status">
                            "<c:out value="${time}"/>": <c:out value="${status.index}"/>
                            <c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    };
                    return getValueSafely(timeOrder, value1) < getValueSafely(timeOrder, value2);
                } else {
                    // -- string sort --
                    return value1 < value2;
                }
            }

            /*
             * This is the function that gets called when a sort box is clicked.
             */
            function  sortBy(field) {
                var descending = $("#col_for_" + field).hasClass("ascending");
                availableEvents.sort(function(event1, event2) {
                    if (descending) {
                        var temp = event1;
                        event1 = event2;
                        event2 = temp;
                    }
                    if (event1[field] == event2[field]) {
                        return 0;
                    } else if (isLessThan(field, event1[field], event2[field])) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                buildTable();
                $("#col_for_" + field).addClass(descending ? "descending" : "ascending");
            }

            $(document).ready(function() {
                var createSelectionCheckboxes = function(args) {
                    filterSettings[args.field] = {};
                    var countsAndValues = countsAndDistinctValuesForField(availableEvents, args.field);
                    var counts = countsAndValues.counts;
                    var values = countsAndValues.values;
                    var html =
                        "<fieldset>" +
                        "    <legend>" + args.legend + "</legend>" +
                        "    <ul>";
                    $.each(values, function(i,value) {
                        filterSettings[args.field][value] = false;
                        html +=
                            "<li>" +
                            "    <label>" +
                            "        <input type='checkbox' onclick='toggleFilter(\"" + args.field + "\",\"" + value + "\");' />" +
                            "        <span class='txt'>" + args.itemLabel(value) + " (" + counts[value] + ")" + "</span>" +
                            "    </label>" +
                            "</li>";
                    });
                    html +=
                        "    </ul>" +
                        "</fieldset>"
                    $('#' + args.field +'_checkboxes').html(html);
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
                buildTable();
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

                <table id="dynamicEventTable"><!-- populated by javascript --></table>

                <button onclick="js.loadURL('volunteerHome.htm');" class="editOrRegister doneAdding">Cancel</button>

        </main>

    </div><%-- .mainCnt --%>

    <%@include file="include/footer.jsp" %>

    </body>
</html>
