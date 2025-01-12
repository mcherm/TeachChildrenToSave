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
