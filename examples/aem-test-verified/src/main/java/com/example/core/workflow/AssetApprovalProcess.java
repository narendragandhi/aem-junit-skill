package com.example.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class, property = {
    "process.label=Asset Approval Process"
})
public class AssetApprovalProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(AssetApprovalProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {

        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        String action = metaDataMap.get("PROCESS_ARGS", "action=approve");

        ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
        if (resolver == null) {
            throw new WorkflowException("Cannot obtain ResourceResolver");
        }

        Resource assetResource = resolver.getResource(payloadPath);
        if (assetResource == null) {
            throw new WorkflowException("Asset not found: " + payloadPath);
        }

        try {
            Resource metadataResource = assetResource.getChild("jcr:content/metadata");
            if (metadataResource != null) {
                ModifiableValueMap properties = metadataResource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    if (action.contains("approve")) {
                        properties.put("dam:status", "approved");
                        LOG.info("Asset approved: {}", payloadPath);
                    } else if (action.contains("reject")) {
                        properties.put("dam:status", "rejected");
                        LOG.info("Asset rejected: {}", payloadPath);
                    }
                    resolver.commit();
                }
            }
        } catch (PersistenceException e) {
            throw new WorkflowException("Failed to update asset metadata", e);
        }
    }

    public String getStatus(Resource assetResource) {
        Resource metadataResource = assetResource.getChild("jcr:content/metadata");
        if (metadataResource != null) {
            return metadataResource.getValueMap().get("dam:status", String.class);
        }
        return null;
    }
}
