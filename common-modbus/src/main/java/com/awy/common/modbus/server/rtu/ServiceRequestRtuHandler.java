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

package com.awy.common.modbus.server.rtu;

import com.awy.common.modbus.server.context.ModbusSession;
import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.requests.*;
import com.digitalpetri.modbus.responses.*;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

//import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;

public interface ServiceRequestRtuHandler {

    default void onReadHoldingRegisters(ServiceRequestRtu<ReadHoldingRegistersResponse, ReadHoldingRegistersRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onReadInputRegisters(ServiceRequestRtu<ReadInputRegistersResponse, ReadInputRegistersRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onReadCoils(ServiceRequestRtu<ReadCoilsResponse, ReadCoilsRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onReadDiscreteInputs(ServiceRequestRtu<ReadDiscreteInputsResponse, ReadDiscreteInputsRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onWriteSingleCoil(ServiceRequestRtu<WriteSingleCoilResponse, WriteSingleCoilRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onWriteSingleRegister(ServiceRequestRtu<WriteSingleRegisterResponse, WriteSingleRegisterRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onWriteMultipleCoils(ServiceRequestRtu<WriteMultipleCoilsResponse, WriteMultipleCoilsRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onWriteMultipleRegisters(ServiceRequestRtu<WriteMultipleRegistersResponse, WriteMultipleRegistersRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onMaskWriteRegister(ServiceRequestRtu<MaskWriteRegisterResponse, MaskWriteRegisterRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

    default void onReadWriteMultipleRegisters(ServiceRequestRtu<ReadWriteMultipleRegistersResponse, ReadWriteMultipleRegistersRequest> service) {
        service.sendException(ExceptionCode.IllegalFunction);
        ReferenceCountUtil.release(service.getRequest());
    }

//    default void onEquipmentRegisterRequest(RegistersAuthResponse registersAuthResponse) {
//    }

    default void onHeartbeatRequest(int siteId,HeartbeatResponse heartbeatResponse) {
    }

    default void onOffline(ModbusSession modbusSession) {
    }


    //    interface ServiceRequestRtu<Request extends ModbusRequest, Response extends ModbusResponse> {
    interface ServiceRequestRtu<Request extends ModbusResponse, Response extends ModbusRequest> {

        int getSiteId();

        /**
         * @return the request to service.
         */
        Request getRequest();

        /**
         * @return the {@link Channel} this request was received on.
         */
        Channel getChannel();

        /**
         * Send a normal response.
         *
         * @param response the service response
         */
        void sendResponse(Response response);

        /**
         * Send an exception response.
         *
         * @param exceptionCode the {@link ExceptionCode}
         */
        void sendException(ExceptionCode exceptionCode);

    }

}
