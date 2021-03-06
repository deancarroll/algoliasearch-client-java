package com.algolia.search.saas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;

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

public class Query {
    public enum QueryType
    {
    	/// all query words are interpreted as prefixes.
    	PREFIX_ALL,
    	/// only the last word is interpreted as a prefix (default behavior).
    	PREFIX_LAST,
    	/// no query word is interpreted as a prefix. This option is not recommended.
    	PREFIX_NONE
    }
    
    protected List<String> attributes;
    protected List<String> attributesToHighlight;
    protected List<String> attributesToSnippet;
    protected int minWordSizeForApprox1;
    protected int minWordSizeForApprox2;
    protected boolean getRankingInfo;
    protected boolean distinct;
    protected int page;
    protected int hitsPerPage;
    protected String tags;
    protected String numerics;
    protected String insideBoundingBox;
    protected String aroundLatLong;
    protected String query;
    protected QueryType queryType;
    protected String optionalWords;
    protected String facets;
    protected String facetsFilter;
    protected int maxNumberOfFacets;
    
    public Query(String query) {
        minWordSizeForApprox1 = 3;
        minWordSizeForApprox2 = 7;
        getRankingInfo = false;
        distinct = false;
        page = 0;
        hitsPerPage = 20;
        this.query = query;
        queryType = QueryType.PREFIX_LAST;
        maxNumberOfFacets = -1;
    }
    
    public Query() {
        minWordSizeForApprox1 = 3;
        minWordSizeForApprox2 = 7;
        getRankingInfo = false;
        distinct = false;
        page = 0;
        hitsPerPage = 20;
        queryType = QueryType.PREFIX_ALL;
        maxNumberOfFacets = -1;
    }
    
    /**
     *  Select how the query words are interpreted:
     */
    public Query setQueryType(QueryType type)
    {
    	this.queryType = type;
    	return this;
    }
    
    /**
     * Set the full text query
     */
    public Query setQueryString(String query)
    {
        this.query = query;
        return this;
    }
    
