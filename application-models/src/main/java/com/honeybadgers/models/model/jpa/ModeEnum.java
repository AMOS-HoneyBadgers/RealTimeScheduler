package com.honeybadgers.models.model.jpa;

import com.honeybadgers.models.exceptions.UnknownEnumException;

public enum ModeEnum {
    Sequential, Parallel;

    public static ModeEnum getFromString(String mode) throws UnknownEnumException {
        if(mode == null)
            return null;
        if( mode.compareToIgnoreCase("Sequential") == 0 ){
            return Sequential;
        }else if ( mode.compareToIgnoreCase( "Parallel")  == 0){
            return Parallel;
        }else{
            throw new UnknownEnumException("Unknown Enum: " + mode);
        }
    }
}
