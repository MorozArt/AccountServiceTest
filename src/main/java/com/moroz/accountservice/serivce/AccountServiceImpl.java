package com.moroz.accountservice.serivce;

import com.moroz.accountservice.entity.User;
import com.moroz.accountservice.exceptions.UserNotFoundException;
import com.moroz.accountservice.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UsersRepository usersRepository;

    private AtomicInteger getCount = new AtomicInteger(0);
    private AtomicInteger addCount = new AtomicInteger(0);

    private final Date start;

    public AccountServiceImpl() {
        start = new Date();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAmount(Integer id){
        getCount.incrementAndGet();
        return getUser(id).getAmount();
    }

    @Override
    @Transactional
    public void addAmount(Integer id, Long value) {
        addCount.incrementAndGet();
        User user = getUser(id);
        usersRepository.addAmount(user.getAmount()+value, id);
    }

    private User getUser(Integer id) {
        User user = usersRepository.findById(id).orElse(null);

        if(user == null) {
            throw new UserNotFoundException(id);
        }

        return user;
    }

    @Override
    public Map<String, Number> getStatistic() {
        Map<String, Number> statisctic = new HashMap<>();
        float workingTime = (new Date().getTime() - start.getTime())/1000;

        statisctic.put("Total call getAmount: ", getCount.intValue());
        statisctic.put("Average getAmount per minute: ", getCount.intValue()/workingTime*60);
        statisctic.put("Total call addAmount: ", addCount.intValue());
        statisctic.put("Average addAmount per minute: ", addCount.intValue()/workingTime*60);

        return statisctic;
    }

    @Override
    public void clearStatistic() {
        getCount = new AtomicInteger(0);
        addCount = new AtomicInteger(0);
    }
}
