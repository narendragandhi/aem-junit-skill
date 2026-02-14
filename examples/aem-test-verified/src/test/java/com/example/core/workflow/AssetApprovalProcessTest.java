package com.example.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.metadata.SimpleMetaDataMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Tests for AssetApprovalProcess workflow
 * Demonstrates testing custom workflow process steps with:
 * - Mocked WorkItem, WorkflowSession, WorkflowData
 * - Real AemContext for resource operations
 * - MetaDataMap for process arguments
 */
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AssetApprovalProcessTest {

    private final AemContext context = new AemContext();

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private WorkflowData workflowData;

    private AssetApprovalProcess process;

    @BeforeEach
    void setUp() {
        process = new AssetApprovalProcess();

        // Create test asset structure
        context.create().resource("/content/dam/test-assets/image.jpg",
            "jcr:primaryType", "dam:Asset");
        context.create().resource("/content/dam/test-assets/image.jpg/jcr:content",
            "jcr:primaryType", "dam:AssetContent");
        context.create().resource("/content/dam/test-assets/image.jpg/jcr:content/metadata",
            "jcr:primaryType", "nt:unstructured",
            "dc:title", "Test Image");

        // Setup workflow mocks with lenient() to avoid UnnecessaryStubbingException
        lenient().when(workItem.getWorkflowData()).thenReturn(workflowData);
        lenient().when(workflowData.getPayload()).thenReturn("/content/dam/test-assets/image.jpg");
        lenient().when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(context.resourceResolver());
    }

    @Test
    void testApproveAsset() throws WorkflowException {
        MetaDataMap metaData = new SimpleMetaDataMap();
        metaData.put("PROCESS_ARGS", "action=approve");

        process.execute(workItem, workflowSession, metaData);

        // Verify asset status was updated
        Resource assetResource = context.resourceResolver().getResource("/content/dam/test-assets/image.jpg");
        String status = process.getStatus(assetResource);
        assertEquals("approved", status);
    }

    @Test
    void testRejectAsset() throws WorkflowException {
        MetaDataMap metaData = new SimpleMetaDataMap();
        metaData.put("PROCESS_ARGS", "action=reject");

        process.execute(workItem, workflowSession, metaData);

        // Verify asset status was updated
        Resource assetResource = context.resourceResolver().getResource("/content/dam/test-assets/image.jpg");
        String status = process.getStatus(assetResource);
        assertEquals("rejected", status);
    }

    @Test
    void testDefaultActionIsApprove() throws WorkflowException {
        // Empty metadata should default to approve
        MetaDataMap metaData = new SimpleMetaDataMap();

        process.execute(workItem, workflowSession, metaData);

        Resource assetResource = context.resourceResolver().getResource("/content/dam/test-assets/image.jpg");
        String status = process.getStatus(assetResource);
        assertEquals("approved", status);
    }

    @Test
    void testAssetNotFoundThrowsException() {
        when(workflowData.getPayload()).thenReturn("/content/dam/nonexistent/asset.jpg");

        MetaDataMap metaData = new SimpleMetaDataMap();
        metaData.put("PROCESS_ARGS", "action=approve");

        assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, metaData);
        });
    }

    @Test
    void testNullResolverThrowsException() {
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(null);

        MetaDataMap metaData = new SimpleMetaDataMap();

        assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, metaData);
        });
    }

    @Test
    void testGetStatusReturnsNullForAssetWithoutMetadata() {
        // Create asset without metadata node
        context.create().resource("/content/dam/no-metadata/image.jpg",
            "jcr:primaryType", "dam:Asset");

        Resource assetResource = context.resourceResolver().getResource("/content/dam/no-metadata/image.jpg");
        String status = process.getStatus(assetResource);

        assertNull(status);
    }
}
