package com.github.dreamroute.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ActivitiApplicationTests {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @Test
    public void contextLoads() {
        
    }
    
    @Test
    public void createUserTest() {
        User user = identityService.newUser("周杰伦");
        user.setLastName("Jay");
        identityService.saveUser(user);
        
        Group group = identityService.newGroup("music");
        group.setName("music");
        identityService.saveGroup(group);
    }

    @Test
    public void deploymentTest() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn20.xml");
        String id = deploymentBuilder.name("m01").key("m01").deploy().getId();
        System.err.println(id);
    }

    @Test
    public void startProcessTest() {
        ProcessInstance m01 = runtimeService.startProcessInstanceByKey("qingjia");
        System.err.println(m01.getId());

        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(m01.getId()).list();
        System.err.println(list);
    }

    @Test
    public void taskTest() {
        List<Task> wangdehai = taskService.createTaskQuery().taskAssignee("wangdehai").list();
        System.err.println(wangdehai);
    }


}
