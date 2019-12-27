package com.moroz.accountservice.controller;

import com.moroz.accountservice.serivce.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/service")
public class Controller {

    @Autowired
    private AccountService accountService;

    @GetMapping("{id}")
    public Long getAmount(@PathVariable Integer id) {
        return accountService.getAmount(id);
    }

    @GetMapping("/statistic")
    public Map<String, Number> getStatistic() {
        return accountService.getStatistic();
    }

    @DeleteMapping("/statistic")
    public void clearStatistic() {
        accountService.clearStatistic();
    }

    @PostMapping("{id}/{value}")
    public void addAmount(@PathVariable Integer id, @PathVariable Long value) {
        accountService.addAmount(id, value);
    }
}
