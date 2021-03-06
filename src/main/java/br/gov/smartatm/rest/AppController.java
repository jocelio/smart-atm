package br.gov.smartatm.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

    @RequestMapping("/")
    String home(ModelMap modal) {
        return "index";
    }

    @RequestMapping("/{page}")
    String pageHandler(@PathVariable("page") final String page) {
        return page;
    }

}
