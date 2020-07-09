package com.github.dreamroute.activiti.guide;

import com.github.dreamroute.activiti.sdk.UserTaskUtil;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SdkTest {
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
    public void sdkTest() {
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        UserTaskUtil.addUserTaskAfterTask(task, processInstance, processEngine, "Zhangsan", "新增任务");

        taskService.claim(task.getId(), "Lisi");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());
    }

}
