///*
// * Copyright 2016 Kevin Herron
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.awy.common.modbus.server.rtu;
//
//import com.digitalpetri.modbus.ExceptionCode;
//import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
//import com.digitalpetri.modbus.requests.*;
//import com.digitalpetri.modbus.responses.*;
//import io.netty.channel.Channel;
//import io.netty.util.ReferenceCountUtil;
//
//public interface ServiceRequestRtuHandler_bak {
//
//    default void onReadHoldingRegisters(ServiceRequestRtu<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onReadInputRegisters(ServiceRequestRtu<ReadInputRegistersRequest, ReadInputRegistersResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onReadCoils(ServiceRequestRtu<ReadCoilsRequest, ReadCoilsResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onReadDiscreteInputs(ServiceRequestRtu<ReadDiscreteInputsRequest, ReadDiscreteInputsResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onWriteSingleCoil(ServiceRequestRtu<WriteSingleCoilRequest, WriteSingleCoilResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onWriteSingleRegister(ServiceRequestRtu<WriteSingleRegisterRequest, WriteSingleRegisterResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onWriteMultipleCoils(ServiceRequestRtu<WriteMultipleCoilsRequest, WriteMultipleCoilsResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onWriteMultipleRegisters(ServiceRequestRtu<WriteMultipleRegistersRequest, WriteMultipleRegistersResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onMaskWriteRegister(ServiceRequestRtu<MaskWriteRegisterRequest, MaskWriteRegisterResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onReadWriteMultipleRegisters(ServiceRequestRtu<ReadWriteMultipleRegistersRequest, ReadWriteMultipleRegistersResponse> service) {
//        service.sendException(ExceptionCode.IllegalFunction);
//        ReferenceCountUtil.release(service.getRequest());
//    }
//
//    default void onEquipmentRegisterRequest(ModbusRtuPayload payload) {
//    }
//
//    default void onHeartbeatRequest(ModbusRtuPayload payload) {
//    }
//
//
//    interface ServiceRequestRtu<Request extends ModbusRequest, Response extends ModbusResponse> {
//
//        int getSiteId();
//
//        /**
//         * @return the request to service.
//         */
//        Request getRequest();
//
//        /**
//         * @return the {@link Channel} this request was received on.
//         */
//        Channel getChannel();
//
//        /**
//         * Send a normal response.
//         *
//         * @param response the service response
//         */
//        void sendResponse(Response response);
//
//        /**
//         * Send an exception response.
//         *
//         * @param exceptionCode the {@link ExceptionCode}
//         */
//        void sendException(ExceptionCode exceptionCode);
//
//    }
//
//}
