package com.upgrad.Grofers.api.controller;

import com.upgrad.Grofers.api.model.*;
import com.upgrad.Grofers.service.businness.*;
import com.upgrad.Grofers.service.entity.*;
import com.upgrad.Grofers.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateService StateService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerService customerService;

    /**
     *
     * @return List of all Stores in the database
     *
     */
    @RequestMapping(method = RequestMethod.GET, path = "/Store", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StoreListResponse> getAllStores() {

        // Getting the list of all Stores with help of Store business service
        final List<StoreEntity> allStores = storeService.getAllStores();

        StoreListResponse StoreListResponse = new StoreListResponse();

        // Adding the list of Stores to StoreList
        List<StoreList> details = new ArrayList<StoreList>();
        for (StoreEntity n: allStores) {
            StoreList detail = new StoreList();
            detail.setId(UUID.fromString(n.getUuid()));
            detail.setStoreName(n.getStoreName());
            detail.setPhotoURL(n.getPhotoUrl());
            detail.setCustomerRating(n.getCustomerRating());
            detail.setAveragePrice(n.getAvgPriceForTwo());
            detail.setNumberCustomersRated(n.getNumCustomersRated());

            // Getting address of Store from address entity
            AddressEntity addressEntity = addressService.getAddressById(n.getAddress().getId());
            StoreDetailsResponseAddress responseAddress = new StoreDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Getting state for current address from state entity
            StateEntity stateEntity = StateService.getStateById(addressEntity.getState().getId());
            StoreDetailsResponseAddressState responseAddressState = new StoreDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // Setting address with state into Store obj
            detail.setAddress(responseAddress);

            // Looping categories and setting name values only
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :n.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sorting category list on name
            Collections.sort(categoryLists);

            // Joining List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            // Add category detail to details(StoreList)
            //details.add(detail);
            StoreListResponse.addStoresItem(detail);
        }

        // return response entity with StoreLists(details) and Http status
        return new ResponseEntity<StoreListResponse>(StoreListResponse, HttpStatus.OK);
    }

    /**
     *
     * @param Store_name
     * @return List of all Stores matched with given Store name
     * @throws StoreNotFoundException - when Store name field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/Store/name/{Store_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StoreListResponse> getStoresByName(@PathVariable String Store_name)
            throws StoreNotFoundException {

        // Throw exception if path variable(Store_name) is empty
        if(Store_name == null || Store_name.isEmpty() || Store_name.equalsIgnoreCase("\"\"")){
            throw new StoreNotFoundException("RNF-003", "Store name field should not be empty");
        }

        // Getting the list of all Stores with help of Store business service based on input Store name
        final List<StoreEntity> allStores = storeService.getStoresByName(Store_name);

        StoreListResponse StoreListResponse = new StoreListResponse();

        // Adding the list of Stores to StoreList
        List<StoreList> details = new ArrayList<StoreList>();
        for (StoreEntity n: allStores) {
            StoreList detail = new StoreList();
            detail.setId(UUID.fromString(n.getUuid()));
            detail.setStoreName(n.getStoreName());
            detail.setPhotoURL(n.getPhotoUrl());
            detail.setCustomerRating(n.getCustomerRating());
            detail.setAveragePrice(n.getAvgPriceForTwo());
            detail.setNumberCustomersRated(n.getNumCustomersRated());

            // Getting address of Store from address entity
            AddressEntity addressEntity = addressService.getAddressById(n.getAddress().getId());
            StoreDetailsResponseAddress responseAddress = new StoreDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Getting state for current address from state entity
            StateEntity stateEntity = StateService.getStateById(addressEntity.getState().getId());
            StoreDetailsResponseAddressState responseAddressState = new StoreDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // Setting address with state into Store obj
            detail.setAddress(responseAddress);

            // Looping categories and setting name values only
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :n.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sorting category list on name
            Collections.sort(categoryLists);

            // Joining List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            // Add category detail to details(StoreList)
            //details.add(detail);

            StoreListResponse.addStoresItem(detail);

        }

        // return response entity with StoreLists(details) and Http status
        return new ResponseEntity<StoreListResponse>(StoreListResponse, HttpStatus.OK);
    }

    /**
     *
     * @param category_id
     * @return List of all Stores having given category id
     * @throws CategoryNotFoundException - When Given category id  field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/Store/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getStoreByCategoryId(@PathVariable String category_id) throws CategoryNotFoundException {

        // Throw exception if path variable(category_id) is empty
        if(category_id == null || category_id.isEmpty() || category_id.equalsIgnoreCase("\"\"")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        // Getting category which matched with given category_id with help of category business service
        CategoryEntity matchedCategory = categoryService.getCategoryEntityByUuid(category_id);

        // Throw exception if given category_id not matched with any category in DB
        if(matchedCategory == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        // If given category_id match with any category in DB, then get all Stores having matched category
        final List<StoreCategoryEntity> allStoreCategories = storeService.getStoreByCategoryId(matchedCategory.getId());

        // Adding the list of Stores to StoreList
        List<StoreList> details = new ArrayList<StoreList>();
        for (StoreCategoryEntity StoreCategoryEntity:allStoreCategories) {
            StoreEntity n = StoreCategoryEntity.getStore();
            StoreList detail = new StoreList();
            detail.setId(UUID.fromString(n.getUuid()));
            detail.setStoreName(n.getStoreName());
            detail.setPhotoURL(n.getPhotoUrl());
            detail.setCustomerRating(n.getCustomerRating());
            detail.setAveragePrice(n.getAvgPriceForTwo());
            detail.setNumberCustomersRated(n.getNumCustomersRated());

            // Getting address of Store from address entity
            AddressEntity addressEntity = addressService.getAddressById(n.getAddress().getId());
            StoreDetailsResponseAddress responseAddress = new StoreDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Getting state for current address from state entity
            StateEntity stateEntity = StateService.getStateById(addressEntity.getState().getId());
            StoreDetailsResponseAddressState responseAddressState = new StoreDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // Setting address with state into Store obj
            detail.setAddress(responseAddress);

            // Looping categories and setting name values only
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :n.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sorting category list on name
            Collections.sort(categoryLists);

            // Joining List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            // Add category detail to details(StoreList)
            details.add(detail);
        }

        // return response entity with StoreLists(details) and Http status
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /**
     *
     * @param Store_id
     * @return Store with details based on given Store id
     * @throws StoreNotFoundException - When given Store id field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/Store/{Store_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getStoreByUUId(@PathVariable String Store_id) throws StoreNotFoundException {

        // Throw exception if path variable(Store_id) is empty
        if(Store_id == null || Store_id.isEmpty() || Store_id.equalsIgnoreCase("\"\"")){
            throw new StoreNotFoundException("RNF-002", "Store id field should not be empty");
        }

        // Getting Store which matched with given Store_id with help of Store business service
        final StoreEntity Store = storeService.getStoreByUUId(Store_id);

        // Throw exception if given Store_id not matched with any Store in DB
        if(Store == null){
            throw new StoreNotFoundException("RNF-001", "No Store by this id");
        }

        // Adding the list of Store details to StoreDetailsResponse
        StoreDetailsResponse details = new StoreDetailsResponse();
        details.setId(UUID.fromString(Store.getUuid()));
        details.setStoreName(Store.getStoreName());
        details.setPhotoURL(Store.getPhotoUrl());
        details.setCustomerRating(Store.getCustomerRating());
        details.setAveragePrice(Store.getAvgPriceForTwo());
        details.setNumberCustomersRated(Store.getNumCustomersRated());

        // Getting address of Store from address entity
        AddressEntity addressEntity = addressService.getAddressById(Store.getAddress().getId());
        StoreDetailsResponseAddress responseAddress = new StoreDetailsResponseAddress();

        responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
        responseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
        responseAddress.setLocality(addressEntity.getLocality());
        responseAddress.setCity(addressEntity.getCity());
        responseAddress.setPincode(addressEntity.getPincode());

        // Setting address with state into Store obj
        StateEntity stateEntity = StateService.getStateById(addressEntity.getState().getId());
        StoreDetailsResponseAddressState responseAddressState = new StoreDetailsResponseAddressState();

        responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
        responseAddressState.setStateName(stateEntity.getStateName());
        responseAddress.setState(responseAddressState);

        // Setting address with state into Store obj
        details.setAddress(responseAddress);

        // Looping categories and setting  values
        List<CategoryList> categoryLists = new ArrayList();
        for (CategoryEntity categoryEntity :Store.getCategoryEntities()) {
            CategoryList categoryListDetail = new CategoryList();
            categoryListDetail.setId(UUID.fromString(categoryEntity.getUuid()));
            categoryListDetail.setCategoryName(categoryEntity.getCategoryName());

            // Looping items and setting to category
            List<ItemList> itemLists = new ArrayList();
            for (ItemEntity itemEntity :categoryEntity.getItemEntities()) {
                ItemList itemDetail = new ItemList();
                itemDetail.setId(UUID.fromString(itemEntity.getUuid()));
                itemDetail.setItemName(itemEntity.getItemName());
                itemDetail.setPrice(itemEntity.getPrice());
                itemDetail.setItemType(itemEntity.getType());
                itemLists.add(itemDetail);
            }
            categoryListDetail.setItemList(itemLists);

            // Adding category to category list
            categoryLists.add(categoryListDetail);
        }

        // Add category detail to details(Store)
        details.setCategories(categoryLists);

        // return response entity with StoreDetails(details) and Http status
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /**
     *
     * @param authorization, customerRating, Store_id
     * @return Store uuid of the rating updated Store
     * @throws StoreNotFoundException - When given Store id field is empty
     *         AuthorizationFailedException - When customer is not logged in or logged out or login expired
     *         InvalidRatingException - When the Rating value provided is invalid
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/Store/{Store_id}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StoreUpdatedResponse> updateCustomerRating(@RequestHeader("authorization") final String authorization, @RequestParam Double customerRating, @PathVariable String Store_id )
            throws AuthorizationFailedException, InvalidRatingException, StoreNotFoundException {

        // Get the bearerToken
        String[] bearerToken = authorization.split("Bearer ");

        // Call the storeService to update the customer
        StoreEntity StoreEntity = storeService.updateCustomerRating(customerRating, Store_id, bearerToken[1]);

        // Attach the details to the updateResponse
        StoreUpdatedResponse StoreUpdatedResponse = new StoreUpdatedResponse()
                .id(UUID.fromString(StoreEntity.getUuid())).status("Store RATING UPDATED SUCCESSFULLY");

        // Returns the StoreUpdatedResponse with OK http status
        return new ResponseEntity<StoreUpdatedResponse>(StoreUpdatedResponse, HttpStatus.OK);
    }

}