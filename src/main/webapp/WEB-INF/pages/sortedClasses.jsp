<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://teachchildrentosaveday.org/tctstaglib" prefix="fn" %>
<!DOCTYPE html>
<html lang="en-US">
<head>
    <title>Teach Children To Save - Sorted Class List for Admin</title>
    <%@include file="include/commonHead.jsp"%>

    <style>td {display:block;}</style>

    <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
    <script type="text/javascript">
        <%@include file="include/sortableTable.jsp"%>
        availableEvents = [
            <c:forEach items="${events}" var="event" varStatus="eventStatus">
            {
                'eventId': '<c:out value="${event.eventId}"/>',
                'eventDate': '<c:out value="${event.eventDate.pretty}"/>',
                'eventTime': '<c:out value="${event.eventTime}"/>',
                'grade': '<c:out value="${event.grade}"/>',
                'deliveryMethodString': '<c:out value="${event.deliveryMethodString}"/>',
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
                'lmiEligible': '<c:out value="${event.linkedTeacher.linkedSchool.lmiEligible}"/>',
                'volunteerFirstName':'<c:out value="${event.linkedVolunteer.firstName}"/>',
                'volunteerLastName':'<c:out value="${event.linkedVolunteer.lastName}"/>',
                'volunteerEmail':'<c:out value="${event.linkedVolunteer.email}"/>',
                'available': '<c:choose><c:when test="${empty event.linkedVolunteer.email}">true</c:when><c:otherwise>false</c:otherwise></c:choose>'
            }<c:if test="${not eventStatus.last}">,</c:if>
            </c:forEach>
        ];


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
                "    <th scope='col' class='sortable deliveryMethodString' id='col_for_deliveryMethodString'><button onclick='sortBy(\"deliveryMethodString\")'><span class='ada-read'>Sort by&nbsp;</span>Delivery Method</button></th>" +
                "    <th scope='col' class='sortable students' id='col_for_numberStudents'><button onclick='sortBy(\"numberStudents\")'><span class='ada-read'>Sort by&nbsp;</span>Students</button></th>" +
                "    <th scope='col' class='sortable teacher' id='col_for_firstName'><button onclick='sortBy(\"firstName\")'><span class='ada-read'>Sort by&nbsp;</span>Teacher</button></th>" +
                "    <th scope='col' class='sortable email' id='col_for_email'><button onclick='sortBy(\"email\")'><span class='ada-read'>Sort by&nbsp;</span>Teacher Email</button></th>" +
                "    <th scope='col' class='sortable school' id='col_for_schoolName'><button onclick='sortBy(\"schoolName\")'><span class='ada-read'>Sort by&nbsp;</span>School</button></th>" +
                "    <th scope='col' class='sortable teacher' id='col_for_volunteerFirstName'><button onclick='sortBy(\"volunteerFirstName\")'><span class='ada-read'>Sort by&nbsp;</span>Volunteer</button></th>" +
                "    <th scope='col' class='sortable email' id='col_for_volunteerEmail'><button onclick='sortBy(\"volunteerEmail\")'><span class='ada-read'>Sort by&nbsp;</span>Volunteer Email</button></th>" +
                "    <th scope='col' class='action'><span class='ada-read'>Column of Details buttons</span></th>" +
                "    <th scope='col' class='action'><span class='ada-read'>Column of Delete buttons</span></th>" +
                "    <th scope='col' class='action'><span class='ada-read'>Column of Modify buttons</span></th>" +

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
                        "    <td class='deliveryMethodString' class='center' data-title='Delivery Method'>" + event.deliveryMethodString + "</td>" +
                        "    <td class='students' class='center' data-title='Number of students'>" + event.numberStudents + "</td>" +
                        "    <td class='teacher' data-title='Teacher'>" + event.firstName + " " + event.lastName + "</td>" +
                        "    <td class='teacher' data-title='TeacherEmail'>" + event.email + "</td>" +
                        "    <td class='school' data-title='School'>" + event.schoolName + "</td>" +
                        "    <td class='teacher' data-title='Volunteer'>" + event.volunteerFirstName + " " + event.volunteerLastName + "</td>" +
                        "    <td class='email' data-title='VolunteerEmail'>" + event.volunteerEmail + "</td>" +
                        "    <td class='action'><form action='eventDetails.htm' method='POST'>" +
                        "        <input type='hidden' name='eventId' value='" + event.eventId +"'/>" +
                        "        <input type='hidden' name='doneURL' value='${calledByURL}'/>" +
                        "        <button class='editOrRegister details' type='submit'>Details</button>" +
                        "    </form></td>" +
                        "   <td class='action'>" +
                        "   <form method='POST' action='deleteEvent.htm' modelAttribute='formData'>" +
                                "<input type='hidden' name='eventId' value='"+ event.eventId + "'/>" +
                                "<button type='submit' onclick=\"return confirm('Delete this class?')\" class='editOrRegister delete'>Delete</button>" +
                        "   </form>" +
                        "   </td>" +
                        "   <td class='action'>" +
                        "      <button onclick=\"js.loadURL('editEvent.htm?eventId=" + event.eventId +"');\" class='editOrRegister'>Modify</button>" +
                        "   </td>" +


                    "</tr>" +
                        "<tr>" +
                        (
                            event.notes === null || event.notes === ""
                                ? ""
                                : "<td colspan='10' class = 'italic'>Class Note: "+ event.notes + "</td><td></td><td></td><td></td>"
                        ) +
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
            createSelectionCheckboxes({
                field: 'deliveryMethodString',
                legend: 'Delivery Method',
                itemLabel: function(s) {return s;}
            });
            createSelectionCheckboxes({
                field: 'available',
                legend: 'Volunteering',
                itemLabel: function(s) {
                    if (s.includes('true')){
                        return"Needs Volunteer";
                    } else {
                        return "Has Volunteer";
                }}
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
        <h1>Classes</h1>

        <div id="actions">

            <h2>Actions</h2>

            <ul class="noUl">
                <li class="mb1">
                    <button onclick="js.loadURL('createEventBySiteAdmin.htm')" class="wideButton  editOrRegister cancel">Create New Class</button>
                    <button onclick="js.loadURL('siteAdminHome.htm')" class="wideButton editOrRegister cancel">Back</button>
                </li>

            </ul>
        </div>

        <a download="classes.xls" href="excel/events.htm" class="downloadExcel">Export to Excel</a>

        <fieldset class="refine">
            <legend>
                Refine by:
            </legend>

            <div class="checkboxLists">
                <div id="available_checkboxes"><%-- populated by javascript --%></div>
                <div id="eventDate_checkboxes"><%-- populated by javascript --%></div>
                <div id="eventTime_checkboxes"><%-- populated by javascript --%></div>
                <div id="county_checkboxes"><%-- populated by javascript --%></div>
                <div id="grade_checkboxes"><%-- populated by javascript --%></div>
                <div id="deliveryMethodString_checkboxes"><%-- populated by javascript --%></div>
                <div id="lmiEligible_checkboxes"><%-- populated by javascript --%></div>
            </div>

            <div class="selectLists">
                <div id="available_select"><%-- populated by javascript --%></div>
                <div id="eventDate_select"><%-- populated by javascript --%></div>
                <div id="eventTime_select"><%-- populated by javascript --%></div>
                <div id="county_select"><%-- populated by javascript --%></div>
                <div id="grade_select"><%-- populated by javascript --%></div>
                <div id="deliveryMethodString_select"><%-- populated by javascript --%></div>
                <div id="lmiEligible_select"><%-- populated by javascript --%></div>
            </div>

        </fieldset>

        <table id="dynamicEventTable" class="responsive"><%-- populated by javascript --%></table>

        <br>

    </main>

</div><%-- .mainCnt --%>

<%@include file="include/footer.jsp" %>

</body>
</html>
