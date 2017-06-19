package com.github.lambdas;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

/**
 * User: Boris
 * Date: 06.06.2017
 * Time: 18:01
 */
public class JUnitInvoker {

    public static void main(String[] args) throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(Request.method(
                Class.forName(args[0]), args[1])
        );
    }


}
