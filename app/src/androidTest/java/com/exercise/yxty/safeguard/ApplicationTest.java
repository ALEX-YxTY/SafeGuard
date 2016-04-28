package com.exercise.yxty.safeguard;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.exercise.yxty.safeguard.db.BlackListDAO;

import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public BlackListDAO blackListDAO;

    public void addTest() {
        blackListDAO = new BlackListDAO(getContext());
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            blackListDAO.add((13000000000l + i) + "", (random.nextInt(3) +1)+ "");
        }
    }

    public void delete() {
        blackListDAO = new BlackListDAO(getContext());
        blackListDAO.delete("13000000005");
    }

    public void update() {
        blackListDAO = new BlackListDAO(getContext());
        blackListDAO.changeMode("13000000003", "1");
    }

    public void count() {
        blackListDAO = new BlackListDAO(getContext());
        int count = blackListDAO.count();
        System.out.println("the number of the blacklist is:" + count);
    }
}