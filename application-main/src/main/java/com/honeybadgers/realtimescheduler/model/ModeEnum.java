package com.honeybadgers.realtimescheduler.model;

public enum ModeEnum {
    Sequential, Parallel;

    public static ModeEnum getFromString(String mode){
        if( mode.compareToIgnoreCase("Sequential") == 0 ){
            return Sequential;
        }else if ( mode.compareToIgnoreCase( "Parallel")  == 0){
            return Parallel;
        }else{
            return Parallel;
        }
    }
}
