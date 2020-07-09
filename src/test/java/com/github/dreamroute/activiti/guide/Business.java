package com.github.dreamroute.activiti.guide;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
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
import java.util.Collection;

@Slf4j
@SpringBootTest
public class Business {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;

    @Test
    public void createUsersTest() {
        User leader = identityService.newUser("Leader");
        leader.setFirstName("Zhang");
        leader.setLastName("san");
        leader.setEmail("zhangsan@bdfint.com");
        leader.setPassword("zhangsan");
        identityService.saveUser(leader);

        User employee = identityService.newUser("employee");
        employee.setFirstName("Li");
        employee.setLastName("si");
        employee.setEmail("lisi@bdfint.com");
        employee.setPassword("lisi");
        identityService.saveUser(employee);

    }

    @Test
    public void workflowTest() {
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        log.info("taskId: {}", task.getId());

        String processDefinitionId = task.getProcessDefinitionId();
        Process process = managementService.executeCommand(commandContext -> {
            BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
            return bpmnModel.getProcesses().get(0);
        });

        Collection<FlowElement> flowElements = process.getFlowElements();
        SequenceFlow sf = needAddTaskSequenceFlow(process, task);
        FlowElement targetFlowElement = sf.getTargetFlowElement();
        String targetId = targetFlowElement.getId();

        SequenceFlow flow = new SequenceFlow();
        flow.setId("zj");
        flow.setName("zj");
        flow.setTargetFlowElement(process.getFlowElement(targetId));
        flow.setTargetRef(targetId);

        UserTask userTask = new UserTask();
        userTask.setId("zjid");
        userTask.setName("zjname");
        userTask.setAssignee("Zhangsan");
        userTask.setBehavior(createUserTaskBehavior(userTask, processEngine));
        userTask.setOutgoingFlows(Arrays.asList(flow));

        process.addFlowElement(userTask);
        process.addFlowElement(flow);

        sf.setTargetRef("zjid");
        sf.setTargetFlowElement(userTask);

        ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService.executeCommand(commandContext -> {
            DeploymentManager deploymentManager = commandContext.getProcessEngineConfiguration().getDeploymentManager();
            return deploymentManager.getProcessDefinitionCache().get(processDefinitionId);
        });

        processDefinitionCacheEntry.setProcess(process);

        taskService.claim(task.getId(), "Lisi");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());

        System.err.println("END");

    }

    /**
     * 获取参数中Task的右边连线
     */
    private static SequenceFlow needAddTaskSequenceFlow(Process process, Task task) {
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement element: flowElements) {
            if (element instanceof SequenceFlow) {
                SequenceFlow sf = (SequenceFlow) element;
                if (sf.getSourceRef().equals(task.getTaskDefinitionKey())) {
                    return sf;
                }
            }
        }
        throw new RuntimeException("无法在此加签!");
    }

    /**
     * 设置UserTask行为
     */
    private static UserTaskActivityBehavior createUserTaskBehavior(UserTask userTask, ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration();
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        UserTaskActivityBehavior userTaskActivityBehavior = activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
        return userTaskActivityBehavior;
    }

}





















