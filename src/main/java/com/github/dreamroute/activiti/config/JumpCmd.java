package com.github.dreamroute.activiti.config;

import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:04
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@AllArgsConstructor
public class JumpCmd implements Command<Void> {

    private String taskId;
    private String targetNodeId;

    @Override
    public Void execute(CommandContext commandContext) {
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        // 执行实例 id
        String executionId = taskEntity.getExecutionId();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        HistoryManager historyManager = commandContext.getHistoryManager();
        // 执行实例对象
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        FlowElement flowElement = process.getFlowElement(targetNodeId);
        if (flowElement == null) {
            throw new RuntimeException("目标节点不存在");
        }
        // 如果该节点是多实例 userTask
//        if (flowElement instanceof UserTask && ((UserTask) flowElement).getLoopCharacteristics() != null) {
//            String name = flowElement.getName();
//            String[] split = name.split(":");
//            Integer ver = Integer.valueOf(split[1]);
//            flowElement.setName(split[0] + ":" + (ver + 1));
//        }
        // 将历史活动表更新
        historyManager.recordActivityEnd(executionEntity, "jump");
        // 设置当前流程
        executionEntity.setCurrentFlowElement(flowElement);
        // 触发执行实例运转
        agenda.planContinueProcessInCompensation(executionEntity);
        // 从runtime 表中删除当前任务
        taskEntityManager.delete(taskId);
        // 将历史任务表更新, 历史任务标记为完成
        historyManager.recordTaskEnd(taskId, "jump");
        return null;
    }
}
