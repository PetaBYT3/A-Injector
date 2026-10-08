package com.a.injector;

interface IShellService {
    int exec(String command) = 1;
    void destroy() = 16777114;
}