    /**
     * Specify the list of attribute names to retrieve. 
     * By default all attributes are retrieved.
     */
    public Query setAttributesToRetrieve(List<String> attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * Specify the list of attribute names to highlight. 
     * By default indexed attributes are highlighted.
     */
    public Query setAttributesToHighlight(List<String> attributes) {
        this.attributesToHighlight = attributes;
        return this;
    }

    /**
     * Specify the list of attribute names to Snippet alongside the number of words to return (syntax is 'attributeName:nbWords').
     * By default no snippet is computed.
     */
    public Query setAttributesToSnippet(List<String> attributes) {
        this.attributesToSnippet = attributes;
        return this;
    }
    
    /**
     * 
     * @param If set to true, enable the distinct feature (disabled by default) if the attributeForDistinct index setting is set. 
     *   This feature is similar to the SQL "distinct" keyword: when enabled in a query with the distinct=1 parameter, 
     *   all hits containing a duplicate value for the attributeForDistinct attribute are removed from results. 
     *   For example, if the chosen attribute is show_name and several hits have the same value for show_name, then only the best 
     *   one is kept and others are removed.
     */
    public Query enableDistinct(boolean distinct) {
    	this.distinct = distinct;
    	return this;
    }
    
    /**
     * Specify the minimum number of characters in a query word to accept one typo in this word. 
     * Defaults to 3.
     */
    public Query setMinWordSizeToAllowOneTypo(int nbChars) {
        minWordSizeForApprox1 = nbChars;
        return this;
    }
    
    /**
     * Specify the minimum number of characters in a query word to accept two typos in this word. 
     * Defaults to 7.
     */
    public Query setMinWordSizeToAllowTwoTypos(int nbChars) {
        minWordSizeForApprox2 = nbChars;
        return this;
    }

    /**
     * if set, the result hits will contain ranking information in _rankingInfo attribute.
     */
    public Query getRankingInfo(boolean enabled) {
        getRankingInfo = enabled;
        return this;
    }

    /**
     * Set the page to retrieve (zero base). Defaults to 0.
     */
    public Query setPage(int page) {
        this.page = page;
        return this;
    }
    
    /**
     *  Set the number of hits per page. Defaults to 10.
     */
    public Query setHitsPerPage(int nbHitsPerPage) {
        this.hitsPerPage = nbHitsPerPage;
        return this;
    }
    
    /**
     *  Set the number of hits per page. Defaults to 10.
     *  @deprecated Use {@code setHitsPerPage}
     */
    @Deprecated
    public Query setNbHitsPerPage(int nbHitsPerPage) {
        return setHitsPerPage(nbHitsPerPage);
    }

    /**
     *  Search for entries around a given latitude/longitude. 
     *  @param radius set the maximum distance in meters.
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitude(float latitude, float longitude, int radius) {
        aroundLatLong = "aroundLatLng=" + latitude + "," + longitude + "&aroundRadius=" + radius;
        return this;
    }
    
    /**
     *  Search for entries around a given latitude/longitude. 
     *  @param radius set the maximum distance in meters.
     *  @param precision set the precision for ranking (for example if you set precision=100, two objects that are distant of less than 100m will be considered as identical for "geo" ranking parameter).
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitude(float latitude, float longitude, int radius, int precision) {
        aroundLatLong = "aroundLatLng=" + latitude + "," + longitude + "&aroundRadius=" + radius + "&aroundPrecision=" + precision;
        return this;
    }
    
    /**
     *  Search for entries inside a given area defined by the two extreme points of a rectangle.
     *    At indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query insideBoundingBox(float latitudeP1, float longitudeP1, float latitudeP2, float longitudeP2) {
        insideBoundingBox = "insideBoundingBox=" + latitudeP1 + "," + longitudeP1 + "," + latitudeP2 + "," + longitudeP2;
        return this;
    }
    
    /**
     * Set the list of words that should be considered as optional when found in the query. 
     * @param words The list of optional words, comma separated.
     */
    public Query setOptionalWords(String words) {
    	this.optionalWords = words;
    	return this;
    }
    
    /**
     * Set the list of words that should be considered as optional when found in the query. 
     * @param words The list of optional words.
     */
    public Query setOptionalWords(List<String> words) {
    	StringBuilder builder = new StringBuilder();
    	for (String word : words) {
    		builder.append(word);
    		builder.append(",");
    	}
    	this.optionalWords = builder.toString();
    	return this;
    }
 
    /**
     * Filter the query by a list of facets. Each facet is encoded as `attributeName:value`. For example: `["category:Book","author:John%20Doe"].
     */
    public Query setFacetFilters(List<String> facets) {
    	JSONArray obj = new JSONArray();
    	for (String facet : facets) {
    		obj.put(facet);
    	}
    	this.facetsFilter = obj.toString();
    	return this;
    }
    
    /**
     * List of object attributes that you want to use for faceting. <br/>
     * Only attributes that have been added in **attributesForFaceting** index setting can be used in this parameter. 
     * You can also use `*` to perform faceting on all attributes specified in **attributesForFaceting**.
     */
    public Query setFacets(List<String> facets) {
    	JSONArray obj = new JSONArray();
    	for (String facet : facets) {
    		obj.put(facet);
    	}
    	this.facets = obj.toString();
    	return this;
    }
    
    /**
     * Limit the number of facet values returned for each facet.
     */
    public Query setMaxNumberOfFacets(int n) {
        this.maxNumberOfFacets = n;
        return this;
    }

    /**
     * Filter the query by a set of tags. You can AND tags by separating them by commas. To OR tags, you must add parentheses. For example tag1,(tag2,tag3) means tag1 AND (tag2 OR tag3).
     * At indexing, tags should be added in the _tags attribute of objects (for example {"_tags":["tag1","tag2"]} )
     */
    public Query setTagFilters(String tags) {
        this.tags = tags;
        return this;
    }
    
    /**
     * Add a list of numeric filters separated by a comma. 
     * The syntax of one filter is `attributeName` followed by `operand` followed by `value. Supported operands are `<`, `<=`, `=`, `>` and `>=`. 
     * You can have multiple conditions on one attribute like for example `numerics=price>100,price<1000`.
     */
    public Query setNumericFilters(String numerics) {
    	this.numerics = numerics;
    	return this;
    }
    
    /**
     * Add a list of numeric filters separated by a comma. 
     * The syntax of one filter is `attributeName` followed by `operand` followed by `value. Supported operands are `<`, `<=`, `=`, `>` and `>=`. 
     * You can have multiple conditions on one attribute like for example `numerics=price>100,price<1000`.
     */
    public Query setNumericFilters(List<String> numerics) {
    	StringBuilder builder = new StringBuilder();
    	boolean first = true;
    	for (String n : numerics) {
    		if (!first)
    			builder.append(",");
    		builder.append(n);
    		first = false;
    	}
    	this.numerics = builder.toString();
    	return this;
    }
    
    protected String getQueryString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            if (attributes != null) {
                stringBuilder.append("attributes=");
                boolean first = true;
                for (String attr : this.attributes) {
                    if (!first)
                        stringBuilder.append(",");
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (attributesToHighlight != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("attributesToHighlight=");
                boolean first = true;
                for (String attr : this.attributesToHighlight) {
                    if (!first)
                        stringBuilder.append(',');
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (attributesToSnippet != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("attributesToSnippet=");
                boolean first = true;
                for (String attr : this.attributesToSnippet) {
                    if (!first)
                        stringBuilder.append(',');
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (minWordSizeForApprox1 != 3) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("minWordSizefor1Typo=");
                stringBuilder.append(minWordSizeForApprox1);
            }
            if (minWordSizeForApprox2 != 7) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("minWordSizefor2Typos=");
                stringBuilder.append(minWordSizeForApprox2);
            }
            if (getRankingInfo) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("getRankingInfo=1");
            }
            if (distinct) {
            	if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("distinct=1");
            }
            if (page > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("page=");
                stringBuilder.append(page);
            }
            if (hitsPerPage != 20 && hitsPerPage > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("hitsPerPage=");
                stringBuilder.append(hitsPerPage);
            }
            if (tags != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("tagFilters=");
                stringBuilder.append(URLEncoder.encode(tags, "UTF-8"));
            }
            if (numerics != null) {
            	if (stringBuilder.length() > 0)
            		stringBuilder.append('&');
            	stringBuilder.append("numericFilters=");
            	stringBuilder.append(URLEncoder.encode(numerics, "UTF-8"));
            }
            if (insideBoundingBox != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append(insideBoundingBox);
            } else if (aroundLatLong != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append(aroundLatLong);
            }
            if (query != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("query=");
                stringBuilder.append(URLEncoder.encode(query, "UTF-8"));
            }
            if (facets != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("facets=");
                stringBuilder.append(URLEncoder.encode(facets, "UTF-8"));
            }
            if (facetsFilter != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("facetFilters=");
                stringBuilder.append(URLEncoder.encode(facetsFilter, "UTF-8"));
            }
            if (maxNumberOfFacets > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("maxNumberOfFacets=");
                stringBuilder.append(maxNumberOfFacets);
            }
            if (optionalWords != null) {
            	if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("optionalWords=");
                stringBuilder.append(URLEncoder.encode(optionalWords, "UTF-8"));
            }
            switch (queryType) {
            case PREFIX_ALL:
            	if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("queryType=prefixAll");
            	break;
            case PREFIX_LAST:
            	break;
            case PREFIX_NONE:
            	if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("queryType=prefixNone");
            	break;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}
