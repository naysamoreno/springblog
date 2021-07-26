package com.codeup.springblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DiceController {


    @GetMapping("/roll-dice")
    public String guessNumberForm() {
        return "guess";

    }

    @GetMapping("/roll-dice/{n}")
    public String checkGuess(@PathVariable int n, Model model) {
        int magicNumber = (int) (Math.floor(Math.random()) * 6) + 1;
        boolean check = magicNumber == n;

        model.addAttribute("n",n);
        model.addAttribute("magicNumber", magicNumber);
        model.addAttribute("check", check);
        return "dice-roll-check";
    }


}
