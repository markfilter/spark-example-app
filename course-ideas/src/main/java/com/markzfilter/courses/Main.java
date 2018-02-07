package com.markzfilter.courses;

import com.markzfilter.courses.model.CourseIdea;
import com.markzfilter.courses.model.CourseIdeaDAO;
import com.markzfilter.courses.model.NotFoundException;
import com.markzfilter.courses.model.SimpleCourseIdeaDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    private static final String FLASH_MESSAGE_KEY = "flash_message";

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
                setFlashMessage(req, "Whoops! Please sign in first!");
                res.redirect("/");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.attribute("username"));
            model.put("flashMessage", captureFlashMessage(req));
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
            model.put("flashMessage", captureFlashMessage(req));
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
            boolean added = idea.addVoter(req.attribute("username"));
            if (added) {
                setFlashMessage(req, "Thanks for your vote!");
            }
            else {
                setFlashMessage(req, "You already voted!");
            }
            res.redirect("/ideas");
            return null;

        });


        get("/ideas/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("idea", dao.findBySlug(req.params("slug")));
            return new ModelAndView(model, "idea.hbs");
        }, new HandlebarsTemplateEngine());



        // Handling Exceptions
        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });

    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }

        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }

        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }


    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);

        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

}
