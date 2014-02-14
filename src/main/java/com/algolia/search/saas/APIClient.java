package com.algolia.search.saas;

import com.google.appengine.api.urlfetch.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2013 Algolia
 * http://www.algolia.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Entry point in the Java API.
 * You should instantiate a Client object with your ApplicationID, ApiKey and Hosts
 * to start using Algolia Search API
 */
public class APIClient {
  private final static int HTTP_TIMEOUT_MS = 30000;
  private final String applicationID;
  private final String apiKey;
  private final List<String> hostsArray;
  private final URLFetchService service = URLFetchServiceFactory.getURLFetchService();
  private String forwardRateLimitAPIKey;
  private String forwardEndUserIP;
  private String forwardAdminAPIKey;

  /**
   * Algolia Search initialization
   *
   * @param applicationID the application ID you have in your admin interface
   * @param apiKey        a valid API key for the service
   */
  public APIClient(String applicationID, String apiKey) {
    this(applicationID, apiKey, Arrays.asList(applicationID + "-1.algolia.io",
        applicationID + "-2.algolia.io",
        applicationID + "-3.algolia.io"));
  }

  /**
   * Algolia Search initialization
   *
   * @param applicationID the application ID you have in your admin interface
   * @param apiKey        a valid API key for the service
   * @param hostsArray    the list of hosts that you have received for the service
   */
  public APIClient(String applicationID, String apiKey, List<String> hostsArray) {
    forwardRateLimitAPIKey = forwardAdminAPIKey = forwardEndUserIP = null;
    if (applicationID == null || applicationID.length() == 0) {
      throw new RuntimeException("AlgoliaSearch requires an applicationID.");
    }
    this.applicationID = applicationID;
    if (apiKey == null || apiKey.length() == 0) {
      throw new RuntimeException("AlgoliaSearch requires an apiKey.");
    }
    this.apiKey = apiKey;
    if (hostsArray == null || hostsArray.size() == 0) {
      throw new RuntimeException("AlgoliaSearch requires a list of hostnames.");
    }
    // randomize elements of hostsArray (act as a kind of load-balancer)
    Collections.shuffle(hostsArray);
    this.hostsArray = hostsArray;
  }

  /**
   * Allow to use IP rate limit when you have a proxy between end-user and Algolia.
   * This option will set the X-Forwarded-For HTTP header with the client IP and the X-Forwarded-API-Key with the API Key having rate limits.
   *
   * @param adminAPIKey     the admin API Key you can find in your dashboard
   * @param endUserIP       the end user IP (you can use both IPV4 or IPV6 syntax)
   * @param rateLimitAPIKey the API key on which you have a rate limit
   */
  public void enableRateLimitForward(String adminAPIKey, String endUserIP, String rateLimitAPIKey) {
    this.forwardAdminAPIKey = adminAPIKey;
    this.forwardEndUserIP = endUserIP;
    this.forwardRateLimitAPIKey = rateLimitAPIKey;
  }

  /**
   * Disable IP rate limit enabled with enableRateLimitForward() function
   */
  public void disableRateLimitForward() {
    forwardAdminAPIKey = forwardEndUserIP = forwardRateLimitAPIKey = null;
  }

  /**
   * List all existing indexes
   * return an JSON Object in the form:
   * { "items": [ {"name": "contacts", "createdAt": "2013-01-18T15:33:13.556Z"},
   * {"name": "notes", "createdAt": "2013-01-18T15:33:13.556Z"}]}
   */
  public JSONObject listIndexes() throws AlgoliaException {
    return getRequest("/1/indexes/");
  }

