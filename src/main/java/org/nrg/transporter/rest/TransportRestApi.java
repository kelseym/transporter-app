package org.nrg.transporter.rest;


import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransportRestApi {

    @PostMapping("/status")
    public String status() {

        //TODO: Report status of the Transporter App.

        return "OK";
    }
}
