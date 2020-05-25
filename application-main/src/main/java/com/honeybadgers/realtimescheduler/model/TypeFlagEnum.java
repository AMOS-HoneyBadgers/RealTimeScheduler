package com.honeybadgers.realtimescheduler.model;

public enum TypeFlagEnum {
    Batch, User;

    public static TypeFlagEnum getFromString(String mode){
        if( mode.compareToIgnoreCase("Batch") == 0 ){
            return Batch;
        }else if ( mode.compareToIgnoreCase( "User")  == 0){
            return User;
        }else{
            return User;
        }
    }
}
