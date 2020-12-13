package com.digitalpetri.modbus.codec.rtu;

import com.digitalpetri.modbus.ModbusPdu;

/**
 * @author yhw
 */
public class ModbusRtuPayload {
    /**
     * 站点id(地址)
     */
    private final int siteId;
    /**
     * Modbus 包内容
     */
    private final ModbusPdu modbusPdu;

    public ModbusRtuPayload(int siteId, ModbusPdu modbusPdu) {
        this.siteId = siteId;
        this.modbusPdu = modbusPdu;
    }


    public int getSiteId() {
        return siteId;
    }

    public ModbusPdu getModbusPdu() {
        return modbusPdu;
    }

}
