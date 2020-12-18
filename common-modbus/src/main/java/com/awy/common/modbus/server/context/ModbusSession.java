package com.awy.common.modbus.server.context;

import lombok.Data;
import lombok.ToString;

/**
 * @author yhw
 */
@ToString
@Data
public final class ModbusSession {

    private int siteId;

    private int manufacturer;

    private int equipmentSerialNumber;

    private ModbusSession(){
    }

    public ModbusSession(int siteId,int manufacturer,int equipmentSerialNumber){
        this.siteId = siteId;
        this.manufacturer = manufacturer;
        this.equipmentSerialNumber = equipmentSerialNumber;
    }

    public final static String getSessionId(int manufacturer,int equipmentSerialNumber){
        String manufacturerHex = Integer.toHexString(manufacturer);
        String equipmentSerialNumberHex = Integer.toHexString(equipmentSerialNumber);
        return manufacturerHex.concat(equipmentSerialNumberHex);
    }


}
