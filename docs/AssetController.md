##Create
- Endpoint : `/api/assets/_create`
- Method : POST
- Description : API to create new asset
- Notes :
  > Possible Values : <br>
  > organisation : GDN, GDP, GDP2, GDP3, DJARUM <br>
  > status : NORMAL, RUSAK_PARAH_SUDAH_BAC, RUSAK_PARAH_BELUM_BAC <br>
  > purchase : BUY, RENT <br>
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
    "assetNumber": String,
    "organisation": String,
    "itemCode": String,
    "vendor": String,
    "location": String,
    "poNumber": String,
    "poIssuedDate": Long,
    "price": Integer,
    "status": String,
    "deliveryDate": Long,
    "notes": String,
    "vehiclePlate": String,
    "nomorRangka": String,
    "nomorMesin": String,
    "purchase": String,
    "category": String
  }
  ```
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": String //assetNumber
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
- Endpoint : `/api/assets/_update`
- Method : POST
- Description : API to update an asset
- Notes :
  > Possible Values : <br>
  > organisation : GDN, GDP, GDP2, GDP3, DJARUM <br>
  > status : NORMAL, RUSAK_PARAH_SUDAH_BAC, RUSAK_PARAH_BELUM_BAC <br>
  > purchase : BUY, RENT <br>
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
    "assetNumber": String,
    "organisation": String,
    "itemCode": String,
    "vendor": String,
    "location": String,
    "poNumber": String,
    "poIssuedDate": Long,
    "price": Integer,
    "status": String,
    "deliveryDate": Long,
    "notes": String,
    "vehiclePlate": String,
    "nomorRangka": String,
    "nomorMesin": String,
    "purchase": String
  }
  ```
- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": Boolean
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

##Get Detail
- Endpoint : `/api/assets/_get-detail/{assetNumber}`
- Method : GET
- Description : API to get asset's detail
- Notes :
  > Possible Values : <br>
  > organisation : GDN, GDP, GDP2, GDP3, DJARUM <br>
  > status : NORMAL, SCHEDULED_MAINTENANCE, PENDING_MAINTENANCE_REQUEST, PENDING_MAINTENANCE, ON_MAINTENANCE, PENDING_TRANSFER, ON_TRANSFER, ON_TRANSFER_DELIVERY, RUSAK_PARAH_SUDAH_BAC, RUSAK_PARAH_BELUM_BAC <br>
  > purchase : BUY, RENT <br>
  > category : MHE, SHR, LOG, OTH <br>
  > dipinjam : YA, TIDAK

- Request Param :
  - storeId : String
  - channelId : String
  - clientId : String
  - requestId : String
  - username : String

- Response Body Success :
  ```json
  {
    "code": 200,
    "status": "OK",
    "data": {
      "assetNumber": String,
      "organisation": String,
      "itemName": String,
      "itemCode": String,
      "vendor": String,
      "location": String,
      "poNumber": String,
      "poIssuedDate": Long,
      "price": Integer,
      "status": String,
      "deliveryDate": Long
      "notes": String,
      "purchase": String,
      "category": String,
      "dipinjam": String
    }
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

##Get All
- Endpoint : `/api/assets/_get-all`
- Method : POST
- Description : API to get all asset with filter and sort
- Notes :
  > Possible Values : <br>
  > organisation : GDN, GDP, GDP2, GDP3, DJARUM <br>
  > status : NORMAL, SCHEDULED_MAINTENANCE, PENDING_MAINTENANCE_REQUEST, PENDING_MAINTENANCE, ON_MAINTENANCE, PENDING_TRANSFER, ON_TRANSFER, ON_TRANSFER_DELIVERY, RUSAK_PARAH_SUDAH_BAC, RUSAK_PARAH_BELUM_BAC <br>
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
      "assetNumber": String,
      "organisation": String,
      "vendor": String,
      "itemCode": String,
      "location": String,
      "status": String,
      "category": String
    },
    "sorts": {
      "assetNumber": String
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
      "assetNumber": String,
      "organisation": String,
      "vendor": String,
      "itemName": String,
      "location": String,
      "status": String
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