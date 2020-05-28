package com.honeybadgers.realtimescheduler.model;

public enum TypeFlagEnum {
    Batch, Realtime;

    public static TypeFlagEnum getFromString(String mode){
        if( mode.compareToIgnoreCase("Batch") == 0 ){
            return Batch;
        } else if ( mode.compareToIgnoreCase( "Realtime")  == 0){
            return Realtime;
        } else {
            return Batch;
        }
    }
}
