##Get All Without Filter
- Endpoint : `/api/warehouses/_get-all`
- Method : GET
- Description : API to get all warehouse **without** filter and sort
- Notes : -

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
      "name": String
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