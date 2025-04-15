package net.project.slounik.utils;

import java.util.Comparator;

public class ComparatorForMap implements Comparator<String> {
    private final String str;

    public ComparatorForMap(String str) {
        this.str=str;
    }

    @Override
    public int compare(String o1, String o2) {
        if(o1.indexOf(str)==o2.indexOf(str)&&!o1.equals(o2))return o1.compareTo(o2);
        return o1.indexOf(str)-o2.indexOf(str);
    }
}
