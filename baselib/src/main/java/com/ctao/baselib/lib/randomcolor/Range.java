package com.ctao.baselib.lib.randomcolor;

/**
 * https://github.com/lzyzsd/AndroidRandomColor
 * Created by bruce on 15/2/9.
 */
public class Range {
    int start;
    int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean contain(int value) {
        return value >= start && value <= end;
    }

    @Override
    public String toString() {
        return "start: " + start + " end: " + end;
    }
}
