##Create
- Endpoint : `/api/items/_create`
- Method : POST
- Description : API to create new item
- Notes :
  > Possible Values : <br>
  > category : MHE, SHR, LOG, OTH <br>

- Request Param :
    - storeId : String
    - channelId : String
    - clientId : String
    - requestId : String
    - username : String
      <br></br>
- Request Body :
  ```json
  {
    "itemName": String,
    "category": String
  }
  ```
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": String //itemCode
  }
  ```
- Response Body Error :
  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "errors":{
      "message": [
        String
      ]
    }
  }
  ```

##Update
- Endpoint : `/api/items/_update`
- Method : POST
- Description : API to update an item
- Notes :
  > Possible Values : <br>
  > category : MHE, SHR, LOG, OTH <br>

- Request Param :
  - storeId : String
  - channelId : String
  - clientId : String
  - requestId : String
  - username : String
    <br></br>
- Request Body :
  ```json
  {
    "itemCode": String,
    "itemName": String,
    "category": String
  }
  ```
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": String //itemCode
  }
  ```
- Response Body Error :
  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "errors":{
      "message": [
        String
      ]
    }
  }
  ```

##Get All Without Filter
- Endpoint : `/api/items/_get-all`
- Method : GET
- Description : API to get all item **without** filter and sort
- Notes :
  > Possible Values : <br>
  > category : MHE, SHR, LOG, OTH <br>

- Request Param :
  - storeId : String
  - channelId : String
  - clientId : String
  - requestId : String
  - username : String
    <br></br>
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": [
    {
      "code": String,
      "name": String,
      "category": String
    }
    ],
    "paging": {
      "page": Integer,
      "total_page": Integer,
      "item_per_page": Integer,
      "total_item": Integer,
      "sort_by": [
      {
        "property_name": String,
        "direction": String
      }
      ]
    },
    "errors": null
  }
  ```
- Response Body Error :
  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "errors":{
      "message": [
        String
      ]
    }
  }
  ```

##Get All With Filter
- Endpoint : `/api/items/_get-all-item`
- Method : POST
- Description : API to get all item **with** filter and sort
- Notes :
  > Possible Values : <br>
  > category : MHE, SHR, LOG, OTH <br>
  >> sort is case-insensitive <br>

- Request Param :
  - storeId : String
  - channelId : String
  - clientId : String
  - requestId : String
  - username : String
    <br></br>
- Request Body :
  ```json
  {
    "filters": {
      "code": String,
      "name": String,
      "category": String
    },
    "sorts": {
      "code": String,
      "name": String
    },
    "page": Integer,
    "item_per_page": Integer
  }
  ```
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": [
    {
      "code": String,
      "name": String,
      "category": String
    }
    ],
    "paging": {
      "page": Integer,
      "total_page": Integer,
      "item_per_page": Integer,
      "total_item": Integer,
      "sort_by": [
      {
        "property_name": String,
        "direction": String
      }
      ]
    },
    "errors": null
  }
  ```
- Response Body Error :
  ```json
  {
    "code": 400,
    "status": "BAD_REQUEST",
    "errors":{
      "message": [
        String
      ]
    }
  }
  ```