package org.nrg.transporter.rest;


import org.nrg.transporter.model.ServerStatus;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransportRestApi {

    TransporterService transporterService;

    @Autowired
    public TransportRestApi(TransporterService transporterService) throws Exception{
        this.transporterService = transporterService;
    }

    @GetMapping("/transport/status")
    public String status() {

        //TODO: Report status of the Transporter App.

        return "OK";
    }

    @GetMapping("/xnat/status")
    public String xnatStats() {
        if( transporterService.xnatHostStatus())
            return "OK";
        else
            return "ERROR";
    }

    // Get endpoint to report the status of the SCP servers
    @GetMapping("/scp/status")
    public ResponseEntity<List<ServerStatus>> scpServerStatus() {
        return ResponseEntity.ok(transporterService.scpServerStatus());
    }

    // Return a list of snapshots available to the user
    @GetMapping("/snapshots")
    public ResponseEntity<List<DataSnap>> getDataSnaps(@RequestParam String user, @RequestParam String token) {
        return ResponseEntity.ok(transporterService.getDataSnaps(user, token));
    }

}
