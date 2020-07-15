package com.github.dreamroute.activiti.guide;

import com.github.dreamroute.activiti.config.JumpCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest
public class AddSign {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;

    @Test
    public void hardTest() {
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        log.info("process definition id is: {}", processDefinition.getId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        Task currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        String currentTaskId = currentTask.getId();
        String nextTaskId = "_5";

        testAddOneTask(currentTaskId, nextTaskId);

        currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(currentTask.getId());

        currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(currentTask.getId());

    }

    public void testAddOneTask(String currentTaskId, String nextTaskId) {
        // 获取当前的任务
        Task currentTask = taskService.createTaskQuery().taskId(currentTaskId).singleResult();
        log.info("taskEntity: {}", currentTask);
        String processDefinitionId = currentTask.getProcessDefinitionId();
        ManagementService managementService = processEngine.getManagementService();
        org.activiti.bpmn.model.Process process = managementService.executeCommand(commandContext -> {
            BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
            return bpmnModel.getProcesses().get(0);
        });

        // 创建新节点
        UserTask userTask = new UserTask();
        userTask.setId("destinyD");
        userTask.setName("加签节点 destinyD");
        userTask.setAssignee("destiny-d");
        userTask.setBehavior(createUserTaskBehavior(userTask, processEngine));

        // 新节点的目标连线
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("extra");
        userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
        sequenceFlow.setTargetFlowElement(process.getFlowElement(nextTaskId));
        sequenceFlow.setTargetRef(nextTaskId);

        process.addFlowElement(userTask);
        process.addFlowElement(sequenceFlow);

        ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService.executeCommand(commandContext -> {
            DeploymentManager deploymentManager = commandContext.getProcessEngineConfiguration().getDeploymentManager();
            return deploymentManager.getProcessDefinitionCache().get(processDefinitionId);
        });
        processDefinitionCacheEntry.setProcess(process);

        // 跳转
        managementService.executeCommand(new JumpCmd(currentTaskId, userTask.getId()));
    }

    /**
     * 设置UserTask行为
     */
    private static UserTaskActivityBehavior createUserTaskBehavior(UserTask userTask, ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }

}
