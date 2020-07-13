package com.github.dreamroute.activiti.config;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cmd.StartProcessInstanceCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

public class StartProcessInstance extends StartProcessInstanceCmd<ProcessInstance> {

    private ProcessEngine processEngine;

    public StartProcessInstance(String processDefinitionId, ProcessEngine processEngine) {
        super(processDefinitionId, processDefinitionId, null, null);
        this.processEngine = processEngine;
    }

    public ProcessInstance execute(CommandContext commandContext) {
        ProcessDefinition processDefinition1 = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        ProcessDefinition processDefinition = processEngine.getRepositoryService().createNativeProcessDefinitionQuery()
                .sql("SELECT * FROM ACT_RE_PROCDEF WHERE ID_ = #{processDefinitionId}")
                .parameter("processDefinitionId", processDefinitionId)
                .singleResult();
        processInstanceHelper = commandContext.getProcessEngineConfiguration().getProcessInstanceHelper();
        ProcessInstance processInstance = createAndStartProcessInstance(processDefinition, businessKey, processInstanceName, variables, transientVariables);
        return processInstance;
    }
}
