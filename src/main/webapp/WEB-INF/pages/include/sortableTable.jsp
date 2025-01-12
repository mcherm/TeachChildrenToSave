

        /*
        * This is accessed as filterSettings[field][value] and it returns true if that
        * box is checked and false if it isn't. Only the checked values should be shown
        * UNLESS none of the values for a field are checked, in which case that field
        * should not restrict the display of rows in the table.
        */
        var filterSettings = {};

        /*
         * This returns a map giving the count of the times each value of this field has
         * occurred, and an array with all the values that it takes on. For your convenience,
         * the array is sorted.
         */
        function countsAndDistinctValuesForField(tableData, field) {
            var counts = {};
            var values = [];
            $.each(tableData, function(i, row) {
                var value = row[field];
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
        function  sortBy(field, tableData) {
            var descending = $("#col_for_" + field).hasClass("ascending");
            tableData.sort(function(row1, row2) {
                if (descending) {
                    var temp = row1;
                    row1 = row2;
                    row2 = temp;
                }
                if (row1[field] == row2[field]) {
                    return 0;
                } else if (isLessThan(field, row1[field], row2[field])) {
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
        /*
        * This is the function that gets called when a checkbox is clicked.
         */
        function toggleFilter(field, value) {
            filterSettings[field][value] = ! filterSettings[field][value];

            console.log('the filter field/value are: ' + field + ' / ' + value)

            buildTable();
        }

