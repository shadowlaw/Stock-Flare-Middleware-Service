# Stock Flare Middleware v0.3.2

Stock Flare is an application built to monitor symbol data and events; and send alerts via different mediums.

This REST API serves clients of the Stock Flare application, by providing an interface to retrieve symbol details and manage notification subscriptions.

## Technologies Used
This API was build with:
- Java 11
- Spring Boot 2
- Maven

# Demo
The [API Demo](https://stockflare-api.demospiral.com/) current has symbol details available from the Jamaican stock market and can be queried without a medium ID. Notification subscription details is unique to a medium ID. Each endpoint can be queried with a medium ID and notification Types below.

**Medium IDs:**
- 938293748

**Symbol Price Notification Types (Symbol Closing Price):**
| Type | Description  |
|---|---|
|  PRC_TRGT_PCNTG_CHG_DEC | Target Precentage Change Decrease  |
|  PRC_TRGT_PCNTG_CHG_INC | Target Precentage Change Increase  |
|  PRC_TRGT_VAL_DEC | Target Value Decrease  |
|  PRC_TRGT_VAL_INC | Target Value Increase  |
|  PRC_VAL_UP_ALL |  All Price Value Changes |

**Symbol News Notification Types**
| Type  | Description  |
|---|---|
|  DIVDEC | Dividend Declarations  |
