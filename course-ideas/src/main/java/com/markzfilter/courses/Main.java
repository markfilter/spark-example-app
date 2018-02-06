package com.markzfilter.courses;

import com.markzfilter.courses.model.CourseIdeaDAO;
import com.markzfilter.courses.model.SimpleCourseIdeaDAO;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        staticFileLocation("/public");
        // This is just for prototyping
        // This should be replaced with an implementation for a database or
        // saving to disk.
        CourseIdeaDAO dao = new SimpleCourseIdeaDAO();

//        get("/", (req, res) -> "<h1>Welcome Students!</h1>");
//        get("/hello", (req, res) -> "<h1>Hello World</h1>");

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.cookie("username"));

            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


        post("/sign-in", (req, res) -> {

            Map<String, String> model = new HashMap<>();
            String username = req.queryParams("username");

            res.cookie("username", username);
            model.put("username", username);

            return new ModelAndView(model, "sign-in.hbs");
        }, new HandlebarsTemplateEngine());


    }

}
