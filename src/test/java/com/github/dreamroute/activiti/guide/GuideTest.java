package com.github.dreamroute.activiti.guide;

import com.github.dreamroute.activiti.domain.User;
import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class GuideTest {

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
    public void engineTest() {
        System.err.println(processEngine);
        System.err.println(runtimeService);
        System.err.println(repositoryService);
        System.err.println(taskService);
        System.err.println(managementService);
        System.err.println(identityService);
        System.err.println(historyService);
        System.err.println(formService);
        DynamicBpmnService dynamicService = processEngine.getDynamicBpmnService();
        System.err.println(dynamicService);
    }

    @Test
    public void deploymentTest() {
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.name("vacation").key("vacation");
        Deployment deploy = builder.addClasspathResource("processes/DD.bpmn").deploy();
        System.err.println("id: " + deploy.getId() + ", name: " + deploy.getName() + ", key: " + deploy.getKey());
    }

    @Test
    public void startProcessTest() {
        runtimeService.startProcessInstanceByKey("myProcess");
        List<Task> tasks = taskService.createTaskQuery().list();
        System.err.println(tasks);
    }

    @Test
    public void queryTest() {
        List<ProcessDefinition> proDefList = repositoryService.createProcessDefinitionQuery().list();
        AtomicInteger count = new AtomicInteger(0);
        if (proDefList != null && !proDefList.isEmpty()) {
            proDefList.forEach(pro -> {
                System.err.println(pro);
                count.getAndIncrement();
            });
        }
        System.err.println(count.get());
    }

    @Test
    public void processPriviligeTest() {
        String id = repositoryService.createDeployment().addClasspathResource("processes/DD.bpmn").deploy().getId();
        System.err.println(id);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(id).singleResult();
        repositoryService.addCandidateStarterUser(processDefinition.getId(), "F91");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "F91-Group");
    }

    @Test
    public void saveTaskTest() {
        Task task1 = taskService.newTask();
        taskService.saveTask(task1);
        Task task2 = taskService.newTask("task-name");
        taskService.saveTask(task2);
    }

    @Test
    public void deleteTaskTest() {
        taskService.deleteTask("task-name");
    }

    @Test
    public void taskVariableTest() {
        Task task = taskService.newTask("mm");
        taskService.saveTask(task);
        taskService.setVariable(task.getId(), "name", "w.dehai");
    }

    @Test
    public void taskVariableSerializableTest() {
        Task task = taskService.newTask("seri2");
        taskService.saveTask(task);
        taskService.setVariable(task.getId(), "user", new User(100L, "w.dehai"));
        System.err.println(task.getId());
    }

    @Test
    public void getTaskVariableTest() {
        Object variable = taskService.getVariable("seri2", "user");
        System.err.println(variable);
    }

    @Test
    public void setTaskLocalVariableTest() {
        Task task = taskService.newTask();
        taskService.saveTask(task);
        taskService.setVariableLocal(task.getId(), "name", "w.dehai");
    }

    @Test
    public void moreTest() {
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/ddd.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.err.println(tasks);
    }

}






























