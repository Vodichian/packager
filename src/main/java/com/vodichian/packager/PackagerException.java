package com.vodichian.packager;

public class PackagerException extends Exception {
    public PackagerException(String s) {
        super(s);
    }

    public PackagerException(Exception e) {
        super(e);
    }
}
