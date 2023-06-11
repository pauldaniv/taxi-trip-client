package com.pauldaniv.promotion.yellowtaxi.client.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service("performance")
public class PerformanceService implements CmdService {
    @Override
    public void runCommand(List<String> params) {
        System.out.println(params);
    }
}
