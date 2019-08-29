package com.fly.scanner.service;

import com.fly.scanner.entity.Scanner;

import java.io.File;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 15:57 2017/10/31
 */
public interface ScannerService {
    Object[] scanMedia2DB(File fileDir, Scanner scanner);
}
