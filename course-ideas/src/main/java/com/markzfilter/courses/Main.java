package com.markzfilter.courses;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        get("/", (req, res) -> "<h1>Welcome Students!</h1>");

        get("/hello", (req, res) -> "<h1>Hello World</h1>");
    }

}
