package com.awy.common.ws.netty.context;

import com.awy.common.ws.netty.process.CmdProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yhw
 */
public class ProcessManager {

    private Map<Byte, CmdProcess> cmdRepository = new HashMap<>();

    public static ProcessManager getInstance(){
        return Holder.manager;
    }

    public CmdProcess getCmdProcess(byte code){
        return cmdRepository.get(code);
    }


    /**
     * 批量添加(code 不允许重复)
     * @param processList
     */
    public void addCmdProcessList(List<CmdProcess> processList){
        if(processList != null && !processList.isEmpty()){
            for (CmdProcess process : processList) {
                cmdRepository.put(process.getCmdCode(),process);
            }
        }
    }

    public void addCmdProcess(CmdProcess process){
        cmdRepository.put(process.getCmdCode(),process);
    }

    static class Holder{
        private static ProcessManager manager = new ProcessManager();
    }
}
