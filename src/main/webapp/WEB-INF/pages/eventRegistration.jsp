<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                        'city': '<c:out value="${event.linkedTeacher.linkedSchool.city}"/>',
                        'state': '<c:out value="${event.linkedTeacher.linkedSchool.state}"/>',
                        'zip': '<c:out value="${event.linkedTeacher.linkedSchool.zip}"/>',
                        'county': '<c:out value="${event.linkedTeacher.linkedSchool.county}"/>',
                        'schoolDistrict': '<c:out value="${event.linkedTeacher.linkedSchool.schoolDistrict}"/>',
                        'lmiEligible': '<c:out value="${event.linkedTeacher.linkedSchool.lmiEligible}"/>'
                        <c:if test="${bank.minLMIForCRA != null}">
                            ,'craEligible': '<c:choose><c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when><c:otherwise>Not eligible</c:otherwise></c:choose>'
                        </c:if>
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

                values.sort(function(a,b) {
                    if (isLessThan(field, a, b)) {
                        return -1;
                    } else {
                        if (isLessThan(field, b, a)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
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
                    "    <th data-title='Sort list by:' scope='col' class='sortable date' id='col_for_eventDate'><button onclick='sortBy(\"eventDate\")'><span class='ada-read'>Sort by&nbsp;</span>Date</button></th>" +
                    "    <th scope='col' class='sortable time' id='col_for_eventTime'><button onclick='sortBy(\"eventTime\")'><span class='ada-read'>Sort by&nbsp;</span>Time</button></th>" +
                    "    <th scope='col' class='sortable grade' id='col_for_grade'><button onclick='sortBy(\"grade\")'><span class='ada-read'>Sort by&nbsp;</span>Grade</button></th>" +
                    "    <th scope='col' class='sortable students' id='col_for_numberStudents'><button onclick='sortBy(\"numberStudents\")'><span class='ada-read'>Sort by&nbsp;</span>Students</button></th>" +
                    "    <th scope='col' class='sortable teacher' id='col_for_firstName'><button onclick='sortBy(\"firstName\")'><span class='ada-read'>Sort by&nbsp;</span>Teacher</button></th>" +
                    "    <th scope='col' class='sortable school' id='col_for_schoolName''><button onclick='sortBy(\"schoolName\")'><span class='ada-read'>Sort by&nbsp;</span>School</button></th>" +
                    <c:if test="${bank.minLMIForCRA != null}">
                    "    <th scope='col' class='sortable' id='col_for_craEligible''><button onclick='sortBy(\"craEligible\")'><span class='ada-read'>Sort by&nbsp;</span>CRA</button></th>" +
                    </c:if>
                    "    <th scope='col' class='action'><span class='ada-read'>Column of Details buttons</span></th>" +
                    "    <th scope='col' class='action'><span class='ada-read'>Column of Sign Up buttons</span></th>" +
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
                            "    <td class='date' data-title='Date'>" + event.eventDate + "</td>" +
                            "    <td class='time' data-title='Time'>" + event.eventTime + "</td>" +
                            "    <td class='grade' class='center' data-title='Grade'>" + event.grade + "</td>" +
                            "    <td class='students' class='center' data-title='Number of students'>" + event.numberStudents + "</td>" +
                            "    <td class='teacher' data-title='Teacher'>" + event.firstName + " " + event.lastName + "</td>" +
                            "    <td class='school' data-title='School'>" + event.schoolName + "</td>" +
                            <c:if test="${bank.minLMIForCRA != null}">
                            "    <td data-title='CRA'>" + event.craEligible + "</td>" +
                            </c:if>
                            "    <td class='action'><form action='eventDetails.htm' method='POST'>" +
                            "        <input type='hidden' name='eventId' value='" + event.eventId +"'/>" +
                            "        <input type='hidden' name='doneURL' value='eventRegistration.htm'/>" +
                            "        <button class='editOrRegister details' type='submit'>Details</button>" +
                            "    </form></td>" +
                            "    <td class='action'>" +
                            "            <form method='POST' action='eventRegistration.htm'>" +
                            "                <input type='hidden' name='eventId' value='" + event.eventId + "'>" +
                            "                <input type='hidden' name='volunteerId' value = '${volunteerId}'>" +
                            "                <button type='submit' class='editOrRegister details'>Sign Up</button>" +
                            "            </form>" +
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

                console.log('the filter field/value are: ' + field + ' / ' + value)

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

            function filterByOptionList(selected){

                var theItem = selected.options[selected.selectedIndex];

                var selectedOption = theItem.value;

                var theCategory = $( 'option:selected', selected).data("name");


                if (theCategory != '' && selectedOption != ''){

                    console.log('selectionOption: ' + selectedOption);
                    console.log('selectedCategory: ' + theCategory);

                    toggleFilter(theCategory, selectedOption);

                } else {

                    // TODO: reset category filter when a dropdown has been cleared, as well as filter based on all select/option

                }
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
                        "       <ul>";

                    /* the selectList is for mobile display, so we don't have to render a lot of checkboxes  */
                    var selectListHtml = "<label><span>" + args.legend + "</span><select onchange='filterByOptionList(this)'><option data-name='' value='' selected>Select a filter</option>";

                    $.each(values, function(i,value) {
                        filterSettings[args.field][value] = false;
                        html +=
                            "<li>" +
                            "    <label>" +
                            "        <input type='checkbox' onclick='toggleFilter(\"" + args.field + "\",\"" + value + "\");' />" +
                            "        <span class='txt'>" + args.itemLabel(value) + " (" + counts[value] + ")" + "</span>" +
                            "    </label>" +
                            "</li>";

                        selectListHtml += "<option data-name='" + args.field + "' value='" + value +  "'>" + args.itemLabel(value) + " (" + counts[value] + ")</option>";

                    });
                    html +=
                        "    </ul>" +
                        "</fieldset>";

                    selectListHtml += "</select></label>";

                    $('#' + args.field +'_checkboxes').html(html);

                    $('#' + args.field +'_select').html(selectListHtml);

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


            <c:if test = "${calledBy == 'siteAdmin'}">
                <%-- If this page is called by the siteAdmin include a list of classes the volunteer is already
                     signed up for--%>
                <h1> Sign Up  ${volunteerFirstName} ${volunteerLastName} for a Class</h1>

                <div id="currentlyRegisteredEvents">

                    <h2>Classes that ${volunteerFirstName} ${volunteerLastName} is currently signed up for: </h2>

                    <table id="eventTable" class="displayTable responsive">

                        <thead>
                        <tr>
                            <th scope="col">Date</th>
                            <th scope="col">Time</th>
                            <th scope="col">School</th>
                            <th scope="col">Teacher</th>
                            <th scope="col" class="center">Grade</th>
                            <th scope="col" class="center">Students</th>
                            <c:if test="${bank.minLMIForCRA != null}">
                                <th scope="col">CRA</th>
                            </c:if>
                            <%--
                            <th scope="col"><span class="ada-read">Column of Details buttons</span></th>
                            <th scope="col"><span class="ada-read">Column of Delete buttons</span></th>
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
                                <td data-title="Students" class="center"><c:out value="${event.numberStudents}"/></td>
                                <c:if test="${bank.minLMIForCRA != null}">
                                    <td data-title="CRA">
                                        <c:choose>
                                            <c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when>
                                            <c:otherwise>Not eligible</c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:if>
                                <%--
                                <td class="action">
                                --%>
                                        <%--=======--%>
                                        <%--<td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>--%>
                                        <%--<td data-title="Time"><c:out value="${event.eventTime}"/></td>--%>
                                        <%--<td data-title="School"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>--%>
                                        <%--<td data-title="Teacher"><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>--%>
                                        <%--<td class="center" data-title="Grade"><c:out value="${event.grade}"/></td>--%>
                                        <%--<td class="center" data-title="Students"><c:out value="${event.numberStudents}"/></td>--%>
                                        <%--<td class="action">--%>
                                        <%-->>>>>>> Stashed changes--%>
                                <%--
                                    <form action="eventDetails.htm" method="POST">
                                        <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                        <input type="hidden" name="doneURL" value="volunteerHome.htm"/>
                                        <button class="editOrRegister details" type="submit">Details</button>
                                    </form>
                                </td>
                                <td class="action">
                                    <button onclick="js.loadURL('volunteerWithdraw.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister delete">
                                        Delete
                                    </button>
                                </td>
                                --%>
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
                        <div id="lmiEligible_checkboxes"><%-- populated by javascript --%></div>
                    </div>

                    <div class="selectLists">
                        <div id="eventDate_select"><%-- populated by javascript --%></div>
                        <div id="eventTime_select"><%-- populated by javascript --%></div>
                        <div id="county_select"><%-- populated by javascript --%></div>
                        <div id="grade_select"><%-- populated by javascript --%></div>
                        <div id="lmiEligible_select"><%-- populated by javascript --%></div>
                    </div>

                </fieldset>

                <table id="dynamicEventTable" class="responsive"><%-- populated by javascript --%></table>

                <br>

                <div class="cancelBlock">
                    <button onclick="js.loadURL('${calledBy}Home.htm');" class="editOrRegister delete">Cancel</button>
                </div>
        </main>

    </div><%-- .mainCnt --%>

    <%@include file="include/footer.jsp" %>

    </body>
</html>
