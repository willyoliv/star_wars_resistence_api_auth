package com.oliveira.willy.starwarsresistence.controller;

import com.oliveira.willy.starwarsresistence.dto.*;
import com.oliveira.willy.starwarsresistence.mapper.*;
import com.oliveira.willy.starwarsresistence.model.Item;
import com.oliveira.willy.starwarsresistence.model.Location;
import com.oliveira.willy.starwarsresistence.model.Rebel;
import com.oliveira.willy.starwarsresistence.service.RebelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("rebels")
public class RebelController {
    private final RebelService rebelService;

    private final LocationMapper locationMapper;

    private final LocationResponseDtoMapper locationResponseDtoMapper;

    private final ItemMapper itemMapper;

    @PutMapping(path = "/{rebelId}/update-location")
    private ResponseEntity<LocationResponseDto> updateRebelLocation(@PathVariable("rebelId") Long rebelId,
                                                               @Valid @RequestBody LocationDto locationDto) {
        Location location = rebelService.updateRebelLocation(locationMapper.locationDTOToLocation(locationDto), rebelId);
        LocationResponseDto locationResponseDto = locationResponseDtoMapper.locationToLocationResponseDto(location);
        return new ResponseEntity<>(locationResponseDto, HttpStatus.OK);
    }

    @PostMapping(path = "/{accuserId}/report-traitor")
    private ResponseEntity<SuccessMessage> reportRebelTraitor(@PathVariable("accuserId") Long accuserId,
                                                              @Valid @RequestBody ReportCreateDto reportCreateDto) {
        Rebel accuser = this.rebelService.findRebelById(accuserId);
        Rebel accused = this.rebelService.findRebelById(reportCreateDto.getAccusedId());
        rebelService.reportRebelTraitor(accuser, accused, reportCreateDto.getReason());
        return new ResponseEntity<>(new SuccessMessage("Report made successfully."), HttpStatus.OK);
    }

    @PostMapping(path = "/inventory/trade")
    public ResponseEntity<SuccessMessage> tradeItemsBetweemRebels(@Valid @RequestBody TradeDto tradeDto) {
        Rebel fromRebel = rebelService.findRebelById(tradeDto.getFromRebel().getRebelId());
        Rebel toRebel = rebelService.findRebelById(tradeDto.getToRebel().getRebelId());
        List<Item> fromRebelItems = tradeDto.getFromRebel().getItems().stream()
                .map(itemMapper::itemDTOToItem).collect(Collectors.toList());
        List<Item> toRebelItems = tradeDto.getToRebel().getItems().stream()
                .map(itemMapper::itemDTOToItem).collect(Collectors.toList());
        rebelService.trade(fromRebel, toRebel, fromRebelItems, toRebelItems);
        return new ResponseEntity<>(new SuccessMessage("Trade made successfully."), HttpStatus.OK);
    }
}
