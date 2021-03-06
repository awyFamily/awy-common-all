package com.digitalpetri.modbus.responses;

import com.digitalpetri.modbus.FunctionCode;

/**
 * @author yhw
 */
public class RegistersAuthResponse extends  SimpleModbusResponse {

    private final int manufacturer;
    private final int equipmentSerialNumber;

    /**
     * @param manufacturer  0x0000 to 0xFFFF (0 to 65535)
     * @param equipmentSerialNumber 0x0000 to 0xFFFF (0 to 65535)
     */
    public RegistersAuthResponse(int manufacturer, int equipmentSerialNumber) {
        super(FunctionCode.EquipmentRegister);

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
