package com.playlistconverter.converter.controller;

import com.playlistconverter.converter.domain.ConversionResponse;
import com.playlistconverter.converter.domain.StreamingServiceType;
import com.playlistconverter.converter.service.ConverterService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/convert")
public class ConverterController {

    private ConverterService converterService;

    public ConverterController(
            ConverterService converterService){
        this.converterService = converterService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConversionResponse convertPlaylist(
            @RequestParam String url,
            @RequestParam(name = "starting") StreamingServiceType startingService,
            @RequestParam(name = "destination") StreamingServiceType destinationService
    ){
        return converterService.convertPlaylist(url, startingService, destinationService);
    }

}
