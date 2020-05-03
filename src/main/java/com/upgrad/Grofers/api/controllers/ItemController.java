package com.upgrad.Grofers.api.controller;

import com.upgrad.Grofers.api.model.ItemList;
import com.upgrad.Grofers.api.model.ItemListResponse;
import com.upgrad.Grofers.service.businness.ItemService;
import com.upgrad.Grofers.service.businness.StoreService;
import com.upgrad.Grofers.service.entity.ItemEntity;
import com.upgrad.Grofers.service.entity.StoreEntity;
import com.upgrad.Grofers.service.exception.StoreNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private StoreService storeService;

    @RequestMapping(method = RequestMethod.GET, path = "/item/Store/{Store_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemByPopularity(@PathVariable("Store_id") final String StoreUuid)
            throws StoreNotFoundException {

        StoreEntity StoreEntity = storeService.getStoreByUUId(StoreUuid);

        if (StoreEntity == null) {
            throw new StoreNotFoundException("RNF-001", "No Store by this id");
        }

        List<ItemEntity> itemEntityList = itemService.getItemsByPopularity(StoreEntity);

        ItemListResponse itemListResponse = new ItemListResponse();

        int itemCount = 0;

        for(ItemEntity ie: itemEntityList) {
            ItemList itemList = new ItemList().id(UUID.fromString(ie.getUuid()))
                    .itemName(ie.getItemName()).price(ie.getPrice()).itemType(ie.getType());
            itemListResponse.add(itemList);
            itemCount += 1;
            if (itemCount >= 5)
                break;
        }

        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }
}
