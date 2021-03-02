package com.digitalpetri.modbus.responses;

import com.digitalpetri.modbus.FunctionCode;

/**
 * 02 4C 00 05 00 03 00 00
 * 02 siteId
 * 4c  cmd
 * 00 05 manufacturer
 * 00 03 equipmentSerialNumber
 * 00 00 crc or Reserved(equipment type)
 * @author yhw
 */
public class HeartbeatResponse extends  SimpleModbusResponse {

    private final int manufacturer;
    private final int equipmentSerialNumber;

    /**
     * @param manufacturer  0x0000 to 0xFFFF (0 to 65535)
     * @param equipmentSerialNumber 0x0000 to 0xFFFF (0 to 65535)
     */
    public HeartbeatResponse(int manufacturer, int equipmentSerialNumber) {
        super(FunctionCode.Heartbeat);

        this.manufacturer = manufacturer;
        this.equipmentSerialNumber = equipmentSerialNumber;
    }

    public int getManufacturer() {
        return manufacturer;
    }

    public int getEquipmentSerialNumber() {
        return equipmentSerialNumber;
    }
}
