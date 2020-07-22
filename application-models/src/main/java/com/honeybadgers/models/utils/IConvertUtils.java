package com.honeybadgers.models.utils;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public interface IConvertUtils {
    /**
     * Returns the given timestamp as OffsetDateTime with UTC as ZoneOffset
     * @param timestamp timestamp to be converted
     * @return OffsetDateTime at UTC ZoneOffset representing the input
     */
    default OffsetDateTime timestampJpaToRest(Timestamp timestamp) {
        if(timestamp == null)
            return null;
        return timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * Returns given offsetDateTime as sql.Timestamp
     * @param offsetDateTime offsetDateTime to be converted
     * @return Timestamp representing input
     */
    default Timestamp timestampRestToJpa(OffsetDateTime offsetDateTime) {
        if(offsetDateTime == null)
            return null;
        // get input as milliseconds since epoch
        long dateTimeAsMilli = Long.parseLong(offsetDateTime.toEpochSecond() + Long.toString(offsetDateTime.getNano()).substring(0, 3));
        return new Timestamp(dateTimeAsMilli);
    }

    /**
     * Convert a list of booleans to an array of int while maintaining its 'value' (true -> 1, false -> 0)
     * ATTENTION: if input is null: return new int[]{1, 1, 1, 1, 1, 1, 1};
     * @param booleans input
     * @return int[] representing input or int[]{1, 1, 1, 1, 1, 1, 1} as default
     */
    default int[] boolListToIntArray(List<Boolean> booleans) {
        if(booleans == null)
            return new int[]{1, 1, 1, 1, 1, 1, 1};
        return booleans.stream().mapToInt(value -> {
            if (value == null)
                return 1;
            // convert boolean to int
            return (value ? 1 : 0);
        }).toArray();
    }

    /**
     * Convert an array of ints to a list of booleans while maintaining its 'value' (0 -> false, else -> true)
     * ATTENTION: if input is null: return Arrays.asList(true, true, true, true, true, true, true);
     * @param ints input
     * @return List of booleans representing input or List{true, true, true, true, true, true, true}
     */
    default List<Boolean> intArrayToBoolList(int[] ints) {
        if(ints == null)
            return Arrays.asList(true, true, true, true, true, true, true);
        return Arrays.stream(ints).mapToObj(value -> (value != 0)).collect(Collectors.toList());
    }
}
