//package com.awy.common.modbus.server.rtu;
//
//import com.awy.common.modbus.server.ModbusRtuSlave;
//import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
//import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
//import com.digitalpetri.modbus.slave.ServiceRequestHandler;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.PooledByteBufAllocator;
//import io.netty.util.ReferenceCountUtil;
//
//import java.util.concurrent.ExecutionException;
//
//public class ModBusRtuSlaveServer  {
//
//    private final ModbusRtuSlave slave = new ModbusRtuSlave();
//
//
//    public void start() throws ExecutionException, InterruptedException {
//        slave.setRequestHandler(new ServiceRequestHandler() {
//            @Override
//            public void onReadHoldingRegisters(ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
//                String clientRemoteAddress = service.getChannel().remoteAddress().toString();
//                String clientIp = clientRemoteAddress.replaceAll(".*/(.*):.*", "$1");
//                String clientPort = clientRemoteAddress.replaceAll(".*:(.*)", "$1");
//
//                ReadHoldingRegistersRequest request = service.getRequest();
//
//                ByteBuf registers = PooledByteBufAllocator.DEFAULT.buffer(request.getQuantity());
//
//                for (int i = 0; i < request.getQuantity(); i++) {
//                    registers.writeShort(i);
//                }
//
//                service.sendResponse(new ReadHoldingRegistersResponse(registers));
//
//                ReferenceCountUtil.release(request);
//            }
//        });
//
//        slave.bind("localhost", 50200).get();
//    }
//
//    public void stop() {
//        slave.shutdown();
//    }
//}