package com.github.dreamroute.activiti.guide;

import com.github.dreamroute.activiti.config.StartProcessInstance;
import com.github.dreamroute.activiti.sdk.UserTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
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

    // 加签
    @Test
    public void sdkTest() {
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        log.info("process definition id is: {}", processDefinition.getId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        String id = task.getId();
        System.err.println(id);

        UserTaskUtil.addUserTaskAfterTask(task, processEngine, "Zhangsan", "新增任务");

        taskService.claim(task.getId(), "Lisi");
        taskService.complete(task.getId());

        processEngine.getManagementService().executeCommand(commandContext -> {
            DeploymentManager deploymentManager = commandContext.getProcessEngineConfiguration().getDeploymentManager();
            deploymentManager.setProcessDefinitionCache(null);
            return null;
        });

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.claim(task.getId(), "Zhangsan");
        taskService.complete(task.getId());

        restart(processDefinition.getId());

    }

    private void restart(String processDefinitionId) {

        ProcessInstance processInstance = processEngine.getManagementService().executeCommand(new StartProcessInstance(processDefinitionId, processEngine));

//        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

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


















