package de.hu.flinkydust.main;

import de.hu.flinkydust.test.StreamDataSourceTest;
import org.junit.runner.JUnitCore;

/**
 * Created by Jan-Christopher on 28.11.2016.
 */
public class FlinkydustMain {

    public static void main(String[] args) {
        JUnitCore.runClasses(StreamDataSourceTest.class);
    }

}
