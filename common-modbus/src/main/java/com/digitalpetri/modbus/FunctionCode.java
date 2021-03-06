/*
 * Copyright 2016 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpetri.modbus;

import java.util.Optional;

public enum FunctionCode {
    //读线圈
    ReadCoils(0x01),
    //读取离散输入响应
    ReadDiscreteInputs(0x02),
    //读保持寄存器
    ReadHoldingRegisters(0x03),
    //读输入寄存器
    ReadInputRegisters(0x04),
    //写单线圈
    WriteSingleCoil(0x05),
    //写单个寄存器
    WriteSingleRegister(0x06),
    ReadExceptionStatus(0x07),
    Diagnostics(0x08),
    GetCommEventCounter(0x0B),
    //通讯事件日志
    GetCommEventLog(0x0C),
    //写多个线圈
    WriteMultipleCoils(0x0F),
    //写多个寄存器
    WriteMultipleRegisters(0x10),
    ReportSlaveId(0x11),
    ReadFileRecord(0x14),
    WriteFileRecord(0x15),
    MaskWriteRegister(0x16),
    ReadWriteMultipleRegisters(0x17),
    ReadFifoQueue(0x18),
    EncapsulatedInterfaceTransport(0x2B),
    //=========== 非标准规则 ===================
    //注册包规则 字节1(站点)  字节2(命令码) 字节3-4(厂商) 字节5-6(设备序列号) 7-8(预留)
    //注册包(需要和厂商单独确认-定义好为 0x4C)   ****
    EquipmentRegister(0x4C),
    //字节1(站点)  字节2(命令码) 字节3-4(厂商) 字节5-6(设备序列号) 7-8(预留)
    //心跳(需要和厂商单独确认-定义好为 0x4D)   *****
    Heartbeat(0x4D),
    //忽略的包
    IgnorePackage(0x00),
    //============================================
    ;

    private final int code;

    FunctionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<FunctionCode> fromCode(int code) {
        switch(code) {
            case 0x01: return Optional.of(ReadCoils);
            case 0x02: return Optional.of(ReadDiscreteInputs);
            case 0x03: return Optional.of(ReadHoldingRegisters);
            case 0x04: return Optional.of(ReadInputRegisters);
            case 0x05: return Optional.of(WriteSingleCoil);
            case 0x06: return Optional.of(WriteSingleRegister);
            case 0x07: return Optional.of(ReadExceptionStatus);
            case 0x08: return Optional.of(Diagnostics);
            case 0x0B: return Optional.of(GetCommEventCounter);
            case 0x0C: return Optional.of(GetCommEventLog);
            case 0x0F: return Optional.of(WriteMultipleCoils);
            case 0x10: return Optional.of(WriteMultipleRegisters);
            case 0x11: return Optional.of(ReportSlaveId);
            case 0x14: return Optional.of(ReadFileRecord);
            case 0x15: return Optional.of(WriteFileRecord);
            case 0x16: return Optional.of(MaskWriteRegister);
            case 0x17: return Optional.of(ReadWriteMultipleRegisters);
            case 0x18: return Optional.of(ReadFifoQueue);
            case 0x2B: return Optional.of(EncapsulatedInterfaceTransport);
            case 0x4C: return Optional.of(EquipmentRegister);
            case 0x4D: return Optional.of(Heartbeat);
            case 0x00: return Optional.of(IgnorePackage);
        }

        return Optional.empty();
    }

    public static boolean isExceptionCode(int code) {
        return fromCode(code - 0x80).isPresent();
    }

}
