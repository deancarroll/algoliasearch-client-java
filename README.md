Algolia Search API Client for Java
==================





[Algolia Search](http://www.algolia.com) is a search API that provides hosted full-text, numerical and faceted search.
Algolia’s Search API makes it easy to deliver a great search experience in your apps & websites providing:

 * REST and JSON-based API
 * search among infinite attributes from a single searchbox
 * instant-search after each keystroke
 * relevance & popularity combination
 * typo-tolerance in any language
 * faceting
 * 99.99% SLA
 * first-class data security

This Java client let you easily use the Algolia Search API from your Java Application. It wraps [Algolia's REST API](http://www.algolia.com/doc/rest_api).

[![Build Status](https://travis-ci.org/algolia/algoliasearch-client-java.png?branch=master)](https://travis-ci.org/algolia/algoliasearch-client-java) [![GitHub version](https://badge.fury.io/gh/algolia%2Falgoliasearch-client-java.png)](http://badge.fury.io/gh/algolia%2Falgoliasearch-client-java) [![Coverage Status](https://coveralls.io/repos/algolia/algoliasearch-client-java/badge.png)](https://coveralls.io/r/algolia/algoliasearch-client-java)



Table of Content
-------------
**Get started**

1. [Setup](#setup)
1. [Quick Start](#quick-start)
1. [Online documentation](#online-documentation)

**Commands reference**

1. [Add a new object](#add-a-new-object-in-the-index)
1. [Update an object](#update-an-existing-object-in-the-index)
1. [Search](#search)
1. [Get an object](#get-an-object)
1. [Delete an object](#delete-an-object)
1. [Index settings](#index-settings)
1. [List indexes](#list-indexes)
1. [Delete an index](#delete-an-index)
1. [Clear an index](#clear-an-index)
1. [Wait indexing](#wait-indexing)
1. [Batch writes](#batch-writes)
1. [Security / User API Keys](#security--user-api-keys)
1. [Copy or rename an index](#copy-or-rename-an-index)
1. [Backup / Retrieve all index content](#backup--retrieve-all-index-content)
1. [Logs](#logs)





Setup
-------------
To setup your project, follow these steps:

If you're using Maven, add the following dependency and repository to your pom file:
```xml
<dependency>
    <groupId>com.algolia</groupId>
    <artifactId>algoliasearch</artifactId>
    <version>[1.1.11,]</version>
</dependency>
<repository>
    <id>algoliasearch</id>
    <name>Algolia Search Client</name>
    <url>https://raw.github.com/algolia/algoliasearch-client-java/master/repository</url>
</repository>
```

Initialize the client with your ApplicationID and API-Key. You can find all of them on [your Algolia account](http://www.algolia.com/users/edit):



```java
  APIClient client = new APIClient("YourApplicationID", "YourAPIKey");
```



Quick Start
-------------

This quick start is a 30 seconds tutorial where you can discover how to index and search objects.

Without any prior-configuration, you can index some contacts in the ```contacts``` index with the following code:
```java
Index index = client.initIndex("contacts");
index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
index.addObject(new JSONObject()
      .put("firstname", "Warren")
      .put("lastname", "Speach")
      .put("followers", 42)
      .put("company", "Norwalk Crmc"));
```

You can then start to search for a contact firstname, lastname, company, ... (even with typos):
```java
// search by firstname
System.out.println(index.search(new Query("jimmie")));
// search a firstname with typo
System.out.println(index.search(new Query("jimie")));
// search for a company
System.out.println(index.search(new Query("california paint")));
// search for a firstname & company
System.out.println(index.search(new Query("jimmie paint")));
```

Settings can be customized to tune the search behavior. For example you can add a custom sort by number of followers to the already good out-of-the-box relevance:
```java
index.setSettings(new JSONObject().append("customRanking", "desc(followers)"));
```

You can also configure the list of attributes you want to index by order of importance (first = most important):
```java
index.setSettings(new JSONObject()
      .append("attributesToIndex", "lastname")
      .append("attributesToIndex", "firstname")
      .append("attributesToIndex", "company"));

```

Since the engine is designed to suggest results as you type, you'll generally search by prefix. In this case the order of attributes is very important to decide which hit is the best:
```java
System.out.println(index.search(new Query("or")));
System.out.println(index.search(new Query("jim")));
```




Online Documentation
----------------

Check our [online documentation](http://www.algolia.com/doc):
 * [Initial Import](http://www.algolia.com/doc#InitialImport)
 * [Ranking &amp; Relevance](http://www.algolia.com/doc#RankingRelevance)
 * [Settings](http://www.algolia.com/doc#Settings)
 * [Search](http://www.algolia.com/doc#Search)
 * [Incremental Updates](http://www.algolia.com/doc#IncrementalUpdates)
 * [Reindexing](http://www.algolia.com/doc#Reindexing)
 * [Numeric-Search](http://www.algolia.com/doc#Numeric-Search)
 * [Category-Search](http://www.algolia.com/doc#Category-Search)
 * [Faceting](http://www.algolia.com/doc#Faceting)
 * [Geo-Search](http://www.algolia.com/doc#Geo-Search)
 * [Security](http://www.algolia.com/doc#Security)
 * [Indexing Several Types](http://www.algolia.com/doc#IndexingSeveralTypes)
 * [REST API](http://www.algolia.com/doc/rest)







Add a new object in the Index
-------------

Each entry in an index has a unique identifier called `objectID`. You have two ways to add en entry in the index:

 1. Using automatic `objectID` assignement, you will be able to retrieve it in the answer.
 2. Passing your own `objectID`

You don't need to explicitely create an index, it will be automatically created the first time you add an object.
Objects are schema less, you don't need any configuration to start indexing. The settings section provide details about advanced settings.

Example with automatic `objectID` assignement:

```java
JSONObject obj = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger"));
System.out.println(obj.getString("objectID"));
```

Example with manual `objectID` assignement:

```java
JSONObject obj = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger"), "myID");
System.out.println(obj.getString("objectID"));
```

Update an existing object in the Index
-------------

You have two options to update an existing object:

 1. Replace all its attributes.
 2. Replace only some attributes.

Example to replace all the content of an existing object:

```java
index.saveObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("city", "New York"), "myID");
```

Example to update only the city attribute of an existing object:

```java
index.partialUpdateObject(new JSONObject().put("city", "San Francisco"), "myID");
```

Search
-------------
 **Opening note:** If you are building a web application, you may be more interested in using our [javascript client](https://github.com/algolia/algoliasearch-client-js) to send queries. It brings two benefits: (i) your users get a better response time by avoiding to go through your servers, and (ii) it will offload your servers of unnecessary tasks.

To perform a search, you just need to initialize the index and perform a call to the search function.

You can use the following optional arguments:

### Query parameters

#### Full Text Search parameters

 * **setQueryString**: (string) The instant-search query string, all words of the query are interpreted as prefixes (for example "John Mc" will match "John Mccamey" and "Johnathan Mccamey"). If no query parameter is set, retrieves all objects.
 * **setQueryType**: select how the query words are interpreted, it can be one of the following value:
  * **PREFIX_ALL**: all query words are interpreted as prefixes,
  * **PREFIX_ALL**: only the last word is interpreted as a prefix (default behavior),
  * **PREFIX_NONE**: no query word is interpreted as a prefix. This option is not recommended.
 * **setOptionalWords**: a string that contains the list of words that should be considered as optional when found in the query. The list of words is comma separated.
 * **setMinWordSizeToAllowOneTypo**: the minimum number of characters in a query word to accept one typo in this word.<br/>Defaults to 3.
 * **setMinWordSizeToAllowTwoTypos**: the minimum number of characters in a query word to accept two typos in this word.<br/>Defaults to 7.

#### Pagination parameters

 * **setPage**: (integer) Pagination parameter used to select the page to retrieve.<br/>Page is zero-based and defaults to 0. Thus, to retrieve the 10th page you need to set `page=9`
 * **setHitsPerPage**: (integer) Pagination parameter used to select the number of hits per page. Defaults to 20.

#### Geo-search parameters

 * **aroundLatitudeLongitude(float, float, int)**: search for entries around a given latitude/longitude.<br/>You specify the maximum distance in meters with the **radius** parameter (in meters).<br/>At indexing, you should specify geoloc of an object with the _geoloc attribute (in the form `{"_geoloc":{"lat":48.853409, "lng":2.348800}}`)
 * **aroundLatitudeLongitude(flot, float, int, int)**: search for entries around a given latitude/longitude with a given precision for ranking (for example if you set precision=100, two objects that are distant of less than 100m will be considered as identical for "geo" ranking parameter).

 * **insideBoundingBox**: search entries inside a given area defined by the two extreme points of a rectangle (defined by 4 floats: p1Lat,p1Lng,p2Lat,p2Lng).<br/>For example `insideBoundingBox=47.3165,4.9665,47.3424,5.0201`).<br/>At indexing, you should specify geoloc of an object with the _geoloc attribute (in the form `{"_geoloc":{"lat":48.853409, "lng":2.348800}}`)

#### Parameters to control results content

 * **setAttributesToRetrieve**: The list of object attributes you want to retrieve (let you minimize the answer size). By default, all attributes are retrieved. You can also use `*` to retrieve all values when an **attributesToRetrieve** setting is specified for your index.
 * **setAttributesToHighlight**: The list of attributes you want to highlight according to the query. If an attribute has no match for the query, the raw value is returned. By default all indexed text attributes are highlighted. You can use `*` if you want to highlight all textual attributes. Numerical attributes are not highlighted. A matchLevel is returned for each highlighted attribute and can contain:
  * **full**: if all the query terms were found in the attribute,
  * **partial**: if only some of the query terms were found,
  * **none**: if none of the query terms were found.
 * **setAttributesToSnippet**: The list of attributes to snippet alongside the number of words to return (syntax is `attributeName:nbWords`). By default no snippet is computed.
 * **getRankingInfo**: if set to true, the result hits will contain ranking information in **_rankingInfo** attribute.


#### Numeric search parameters
 * **setNumericFilters**: a string that contains the list of numeric filters you want to apply separated by a comma. The syntax of one filter is `attributeName` followed by `operand` followed by `value`. Supported operands are `<`, `<=`, `=`, `>` and `>=`. 
 You can have multiple conditions on one attribute like for example `numericFilters=price>100,price<1000`. You can also use a string array encoding (for example `numericFilters: ["price>100","price<1000"]`).

#### Category search parameters
 * **setTagFilters**: filter the query by a set of tags. You can AND tags by separating them by commas. To OR tags, you must add parentheses. For example, `tags=tag1,(tag2,tag3)` means *tag1 AND (tag2 OR tag3)*. You can also use a string array encoding, for example `tagFilters: ["tag1",["tag2","tag3"]]` means *tag1 AND (tag2 OR tag3)*.<br/>At indexing, tags should be added in the **_tags** attribute of objects (for example `{"_tags":["tag1","tag2"]}`). 

#### Faceting parameters
 * **setFacetFilters**: filter the query by a list of facets. Facets are separated by commas and each facet is encoded as `attributeName:value`. To OR facets, you must add parentheses. For example: `facetFilters=(category:Book,category:Movie),author:John%20Doe`. You can also use a string array encoding (for example `[["category:Book","category:Movie"],"author:John%20Doe"]`).
 * **setFacets**: List of object attributes that you want to use for faceting. <br/>Attributes are separated with a comma (for example `"category,author"` ). You can also use a JSON string array encoding (for example `["category","author"]` ). Only attributes that have been added in **attributesForFaceting** index setting can be used in this parameter. You can also use `*` to perform faceting on all attributes specified in **attributesForFaceting**.

#### Distinct parameter
 * **setDistinct**: If set to true, enable the distinct feature (disabled by default) if the `attributeForDistinct` index setting is set. This feature is similar to the SQL "distinct" keyword: when enabled in a query with the `distinct=1` parameter, all hits containing a duplicate value for the attributeForDistinct attribute are removed from results. For example, if the chosen attribute is `show_name` and several hits have the same value for `show_name`, then only the best one is kept and others are removed.

```java
Index index = client.initIndex("contacts");
System.out.println(index.search(new Query("query string")));
System.out.println(index.search(new Query("query string").
             setAttributesToRetrieve(Arrays.asList("firstname", "lastname")).
             setNbHitsPerPage(50)));
```

The server response will look like:

```javascript
{
  "hits": [
    {
      "firstname": "Jimmie",
      "lastname": "Barninger",
      "objectID": "433",
      "_highlightResult": {
        "firstname": {
          "value": "<em>Jimmie</em>",
          "matchLevel": "partial"
        },
        "lastname": {
          "value": "Barninger",
          "matchLevel": "none"
        },
        "company": {
          "value": "California <em>Paint</em> & Wlpaper Str",
          "matchLevel": "partial"
        }
      }
    }
  ],
  "page": 0,
  "nbHits": 1,
  "nbPages": 1,
  "hitsPerPage": 20,
  "processingTimeMS": 1,
  "query": "jimmie paint",
  "params": "query=jimmie+paint&attributesToRetrieve=firstname,lastname&hitsPerPage=50"
}
```

Get an object
-------------

You can easily retrieve an object using its `objectID` and optionnaly a list of attributes you want to retrieve (using comma as separator):

```java
// Retrieves all attributes
index.getObject("myID");
// Retrieves only the firstname attribute
index.getObject("myID", Arrays.asList("firstname"));
```

Delete an object
-------------

You can delete an object using its `objectID`:

```java
index.deleteObject("myID");
```

Index Settings
-------------

You can retrieve all settings using the `getSettings` function. The result will contains the following attributes:


#### Indexing parameters
 * **attributesToIndex**: (array of strings) the list of fields you want to index.<br/>If set to null, all textual and numerical attributes of your objects are indexed, but you should update it to get optimal results.<br/>This parameter has two important uses:
  * *Limit the attributes to index*.<br/>For example if you store a binary image in base64, you want to store it and be able to retrieve it but you don't want to search in the base64 string.
  * *Control part of the ranking*.<br/>(see the ranking parameter for full explanation) Matches in attributes at the beginning of the list will be considered more important than matches in attributes further down the list. In one attribute, matching text at the beginning of the attribute will be considered more important than text after, you can disable this behavior if you add your attribute inside `unordered(AttributeName)`, for example `attributesToIndex: ["title", "unordered(text)"]`.
 * **attributesForFaceting**: (array of strings) The list of fields you want to use for faceting. All strings in the attribute selected for faceting are extracted and added as a facet. If set to null, no attribute is used for faceting.
 * **attributeForDistinct**: The attribute name used for the `Distinct` feature. This feature is similar to the SQL "distinct" keyword: when enabled in query with the `distinct=1` parameter, all hits containing a duplicate value for this attribute are removed from results. For example, if the chosen attribute is `show_name` and several hits have the same value for `show_name, then only the best one is kept and others are removed.
 * **ranking**: (array of strings) controls the way results are sorted.<br/>We have six available criteria: 
  * **typo**: sort according to number of typos,
  * **geo**: sort according to decreassing distance when performing a geo-location based search,
  * **proximity**: sort according to the proximity of query words in hits,
  * **attribute**: sort according to the order of attributes defined by attributesToIndex,
  * **exact**: 
    * if the user query contains one word: sort objects having an attribute that is exactly the query word before others. For example if you search for the "V" TV show, you want to find it with the "V" query and avoid to have all popular TV show starting by the v letter before it.
    * if the user query contains multiple words: sort according to the number of words that matched exactly (and not as a prefix).
  * **custom**: sort according to a user defined formula set in **customRanking** attribute.<br/>The standard order is ["typo", "geo", "proximity", "attribute", "exact", "custom"]
 * **customRanking**: (array of strings) lets you specify part of the ranking.<br/>The syntax of this condition is an array of strings containing attributes prefixed by asc (ascending order) or desc (descending order) operator.
For example `"customRanking" => ["desc(population)", "asc(name)"]`  
 * **queryType**: Select how the query words are interpreted, it can be one of the following value:
  * **prefixAll**: all query words are interpreted as prefixes,
  * **prefixLast**: only the last word is interpreted as a prefix (default behavior),
  * **prefixNone**: no query word is interpreted as a prefix. This option is not recommended.
 * **slaves**: The list of indexes on which you want to replicate all write operations. In order to get response times in milliseconds, we pre-compute part of the ranking during indexing. If you want to use different ranking configurations depending of the use-case, you need to create one index per ranking configuration. This option enables you to perform write operations only on this index, and to automatically update slave indexes with the same operations.

#### Default query parameters (can be overwrite by query)
 * **minWordSizefor1Typo**: (integer) the minimum number of characters to accept one typo (default = 3).
 * **minWordSizefor2Typos**: (integer) the minimum number of characters to accept two typos (default = 7).
 * **hitsPerPage**: (integer) the number of hits per page (default = 10).
 * **attributesToRetrieve**: (array of strings) default list of attributes to retrieve in objects. If set to null, all attributes are retrieved.
 * **attributesToHighlight**: (array of strings) default list of attributes to highlight. If set to null, all indexed attributes are highlighted.
 * **attributesToSnippet**: (array of strings) default list of attributes to snippet alongside the number of words to return (syntax is 'attributeName:nbWords')<br/>By default no snippet is computed. If set to null, no snippet is computed.
 * **highlightPreTag**: (string) Specify the string that is inserted before the highlighted parts in the query result (default to "&lt;em&gt;").
 * **highlightPostTag**: (string) Specify the string that is inserted after the highlighted parts in the query result (default to "&lt;/em&gt;").
 * **optionalWords**: (array of strings) Specify a list of words that should be considered as optional when found in the query.

You can easily retrieve settings or update them:


```java
System.out.println(index.getSettings());
```

```java
index.setSettings(new JSONObject().append("customRanking", "desc(followers)"));
```

List indexes
-------------
You can list all your indexes with their associated information (number of entries, disk size, etc.) with the `listIndexes` method:

```java
client.listIndexes();
```

Delete an index
-------------
You can delete an index using its name:

```java
client.deleteIndex("contacts");
```

Clear an index
-------------
You can delete the index content without removing settings and index specific API keys with the clearIndex command:

```java
index.clearIndex();
```

Wait indexing
-------------

All write operations return a `taskID` when the job is securely stored on our infrastructure but not when the job is published in your index. Even if it's extremely fast, you can easily ensure indexing is complete using the `waitTask` method on the `taskID` returned by a write operation. 

For example, to wait for indexing of a new object:
```java
JSONObject res = index.addObject(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
index.waitTask(String.valueOf(res.getLong("objectID")));
```


If you want to ensure multiple objects have been indexed, you can only check the biggest taskID.

Batch writes
-------------

You may want to perform multiple operations with one API call to reduce latency.
We expose three methods to perform batch:
 * `addObjects`: add an array of object using automatic `objectID` assignement
 * `saveObjects`: add or update an array of object that contains an `objectID` attribute
 * `partialUpdateObjects`: partially update an array of objects that contain an `objectID` attribute (only specified attributes will be updated, other will remain unchanged)

Example using automatic `objectID` assignement:
```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
index.addObjects(array);
```

Example with user defined `objectID` (add or update):
```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "SFO"));
array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "LA"));
index.saveObjects(array);
```

Example that update only the `firstname` attribute:
```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("objectID", "SFO"));
array.add(new JSONObject().put("firstname", "Warren").put("objectID", "LA"));
index.partialUpdateObjects(array);
```



Security / User API Keys
-------------

The admin API key provides full control of all your indexes. 
You can also generate user API keys to control security. 
These API keys can be restricted to a set of operations or/and restricted to a given index.

To list existing keys, you can use `listUserKeys` method:
```java
// Lists global API Keys
client.listUserKeys();
// Lists API Keys that can access only to this index
index.listUserKeys();
```

Each key is defined by a set of rights that specify the authorized actions. The different rights are:
 * **search**: allows to search,
 * **browse**: allow to retrieve all index content via the browse API,
 * **addObject**: allows to add/update an object in the index,
 * **deleteObject**: allows to delete an existing object,
 * **deleteIndex**: allows to delete index content,
 * **settings**: allows to get index settings,
 * **editSettings**: allows to change index settings.

Example of API Key creation:
```java
// Creates a new global API key that can only perform search actions
JSONObject res = client.addUserKey(Arrays.asList("search"));
System.out.println("Key: " + res.getString("key"));
// Creates a new API key that can only perform search action on this index
JSONObject res = index.addUserKey(Arrays.asList("search"));
System.out.println("Key: " + res.getString("key"));
```

You can also create an API Key with advanced restrictions:

 * Add a validity period: the key will be valid only for a specific period of time (in seconds),
 * Specify the maximum number of API calls allowed from an IP address per hour. Each time an API call is performed with this key, a check is performed. If the IP at the origin of the call did more than this number of calls in the last hour, a 403 code is returned. Defaults to 0 (no rate limit). This parameter can be used to protect you from attempts at retrieving your entire content by massively querying the index.


  Note: If you are sending the query through your servers, you must use the `enableRateLimitForward("TheAdminAPIKey", "EndUserIP", "APIKeyWithRateLimit")` function to enable rate-limit.

 * Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited). This parameter can be used to protect you from attempts at retrieving your entire content by massively querying the index.

```java
// Creates a new global API key that is valid for 300 seconds
JSONObject res = client.addUserKey(Arrays.asList("search"), 300, 0, 0);
System.out.println("Key: " + res.getString("key"));
// Creates a new index specific API key valid for 300 seconds, with a rate limit of 100 calls per hour per IP and a maximum of 20 hits
JSONObject res = index.addUserKey(Arrays.asList("search"), 300, 100, 20);
System.out.println("Key: " + res.getString("key"));
```

Get the rights of a given key:
```java
// Gets the rights of a global key
client.getUserKeyACL("f420238212c54dcfad07ea0aa6d5c45f");
// Gets the rights of an index specific key
index.getUserKeyACL("71671c38001bf3ac857bc82052485107");
```

Delete an existing key:
```java
// Deletes a global key
client.deleteUserKey("f420238212c54dcfad07ea0aa6d5c45f");
// Deletes an index specific key
index.deleteUserKey("71671c38001bf3ac857bc82052485107");
```

Copy or rename an index
-------------

You can easily copy or rename an existing index using the `copy` and `move` commands.
**Note**: Move and copy commands overwrite destination index.

```java
// Rename MyIndex in MyIndexNewName
client.moveIndex("MyIndex", "MyIndexNewName");
// Copy MyIndex in MyIndexCopy
client.copyIndex("MyIndex", "MyIndexCopy");
```

The move command is particularly useful is you want to update a big index atomically from one version to another. For example, if you recreate your index `MyIndex` each night from a database by batch, you just have to:
 1. Import your database in a new index using [batches](#batch-writes). Let's call this new index `MyNewIndex`.
 1. Rename `MyNewIndex` in `MyIndex` using the move command. This will automatically override the old index and new queries will be served on the new one.

```java
// Rename MyNewIndex in MyIndex (and overwrite it)
client.moveIndex("MyNewIndex", "MyIndex");
```

Backup / Retrieve all index content
-------------

You can retrieve all index content for backup purpose of for analytics using the browse method. 
This method retrieve 1000 objects by API call and support pagination.

```java
// Get first page
index.browse(0);
// Get second page
index.browse(1);
```

Logs
-------------

You can retrieve the last logs via this API. Each log entry contains: 
 * Timestamp in ISO-8601 format
 * Client IP
 * Request Headers (API-Key is obfuscated)
 * Request URL
 * Request method
 * Request body
 * Answer HTTP code
 * Answer body
 * SHA1 ID of entry

You can retrieve the logs of your last 1000 API calls and browse them using the offset/length parameters:
 * ***offset***: Specify the first entry to retrieve (0-based, 0 is the most recent log entry). Default to 0.
 * ***length***: Specify the maximum number of entries to retrieve starting at offset. Defaults to 10. Maximum allowed value: 1000.

```java
// Get last 10 log entries
client.getLogs();
// Get last 100 log entries
client.getLogs(0, 100);
```





