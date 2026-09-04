package com.example.core.search;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Session;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility to help mock QueryBuilder results in AEM JUnit tests.
 */
public class QueryBuilderMockHelper {
    
    public static void mockQueryResult(QueryBuilder queryBuilder, List<Resource> results) {
        Query query = mock(Query.class);
        SearchResult result = mock(SearchResult.class);
        
        when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getResources()).thenReturn(results.iterator());
        when(result.getTotalMatches()).thenReturn((long) results.size());
    }
}
