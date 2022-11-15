# Luminous

A system providing [candle charts](https://www.tradingview.com/chart/?symbol=FX%3AEURUSD) information.

### Description

#### Background

Luminous was developed as part of an application process at a financial institution. The provided [service](system/partner/README.md) has now also been fully re-implemented and co-exists within this repository for simplicity reasons.
A few assumptions had to be made, given that the protocol streams financial data points over multiple websocket connections without further correlation.
Please have a look at the corresponding document about that service and protocol.

The commits haven't changed much, besides some repackaging (mostly naming) or feature branch transplantation of the newly implemented service.

#### Candle

A candle aggregates quote information over a period, like 1m, 10m or 1h and so forth. It's also worth mentioning that the close and open price information of adjacent periods could be different, as a quote reflects the most recent price that a buyer and seller agreed upon.
It usually composes the following attributes:

| Property | Type      | Description                                                       |
|----------|-----------|-------------------------------------------------------------------|
| open     | numerical | current price of the first quote related to the underlying period |
| opened   | instant   | timestamp of the first quote related to the underlying period     |
| close    | numerical | current price of the last quote related to the underlying period  |
| closed   | instant   | timestamp of the last quote related to the underlying period      |
| high     | numerical | highest price of all quotes related to the underlying period      |
| low      | numerical | lowest price of all quotes related to the underlying period       |

A candle chart is therefore a fixed-length bucketed stream of quote information, where every bucket is transformed into a candle with the named information, and can be displayed in a linear way.
It should be considered that buckets could also be empty if quotes do not change over time. This case is handled using a replication strategy, taking the nearest (time-based) known bucket or candle, respectively.
It's fair to also represent the most recent candle with the present information for convenience reasons, even if it's actually not finished or closed.

A time-based bucket uses a [half closed interval](https://mathworld.wolfram.com/Half-ClosedInterval.html) and is usually aligned with our natural feeling of time, meaning that an hourly chart wouldn't start at 12:14, but rather compose quotes from [12:00, 13:00) and likewise other periods.
However, the latter is not a hard requirement.

Numerical price information could be pinned using fix point decimals or treated more leniently using floating values. A technical chart analysis may not require 100% accurate data, but it may also not hurt if it meets the business requirements.

### API

A few REST endpoints are provided to show the raw information about quotes and instruments, further backed by an endpoint displaying the actual chart information.
A chart will already be conveniently back-filled. A streaming approach using a "packed" or "compact" protocol would be a better solution, also from a UX point of view. It's only a small portion of the chart that is changing frequently.

Please have a look at the corresponding [postman collection](manual/postman/partner.collection.json) below our manual.

### Development

Please execute the following steps. The first step needs to be executed whenever the partner service changes or hasn't built at all.

1. `docker-compose build` to build a dockerized version of the partner service. This image is required during the testing phase and can only be obtained through a local build.
2. `./gradlew build` to build the quarkus application from a shell, which also bundles a docker container.

### Demo

Please start the system. It's also possible to run the application from Intellij IDEA

1. `docker-compose -f docker-compose.yml -f docker-compose-self.yml up` to start the partner service with a default configuration along with luminous.
2. A [postman collection](manual/postman/partner.collection.json) can be found in the manual directory of this project.
