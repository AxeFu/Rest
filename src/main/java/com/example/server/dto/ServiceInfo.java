package com.example.server.dto;

public class ServiceInfo
{
    public final String serviceName;
    public final String command;
    public ServiceInfo(String serviceName, String command) {
        this.serviceName = serviceName;
        this.command = command;
    }

}
