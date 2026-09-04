package com.example.core.search;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SearchServiceTest {

    private final AemContext context = new AemContext();

    @Mock
    private QueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        context.registerService(QueryBuilder.class, queryBuilder);
    }

    @Test
    void testSearchWithMockResults() {
        // Create some mock resources
        Resource res1 = context.create().resource("/content/mysite/page1");
        Resource res2 = context.create().resource("/content/mysite/page2");
        List<Resource> mockResults = List.of(res1, res2);

        // Use our helper to mock QueryBuilder
        QueryBuilderMockHelper.mockQueryResult(queryBuilder, mockResults);

        Query query = queryBuilder.createQuery(new PredicateGroup(), mock(Session.class));
        SearchResult searchResult = query.getResult();
        Iterator<Resource> resources = searchResult.getResources();

        assertEquals(2, searchResult.getTotalMatches());
        assertEquals(res1, resources.next());
        assertEquals(res2, resources.next());
    }
}
