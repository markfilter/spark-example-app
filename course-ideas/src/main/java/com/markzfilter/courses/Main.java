package com.markzfilter.courses;

import com.markzfilter.courses.model.CourseIdea;
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


        // Middleware ======================================
        // If a filter has no routes defined, then it is fired after every request
        // In this case, if the request has a cookie, then add an attribute to the
        // request.
        // Now, if you have references to checks in routes req.cookie("username"),
        // you can replace it with req.attribute("username")
        before((req, res) -> {
            if (req.cookie("username") != null) {
                req.attribute("username", req.cookie("username"));
            }
        });


        // First, catch the Uri you want to protect.
        // Second, filter object (req, res)
        // Third, check to make sure that a username exists in the cookie
        // if it does NOT, then redirect to the Home page.
        // Add the halt() method to prevent further processing from additional routes
        // that could be triggered by the redirect.
        before("/ideas", (req, res) -> {
            if (req.attribute("username") == null) {
                res.redirect("/");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.attribute("username"));

            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


        post("/sign-in", (req, res) -> {

            Map<String, String> model = new HashMap<>();
            String username = req.queryParams("username");

            res.cookie("username", username);
            model.put("username", username);

            return new ModelAndView(model, "sign-in.hbs");
        }, new HandlebarsTemplateEngine());


        get("/ideas", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("ideas", dao.findAll());

            return new ModelAndView(model, "ideas.hbs");
        }, new HandlebarsTemplateEngine());


        post("/ideas", (req, res) -> {

            String title = req.queryParams("title");

            CourseIdea courseIdea = new CourseIdea(title, req.attribute("username"));
            dao.add(courseIdea);

            res.redirect("/ideas");

            return null;
        });


        post("/ideas/:slug/vote", (req, res) -> {

            CourseIdea idea = dao.findBySlug(req.params("slug"));
            idea.addVoter(req.attribute("username"));
            res.redirect("/ideas");
            return null;

        });


        get("/ideas/:slug/vote", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("idea", dao.findBySlug(req.params("slug")));
            return new ModelAndView(model, "idea.hbs");
        }, new HandlebarsTemplateEngine());
    }

}