  /**
   * Delete an index
   *
   * @param indexName the name of index to delete
   *                  return an object containing a "deletedAt" attribute
   */
  public JSONObject deleteIndex(String indexName) throws AlgoliaException {
    try {
      return deleteRequest("/1/indexes/" + URLEncoder.encode(indexName, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    }
  }

  /**
   * Move an existing index.
   *
   * @param srcIndexName the name of index to copy.
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   */
  public JSONObject moveIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("operation", "move");
      content.put("destination", dstIndexName);
      return postRequest("/1/indexes/" + URLEncoder.encode(srcIndexName, "UTF-8") + "/operation", content.toString());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage()); // $COVERAGE-IGNORE$
    }
  }

  /**
   * Copy an existing index.
   *
   * @param srcIndexName the name of index to copy.
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   */
  public JSONObject copyIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("operation", "copy");
      content.put("destination", dstIndexName);
      return postRequest("/1/indexes/" + URLEncoder.encode(srcIndexName, "UTF-8") + "/operation", content.toString());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage()); // $COVERAGE-IGNORE$
    }
  }

  /**
   * Return 10 last log entries.
   */
  public JSONObject getLogs() throws AlgoliaException {
    return getRequest("/1/logs");
  }

  /**
   * Return last logs entries.
   *
   * @param offset Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   */
  public JSONObject getLogs(int offset, int length) throws AlgoliaException {
    return getRequest("/1/logs?offset=" + offset + "&length=" + length);
  }

  /**
   * Get the index object initialized (no server call needed for initialization)
   *
   * @param indexName the name of index
   */
  public Index initIndex(String indexName) {
    return new Index(this, indexName);
  }

  /**
   * List all existing user keys with their associated ACLs
   */
  public JSONObject listUserKeys() throws AlgoliaException {
    return getRequest("/1/keys");
  }

  /**
   * Get ACL of a user key
   */
  public JSONObject getUserKeyACL(String key) throws AlgoliaException {
    return getRequest("/1/keys/" + key);
  }

  /**
   * Delete an existing user key
   */
  public JSONObject deleteUserKey(String key) throws AlgoliaException {
    return deleteRequest("/1/keys/" + key);
  }

  /**
   * Create a new user key
   *
   * @param acls the list of ACL for this key. Defined by an array of strings that
   *             can contains the following values:
   *             - search: allow to search (https and http)
   *             - addObject: allows to add/update an object in the index (https only)
   *             - deleteObject : allows to delete an existing object (https only)
   *             - deleteIndex : allows to delete index content (https only)
   *             - settings : allows to get index settings (https only)
   *             - editSettings : allows to change index settings (https only)
   */
  public JSONObject addUserKey(List<String> acls) throws AlgoliaException {
    JSONArray array = new JSONArray(acls);
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("acl", array);
    } catch (JSONException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    }
    return postRequest("/1/keys", jsonObject.toString());
  }

  /**
   * Create a new user key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   */
  public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    JSONArray array = new JSONArray(acls);
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("acl", array);
      jsonObject.put("validity", validity);
      jsonObject.put("maxQueriesPerIPPerHour", maxQueriesPerIPPerHour);
      jsonObject.put("maxHitsPerQuery", maxHitsPerQuery);

    } catch (JSONException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    }
    return postRequest("/1/keys", jsonObject.toString());
  }

  protected JSONObject getRequest(String url) throws AlgoliaException {
    return _request(Method.GET, url, null);
  }

  protected JSONObject deleteRequest(String url) throws AlgoliaException {
    return _request(Method.DELETE, url, null);
  }

  protected JSONObject postRequest(String url, String obj) throws AlgoliaException {
    return _request(Method.POST, url, obj);
  }

  protected JSONObject putRequest(String url, String obj) throws AlgoliaException {
    return _request(Method.PUT, url, obj);
  }

  private JSONObject _request(Method m, String url, String json) throws AlgoliaException {


    // for each host
    for (String host : this.hostsArray) {
      HTTPRequest req;
      URL reqUrl = null;
      try {
        reqUrl = new URL("https://" + host + url);
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
      switch (m) {
        case DELETE:
          req = new HTTPRequest(reqUrl, HTTPMethod.DELETE);
          break;
        case GET:
          req = new HTTPRequest(reqUrl, HTTPMethod.GET);
          break;
        case POST:
          req = new HTTPRequest(reqUrl, HTTPMethod.POST);
          break;
        case PUT:
          req = new HTTPRequest(reqUrl, HTTPMethod.PUT);
          break;
        default:
          throw new IllegalArgumentException("Method " + m + " is not supported");
      }
      // set auth headers

      req.addHeader(new HTTPHeader("X-Algolia-Application-Id", this.applicationID));
      if (forwardAdminAPIKey == null) {
        req.addHeader(new HTTPHeader("X-Algolia-API-Key", this.apiKey));
      } else {
        req.addHeader(new HTTPHeader("X-Algolia-API-Key", this.forwardAdminAPIKey));
        req.addHeader(new HTTPHeader("X-Forwarded-For", this.forwardEndUserIP));
        req.addHeader(new HTTPHeader("X-Forwarded-API-Key", this.forwardRateLimitAPIKey));
      }

      if (json != null) {
        req.setPayload(json.getBytes());
        req.setHeader(new HTTPHeader("Content-type", "application/json"));
      }

      req.getFetchOptions().setDeadline(30D);

      HTTPResponse response;
      try {
        response = service.fetch(req);
      } catch (IOException e) {
        // on error continue on the next host
        continue;
      }

      int code = response.getResponseCode();
      if (code == 403) {
        throw new AlgoliaException("Invalid Application-ID or API-Key");
      }
      if (code == 404) {
        throw new AlgoliaException("Resource does not exist");
      }
      if (code == 503) {
        continue;
      }

      try {
        InputStream istream = new ByteArrayInputStream(response.getContent());
        InputStreamReader is = new InputStreamReader(istream, "UTF-8");
        JSONTokener tokener = new JSONTokener(is);
        JSONObject res = new JSONObject(tokener);
        is.close();
        return res;
      } catch (IOException e) {
        continue;
      } catch (JSONException e) {
        //System.out.println(response.getEntity().getContent().read);
        throw new AlgoliaException("JSON decode error:" + e.getMessage());
      }
    }
    throw new AlgoliaException("Hosts unreachable");
  }

  private static enum Method {
    GET, POST, PUT, DELETE, OPTIONS, TRACE, HEAD;
  }
}
